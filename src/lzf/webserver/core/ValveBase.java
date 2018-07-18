package lzf.webserver.core;

import java.io.IOException;

import javax.servlet.ServletException;

import lzf.webserver.Container;
import lzf.webserver.Valve;
import lzf.webserver.connector.Request;
import lzf.webserver.connector.Response;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月12日 下午2:52:27
* @Description 阀门抽象类，阀门实现类应继承该抽象类
*/
public abstract class ValveBase implements Valve {
	
	protected Valve next = null;
	
	protected Container container = null;
	
	/**
	 * 获取下一个管道
	 * @return Valve 下一个管道
	 */
	@Override
	public Valve getNext() {
		return next;
	}
		
	/**
	 * 设置下一个管道
	 * @param valve 下一个管道实例
	 */
	@Override
	public void setNext(Valve valve) {
		this.next = valve;
	}
	
	/**
	 * 获取该阀门所属的容器实例
	 * @return Container容器实例
	 */
	@Override
	public Container getContainer() {
		return container;
	}
	
	/**
	 * 设置该阀门所属的容器实例
	 * @param container Container容器实例
	 */
	@Override
	public void setContainer(Container container) {
		this.container = container;
	}
		
	/**
	 * 阀门具体的业务逻辑实现
	 * @param request 请求对象
	 * @param response 响应对象
	 */
	@Override
	public final void invoke(Request request, Response response)
			throws IOException, ServletException {
		invoke0(request, response);
		next.invoke(request, response);
	}
	
	/**
	 * 阀门具体的业务逻辑实现细节，实现阀门的对象需重写该方法
	 * @param request 请求对象
	 * @param response 响应对象
	 */
	protected abstract void invoke0(Request request, Response response)
			throws IOException, ServletException;
	
	/**
	 * 包含这个管道的类名、容器的名称
	 * @return 类似lzf.webserver.valve[containerName]
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.getClass().getName());
		sb.append('[');
		if(getContainer() == null) {
			sb.append("Container is null");
		} else {
			sb.append(getContainer().getName());
		}
		sb.append(']');
		return sb.toString();
	}
}
