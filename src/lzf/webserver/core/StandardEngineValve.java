package lzf.webserver.core;

import java.io.IOException;

import javax.servlet.ServletException;

import lzf.webserver.connector.Request;
import lzf.webserver.connector.Response;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��20�� ����9:18:39
* @Description ��˵��
*/
public class StandardEngineValve extends ValveBase {

	public StandardEngineValve() {
		super();
	}
	
	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		

	}
}
