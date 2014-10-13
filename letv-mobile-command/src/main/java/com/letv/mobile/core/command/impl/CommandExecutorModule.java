/**
 * 
 */
package com.letv.mobile.core.command.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.letv.mobile.core.async.api.IAsyncCallable;
import com.letv.mobile.core.async.api.IAsyncCallback;
import com.letv.mobile.core.async.api.ICancellable;
import com.letv.mobile.core.command.annotation.ConstraintLiteral;
import com.letv.mobile.core.command.api.ICommand;
import com.letv.mobile.core.command.api.ICommandExecutionContext;
import com.letv.mobile.core.command.api.ICommandExecutor;
import com.letv.mobile.core.command.api.ICommandHandler;
import com.letv.mobile.core.command.api.ICommandValidator;
import com.letv.mobile.core.command.api.UnsupportedCommandException;
import com.letv.mobile.core.log.api.Trace;
import com.letv.mobile.core.microkernel.api.AbstractModule;
import com.letv.mobile.core.microkernel.api.IKernelContext;
import com.letv.mobile.core.rpc.http.api.IRestProxyService;

/**
 * @author  
 *
 */
public class CommandExecutorModule<T extends IKernelContext> extends AbstractModule<T> implements
		ICommandExecutor {
	private static final Trace log = Trace.getLogger("com.wxxr.mobile.core.command.CommandExecutor");
	private static class DummyCallback<V> implements IAsyncCallback<V> {

		@Override
		public void success(V result) {
		}

		@Override
		public void failed(Throwable cause) {
		}

		@Override
		public void cancelled() {
		}

		@Override
		public void setCancellable(ICancellable cancellable) {
		}
		
	}
	
	private class TimeoutChecker implements Runnable {
		private Thread thread;
		private boolean keepAlive;
		
		@Override
		public void run() {
			this.thread = Thread.currentThread();
			while(keepAlive){
				try {
					if(!taskQueue.isEmpty()){
						CancellableTask<?>[] tasks = getWaitingTasks();
						if(tasks != null){
							for (CancellableTask<?> task : tasks) {
								task.cancelIfTimeout();
							}
						}
					}
				}catch(Throwable t){
					log.error("Caught throwable inside main loop of Command timeout checker", t);
				}
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
				}
			}
		}
		
		public void start() {
			this.keepAlive = true;
			new Thread(this,"Command Timeout Checker").start();
		}
		
		public void stop() {
			this.keepAlive = false;
			if((this.thread != null)&&this.thread.isAlive()){
				this.thread.interrupt();
				try {
					this.thread.join(2000L);
				} catch (InterruptedException e) {
				}
			}
			this.thread = null;
		}
		
	}
	private abstract class CancellableTask<V> implements Runnable,ICancellable {
		private final long timestamp = System.currentTimeMillis();
		private volatile boolean cancelled, running;
		
		public CancellableTask(IAsyncCallback<V> cb){
			cb.setCancellable(this);
		}
		
		@Override
		public void cancel() {
			if(this.running == false){
				this.cancelled = true;
				removeSumittedTask(this);
				getCallback().cancelled();
			}
		}

		@Override
		public boolean isCancelled() {
			return this.cancelled;
		}
		
		@Override
		public void run() {
			if(cancelled){
				return;
			}
			this.running = true;
			doExecute();
		}

		public void cancelIfTimeout() {
			if(this.running){
				removeFromWaitingList(this);
			}else{
				int secondsElapsed = (int)((System.currentTimeMillis() - this.timestamp)/1000);
				if(secondsElapsed >= timeoutInSeconds){
					cancel();
				}
			}
		}

		protected abstract IAsyncCallback<?> getCallback();
		
		protected abstract void doExecute();

	}
	private class CommandTask<S>  extends CancellableTask<S> {
		private final ICommand<S> command;
		private final IAsyncCallback<S> callback;
		
		public CommandTask(ICommand<S> cmd, IAsyncCallback<S> cb){
			super(cb);
			this.command = cmd;
			this.callback = cb;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void doExecute() {
			this.callback.setCancellable(null);
			ICommandHandler<S, ICommand<S>> handler = null;
			synchronized(handlers){
				handler = (ICommandHandler<S, ICommand<S>>) handlers.get(command.getCommandName());
			}
			handler.execute(command, callback);
		}

		@Override
		protected IAsyncCallback<?> getCallback() {
			return this.callback;
		}
	}
	
	private class RunnableTask extends CancellableTask<Object> {
		private final Runnable runnable;
		private final IAsyncCallback<Object> callback;
		
		public RunnableTask(Runnable task, IAsyncCallback<Object> cb){
			super(cb);
			this.runnable = task;
			this.callback = cb;
		}

		@Override
		protected void doExecute() {
			if(this.runnable instanceof ICancellable){
				this.callback.setCancellable((ICancellable)this.runnable);
			}
			try {
				this.runnable.run();
				this.callback.success(null);
			}catch(Throwable t){
				this.callback.failed(t);
			}
		}

		@Override
		protected IAsyncCallback<?> getCallback() {
			return this.callback;
		}
	}

	private class CallableTask<S> extends CancellableTask<S> {
		private final Callable<S> command;
		private final IAsyncCallback<S> callback;
		
		public CallableTask(Callable<S> cmd, IAsyncCallback<S> cb){
			super(cb);
			this.command = cmd;
			this.callback = cb;
		}

		@Override
		protected void doExecute() {
			if(this.command instanceof ICancellable){
				this.callback.setCancellable((ICancellable)this.command);
			}
			try {
				this.callback.success(this.command.call());
			}catch(Throwable t){
				this.callback.failed(t);
			}
		}

		@Override
		protected IAsyncCallback<?> getCallback() {
			return this.callback;
		}

	}
	
	private class AsyncCallableTask<S> extends CancellableTask<S> {
		private final IAsyncCallable<S> command;
		private final IAsyncCallback<S> callback;
		
		public AsyncCallableTask(IAsyncCallable<S> cmd, IAsyncCallback<S> cb){
			super(cb);
			this.command = cmd;
			this.callback = cb;
		}

		@Override
		protected void doExecute() {
			if(this.command instanceof ICancellable){
				this.callback.setCancellable((ICancellable)this.command);
			}
			try {
				this.command.call(this.callback);
			}catch(Throwable t){
				this.callback.failed(t);
			}
		}

		@Override
		protected IAsyncCallback<?> getCallback() {
			return this.callback;
		}

	}


	private int maxThread = 20;
	private int minThread = 5;
	private int commandQueueSize = 100, timeoutInSeconds = 30;
	private ThreadPoolExecutor executor;
	private ScheduledExecutorService scheduledExecutor;
	private ThreadGroup cmdGrp = new ThreadGroup("Command Executor thread group");
	private Map<String, ICommandHandler<?,?>> handlers = new HashMap<String, ICommandHandler<?,?>>();
	private List<ICommandValidator> validators = new ArrayList<ICommandValidator>();
	private LinkedBlockingDeque<Runnable> taskQueue;
	private LinkedList<CancellableTask<?>> waitingTasks = new LinkedList<CancellableTask<?>>();
	private TimeoutChecker tmChecker;
	private ICommandExecutionContext cmdCtx = new ICommandExecutionContext() {
		
		public IKernelContext getKernelContext() {
			return context;
		}
	};
	
	protected void removeSumittedTask(Runnable task){
		this.executor.remove(task);
		removeFromWaitingList(task);
	}
	
	protected boolean removeFromWaitingList(Runnable task) {
		synchronized(this.waitingTasks){
			return this.waitingTasks.remove(task);
		}
	}
	/**
	 * @param command
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <V> ICommandHandler<V, ICommand<V>> validateCommand(final ICommand<V> command) {
		final ICommandHandler<V, ICommand<V>> handler;
		synchronized(this.handlers){
			handler = (ICommandHandler<V, ICommand<V>>) this.handlers.get(command.getCommandName());
		}
		if(handler == null){
			throw new UnsupportedCommandException("Cannot find command handler for command :"+command.getCommandName());
		}
		command.validate();
		ICommandValidator[] valids = null;
		synchronized(this.validators){
			valids = this.validators.toArray(new ICommandValidator[0]);
		}
		if(valids != null){
			for (ICommandValidator iCommandValidator : valids) {
				iCommandValidator.checkCommandConstraints(command);
			}
		}
		return handler;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.command.api.ICommandExecutor#registerCommandHandler(java.lang.String, com.wxxr.mobile.core.command.api.ICommandHandler)
	 */
	public <V, C extends ICommand<V>> ICommandExecutor registerCommandHandler(String cmdName,
			ICommandHandler<V,C> handler) {
		synchronized(this.handlers){
			this.handlers.put(cmdName, handler);
			handler.init(cmdCtx);
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.command.api.ICommandExecutor#unregisterCommandHandler(java.lang.String, com.wxxr.mobile.core.command.api.ICommandHandler)
	 */
	public <V, C extends ICommand<V>> ICommandExecutor unregisterCommandHandler(String cmdName,
			ICommandHandler<V,C> handler) {
		synchronized(this.handlers){
			@SuppressWarnings("unchecked")
			ICommandHandler<V,C> old = (ICommandHandler<V, C>) this.handlers.get(cmdName);
			if(old == handler){
				this.handlers.remove(cmdName);
				handler.destroy();
			}
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.command.api.ICommandExecutor#registerCommandValidator(com.wxxr.mobile.core.command.api.ICommandValidator)
	 */
	public ICommandExecutor registerCommandValidator(ICommandValidator validator) {
		synchronized(this.validators){
			if(!validators.contains(validator)){
				this.validators.add(validator);
				validator.init(cmdCtx);
			}
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.command.api.ICommandExecutor#unregisterCommandValidator(com.wxxr.mobile.core.command.api.ICommandValidator)
	 */
	public ICommandExecutor unregisterCommandValidator(
			ICommandValidator validator) {
		synchronized(this.validators){
			if(this.validators.remove(validator)){
				validator.destroy();
			}
		}
		return this;
	}

	@Override
	protected void initServiceDependency() {
		addRequiredService(IRestProxyService.class);
	}

	protected boolean isCurrentCommandThread() {
		return isCommandThread(Thread.currentThread());
	}
	
	protected boolean isCommandThread(Thread t) {
		return t.getThreadGroup() == this.cmdGrp;
	}
	
	@Override
	protected void startService() {
		this.taskQueue = new LinkedBlockingDeque<Runnable>(this.commandQueueSize);
		this.executor = new ThreadPoolExecutor(this.minThread, this.maxThread, 60, TimeUnit.SECONDS, this.taskQueue, 
				new ThreadFactory() {
					private AtomicInteger seqNo = new AtomicInteger(1);
					public Thread newThread(Runnable r) {
						return new Thread(cmdGrp,r, "Command executor thread -- "+seqNo.getAndIncrement());
					}
				}, new ThreadPoolExecutor.AbortPolicy());
		this.tmChecker = new TimeoutChecker();
		this.tmChecker.start();
		this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		context.registerService(ICommandExecutor.class, this);
	}

	@Override
	protected void stopService() {
		context.unregisterService(ICommandExecutor.class, this);
		if(this.tmChecker != null){
			this.tmChecker.stop();
			this.tmChecker = null;
		}
		if(this.scheduledExecutor != null){
			this.scheduledExecutor.shutdownNow();
			this.scheduledExecutor = null;
		}
		if(this.executor != null){
			this.executor.shutdownNow();
			this.executor = null;
		}
		if(this.taskQueue != null){
			this.taskQueue.clear();
			this.taskQueue = null;
		}
		synchronized(this.validators){
			for (ICommandValidator validator : this.validators) {
				validator.destroy();
			}
			this.validators.clear();
		}
		synchronized(this.handlers){
			for (ICommandHandler<?,?> handler : this.handlers.values()) {
				handler.destroy();
			}
			this.handlers.clear();
		}
	}

	/**
	 * @return the maxThread
	 */
	public int getMaxThread() {
		return maxThread;
	}

	/**
	 * @return the minThread
	 */
	public int getMinThread() {
		return minThread;
	}

	/**
	 * @return the commandQueueSize
	 */
	public int getCommandQueueSize() {
		return commandQueueSize;
	}

	/**
	 * @param maxThread the maxThread to set
	 */
	public void setMaxThread(int maxThread) {
		this.maxThread = maxThread;
	}

	/**
	 * @param minThread the minThread to set
	 */
	public void setMinThread(int minThread) {
		this.minThread = minThread;
	}

	/**
	 * @param commandQueueSize the commandQueueSize to set
	 */
	public void setCommandQueueSize(int commandQueueSize) {
		this.commandQueueSize = commandQueueSize;
	}

	public <V> void submitCommand(final ICommand<V> command, final IAsyncCallback<V> callback) {
		if(command == null){
			throw new IllegalArgumentException("Invalid command NULL !");
		}
		validateCommand(command);
		CommandTask<V> task = new CommandTask<V>(command, callback != null ? callback : new DummyCallback<V>());
		submitTask(task);
		
	}

	public void validationConstraints(ConstraintLiteral... constraints) {
		ICommandValidator[] valids = null;
		synchronized(this.validators){
			valids = this.validators.toArray(new ICommandValidator[0]);
		}
		if(valids != null){
			for (ICommandValidator iCommandValidator : valids) {
				iCommandValidator.validationConstraints(constraints);
			}
		}
	}

	@Override
	public void submit(Runnable task, IAsyncCallback<Object> callback) {
		if(task == null){
			throw new IllegalArgumentException("Invalid Task NULL !");
		}
		RunnableTask wtask = new RunnableTask(task, callback != null ? callback : new DummyCallback<Object>());
		submitTask(wtask);
	}

	@Override
	public <S> void submit(IAsyncCallable<S> call, IAsyncCallback<S> callback) {
		if(call == null){
			throw new IllegalArgumentException("Invalid callable NULL !");
		}
		AsyncCallableTask<S> task = new AsyncCallableTask<S>(call, callback != null ? callback : new DummyCallback<S>());
		submitTask(task);
	}

	
	@Override
	public <S> void submit(Callable<S> call, IAsyncCallback<S> callback) {
		if(call == null){
			throw new IllegalArgumentException("Invalid callable NULL !");
		}
		CallableTask<S> task = new CallableTask<S>(call, callback != null ? callback : new DummyCallback<S>());
		submitTask(task);
	}

	/**
	 * @param task
	 */
	protected <S> void submitTask(CancellableTask<S> task) {
		if(isCurrentCommandThread()){
			task.run();
		}else{
			this.executor.submit(task);
			add2WaitingList(task);
		}
	}
	
	
	protected void add2WaitingList(CancellableTask<?> task) {
		synchronized(this.waitingTasks){
			if(!this.waitingTasks.contains(task)){
				this.waitingTasks.add(task);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected CancellableTask<?>[] getWaitingTasks() {
		synchronized(this.waitingTasks){
			return this.waitingTasks.toArray(new CancellableTask[0]);
		}
	}
	/**
	 * @return the timeoutInSeconds
	 */
	public int getTimeoutInSeconds() {
		return timeoutInSeconds;
	}
	/**
	 * @param timeoutInSeconds the timeoutInSeconds to set
	 */
	public void setTimeoutInSeconds(int timeoutInSeconds) {
		this.timeoutInSeconds = timeoutInSeconds;
	}

	@Override
	public void invokeLater(Runnable task, long delay, TimeUnit unit) {
		this.scheduledExecutor.schedule(task, delay, unit);
	}

}
