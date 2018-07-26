package lzf.webserver;
/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��21�� ����4:40:23
* @Description �������ӿڣ���webӦ��������������XML�ļ���������̬��Դ�ļ��Ķ�ȡ�������ʵ��
* @see lzf.webserver.core.WebappLoader
*/
public interface Loader {

	/**
	 * @return �������������������
	 */
	public ClassLoader getClassLoader();
	
	/**
	 * @return ��webӦ�õ�JSP�������
	 */
	public ClassLoader getJspClassLoader();
	
	/**
	 * @return ��������Ӧ��Context����
	 */
	public Context getContext();
	
	/**
	 * @param context ���ü�����������Context����
	 */
	public void setContext(Context context) throws LifecycleException;
	
	/**
	 * @return Context����֧���ؼ�����(�Ȳ��JSP��Servlet��Jar��)
	 */
	public boolean getReloadable();
	
	/**
	 * @param reloadable �����ؼ���(�Ȳ��ѡ��)
	 */
	public void setReloadable(boolean reloadable) throws LifecycleException;
}
