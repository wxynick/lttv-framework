package com.letv.mobile.core.rpc.api;

/**
 * Thrown to indicate that an request has timed out.
 * 
 */
public class RequestTimeoutException extends RequestException {
  private static String formatMessage(int timeoutMillis) {
    return "A request timeout has expired after "
        + Integer.toString(timeoutMillis) + " ms";
  }

  /**
   * Time, in milliseconds, of the timeout.
   */
  private final int timeoutMillis;

  /**
   * Request object which experienced the timed out.
   */
  private final Request request;

  /**
   * Constructs a timeout exception for the given {@link Request}.
   * 
   * @param request the request which timed out
   * @param timeoutMillis the number of milliseconds which expired
   */
  public RequestTimeoutException(Request request, int timeoutMillis) {
    super(formatMessage(timeoutMillis));
    this.request = request;
    this.timeoutMillis = timeoutMillis;
  }

  /**
   * Returns the {@link Request} instance which timed out.
   * 
   * @return the {@link Request} instance which timed out
   */
  public Request getRequest() {
    return request;
  }

  /**
   * Returns the request timeout value in milliseconds.
   * 
   * @return the request timeout value in milliseconds
   */
  public int getTimeoutMillis() {
    return timeoutMillis;
  }
}
