package lzf.webserver;

import java.security.Principal;

import lzf.webserver.connector.Request;
import lzf.webserver.connector.Response;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月29日 上午10:25:02
* @Description 资源认证模块，Engine、Host、Context应当拥有这个模块
* 泛型参数T为这个容器的类型
*/
public interface Realm<T extends Container<?, ?>> {

	/**
	 * @return 该资源认证模块所属的容器
	 */
	public T getContainer();
	
	/**
	 * @param container 该资源认证模块所属的容器
	 */
	public void setContainer(T container);
	
	/**
	 * 通过用户名进行身份验证
	 * @param username 用户名
	 * @return Principal对象，如果没有通过身份认证则返回null
	 */
	public Principal authenticate(String username);
	
	/**
	 * 通过用户名和密码进行认证
	 * @param username 用户名
	 * @param password 密码
	 * @return Principal对象，如果没有通过身份认证则返回null
	 */
	public Principal authenticate(String username, String password);
	
	/**
	 * 判定是否可以访问这个资源
	 * @param request 单次请求对象
	 * @param response 单次响应对象
	 * @param context 请求映射的Context容器
	 * @return 本次请求可以访问这个资源吗
	 */
	public boolean hasResourcePermission(Request request, Response response);
	
	/**
	 * @return 该资源认证模块可用吗
	 */
	public boolean isAvailable();
}
