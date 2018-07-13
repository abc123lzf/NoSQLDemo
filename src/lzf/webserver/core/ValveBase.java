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
	
	public Valve getNext() {
		return next;
	}
		
	public void setNext(Valve valve) {
		this.next = valve;
	}
		
	//必须在该方法中调用next.invoke()
	public abstract void invoke(Request request, Response response)
			throws IOException, ServletException;
}
