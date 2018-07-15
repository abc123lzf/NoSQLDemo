package lzf.webserver;

import java.io.IOException;

import javax.servlet.ServletException;

import lzf.webserver.connector.Request;
import lzf.webserver.connector.Response;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月12日 下午1:31:57
* @Description 管道模型中的阀门
*/
public interface Valve {

	/**
	 * 获取下一个阀门
	 * @return 下一个阀门实例，不存在则返回null
	 */
	public Valve getNext();
	
	/**
	 * 设置下一个阀门
	 * @param valve 下一个阀门实例
	 */
	public void setNext(Valve valve);
	
	/**
	 * 阀门执行方法，如果是基础阀门，则应当调用子容器管道对象的第一个阀门
	 * @param request 连接器封装好的Request对象
	 * @param response 连接器封装好的Response对象
	 */
	public void invoke(Request request, Response response) 
			throws IOException, ServletException;
}
