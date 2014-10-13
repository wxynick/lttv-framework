/**
 * 
 */
package com.letv.mobile.core.rpc.http.api;


/**
 * @author  
 *
 */
public interface IRestProxyService {
    void setDefautTarget(String target);
	<T> T getRestService(Class<T> clazz, Class<?> ifRest);
	/**
	 * 使用这个方法可以返回Rest服务的同步或异步接口<br/>
	 * 要返回同步接口，第一个参数必须时rest的接口类，第二个参数必须为空（NULL），第三个参数是服务器的URL地址，可以为空<br/>
	 * 要返回异步接口，第一个参数必须是rest的异步接口类，第二个参数必须为rest的接口类，第三个参数是服务器的URL地址，可以为空<br/>
	 * 所谓的rest的异步接口类是指，这个接口类中的所有方法的名称和参数必须和真实的rest接口类一致，只是返回值必须是<code>Async<T></code>,T为真实rest接口方法的返回值类型
	 * @param clazz
	 * @param ifRest
	 * @param target
	 * @return
	 */
	<T> T getRestService(Class<T> clazz, Class<?> ifRest, String target);
}
