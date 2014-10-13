package com.letv.mobile.core.command.api;

import com.letv.mobile.core.async.api.IAsyncCallback;

/**
 * @author  
 *
 */
public interface ICommandHandler<T, C extends ICommand<T>> {
   void execute(C command,IAsyncCallback<T> callback);
   void init(ICommandExecutionContext ctx);
   void destroy();

}