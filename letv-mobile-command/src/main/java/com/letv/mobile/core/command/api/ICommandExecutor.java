/**
 * 
 */
package com.letv.mobile.core.command.api;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.letv.mobile.core.async.api.IAsyncCallable;
import com.letv.mobile.core.async.api.IAsyncCallback;
import com.letv.mobile.core.command.annotation.ConstraintLiteral;

/**
 * @author  
 *
 */
public interface ICommandExecutor {
	void invokeLater(Runnable task, long delay, TimeUnit unit);
	void submit(Runnable task,IAsyncCallback<Object> callback);
	<T> void submit(Callable<T> call,IAsyncCallback<T> callback);
	<T> void submit(IAsyncCallable<T> call,IAsyncCallback<T> callback);
	<T> void submitCommand(ICommand<T> command,IAsyncCallback<T> callback);
	<T, C extends ICommand<T>> ICommandExecutor registerCommandHandler(String cmdName,ICommandHandler<T,C> handler);
	<T, C extends ICommand<T>> ICommandExecutor unregisterCommandHandler(String cmdName,ICommandHandler<T,C> handler);
	ICommandExecutor registerCommandValidator(ICommandValidator validator);
	ICommandExecutor unregisterCommandValidator(ICommandValidator validator);
	void validationConstraints(ConstraintLiteral... constraints);
}
