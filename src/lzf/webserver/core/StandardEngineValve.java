package lzf.webserver.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import lzf.webserver.Host;
import lzf.webserver.connector.Request;
import lzf.webserver.connector.Response;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��20�� ����9:18:39
* @Description ��˵��
*/
public final class StandardEngineValve extends ValveBase {

	StandardEngineValve() {
		super();
	}
	
	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		
		Host host = request.getHost();
		//���û���ҵ�Host����Mapperӳ��û���ҵ�������Host��������ôֱ�ӷ����ͻ���400����
		if(host == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "������:" 
					+ request.getServerName() + "������");
			return;
		}
		
		//���͸�Host�����Ĺܵ�
		host.getPipeline().getFirst().invoke(request, response);
	}
}