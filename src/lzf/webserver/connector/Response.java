package lzf.webserver.connector;

import java.io.IOException;
import java.util.Locale;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��12�� ����1:46:26
* @Description HTTP��Ӧ��
*/
public abstract class Response extends ResponseBase {
	//״̬���Ӧ�ĸ�����Ϣ
	protected String statusMsg;
	//�����Ӧ�Ѿ����͸��ͻ�������
	protected volatile boolean committed = false;
	
	public Response() {
		super();
	}
	
	/**
	 * ��ͻ��˷�����Ӧ��������������
	 */
	public abstract void sendResponse();
	
	/**
	 * ��ͻ��˷���ָ��״̬�벢�����Ӧ�����ô˷��������Ӧ��Ӧ�ٱ�д��
	 * @param sc ��Ӧ��
	 */
	@Override
	public synchronized final void sendError(int sc, String msg) throws IOException {
		committed = true;
		sendError0(sc, msg);
	}
	
	/**
	 * Ӧ����д��ͻ���Socket�ķ���
	 */
	protected abstract void sendError0(int sc, String msg);
	
	/**
	 * ������ͻ��˷���ָ��״̬��ָ������HTMLҳ�棬Ȼ�������Ӧ�����ô˷��������Ӧ��Ӧ�ٱ�д��
	 * @param sc ��Ӧ��
	 */
	@Override
	public synchronized final void sendError(int sc) throws IOException {
		committed = true;
		sendError0(sc);
	}
	
	/**
	 * Ӧ����д��ͻ���Socket�ķ���
	 */
	protected abstract void sendError0(int sc);

	/**
	 * �ض���ҳ��
	 * @param location ��תURL������Ϊ����·��Ҳ��Ϊ���·��
	 */
	@Override
	public final void sendRedirect(String location) throws IOException {
		setStatus(302);
		headerMap.put("Location", location);
	}
	
	/**
	 * ������Ӧ״̬��
	 * @param sc ״̬�� ��Χ��100~500
	 * @param sm ״̬������
	 */
	@Override
	public final void setStatus(int sc, String sm) {
		this.statusMsg = sm;
	}
	
	/**
	 * ���Response����������
	 */
	@Override
	public void reset() {
		if(isCommitted())
			throw new IllegalStateException();
		headerMap.clear();
		status = 0;
	}

	/**
	 * ��Response�����ύ���ͻ�������
	 */
	@Override
	public boolean isCommitted() {
		return committed;
	}
	
	@Override
	public void setLocale(Locale loc) {
		// TODO Auto-generated method stub

	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}
}
