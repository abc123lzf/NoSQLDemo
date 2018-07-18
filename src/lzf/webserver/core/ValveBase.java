package lzf.webserver.core;

import java.io.IOException;

import javax.servlet.ServletException;

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
	
	protected Valve next;
	
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
	 * 阀门具体的业务逻辑实现，必须在该方法最后调用next.invoke()
	 * @param request 请求对象
	 * @param response 响应对象
	 */
	@Override
	public abstract void invoke(Request request, Response response)
			throws IOException, ServletException;
}
