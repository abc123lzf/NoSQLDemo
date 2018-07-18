package lzf.webserver.connector;

import java.io.IOException;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��12�� ����1:46:26
* @Description HTTP��Ӧ��
*/
public class Response extends ResponseBase {

	@Override
	public void sendError(int sc, String msg) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendError(int sc) throws IOException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * ��ͻ��˷�����תָ��
	 * ����ͻ��˷���״̬��302Ȼ����ת
	 * @param location ��תURL������Ϊ����·��Ҳ��Ϊ���·��
	 */
	@Override
	public void sendRedirect(String location) throws IOException {
		setStatus(302);
		headerMap.put("Refresh", location);
	}
}
