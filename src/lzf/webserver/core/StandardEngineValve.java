package lzf.webserver.core;

import java.io.IOException;

import javax.servlet.ServletException;

import lzf.webserver.connector.Request;
import lzf.webserver.connector.Response;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月20日 下午9:18:39
* @Description 类说明
*/
public class StandardEngineValve extends ValveBase {

	public StandardEngineValve() {
		super();
	}
	
	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		

	}
}
