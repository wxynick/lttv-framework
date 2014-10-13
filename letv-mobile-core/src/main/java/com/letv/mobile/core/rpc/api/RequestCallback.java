/*
 * Copyright 2006 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.letv.mobile.core.rpc.api;

/**
 * The primary interface a caller must implement to receive a response to a
 * {@link Request}.
 * 
 */
public interface RequestCallback<S extends Response, T extends Request<S>> {

  /**
   * Called when a pending {@link Request} completes
   * normally.  Note this method is called even when the status code of the 
   * HTTP response is not "OK", 200. 
   * 
   * @param request the object that generated this event
   * @param response an instance of the
   *        {@link Response} class 
   */
  void onResponseReceived(T request, S response);

  /**
   * Called when a {@link Request} does not complete
   * normally.  A {@link RequestTimeoutException RequestTimeoutException} is
   * one example of the type of error that a request may encounter.
   * 
   * @param request the request object which has experienced the error
   *     condition, may be null if the request was never generated
   * @param exception the error that was encountered
   */
  void onError(T request, Throwable exception);
  
  void onPrepare(T request) throws Exception ;
}
