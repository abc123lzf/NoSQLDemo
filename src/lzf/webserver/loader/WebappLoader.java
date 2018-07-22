package lzf.webserver.loader;

import java.io.File;

import lzf.webserver.Context;
import lzf.webserver.LifecycleException;
import lzf.webserver.Loader;
import lzf.webserver.core.LifecycleBase;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;

/**
 * @author ���ӷ�
 * @version 1.0
 * @date 2018��7��21�� ����4:30:10
 * @Description webӦ��������������XML�ļ���������̬��Դ�ļ��Ķ�ȡ�������ʵ��
 */
public class WebappLoader extends LifecycleBase implements Loader {

	public static final Log log = LogFactory.getLog(WebappLoader.class);
	
	public static final String WEBAPP_PATH = System.getProperty("user.dir") + File.separator + "webapps";
	// webӦ��URL·��
	public static final String WEBAPP_URL = "file:/" + WEBAPP_PATH;

	// ��Web������������Context����
	private Context context = null;

	private ClassLoader classLoader = null;

	private boolean reloadable = false;

	public WebappLoader() {
	}

	public WebappLoader(Context context) {
		this.context = context;
	}

	@Override
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public Context getContext() {
		return context;
	}

	@Override
	public void setContext(Context context) throws LifecycleException {
		if (context == null)
			return;

		if (getLifecycleState().isAvailable()) {
			throw new LifecycleException("The webAppLoader is running");
		}

		synchronized (this) {
			this.context = context;
			setReloadable(context.getReloadable());
		}
	}

	@Override
	public boolean getReloadable() {
		return reloadable;
	}

	@Override
	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}

	@Override
	protected void initInternal() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void startInternal() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void stopInternal() throws Exception {

	}

	@Override
	protected void destoryInternal() throws Exception {

	}

	protected void resourceLoad(String path) {
		File file = new File(path);
		if (file.exists()) {
			File[] files = file.listFiles();
			if (files.length == 0) {
				return;
			} else {
				for (File file2 : files) {
					if (file2.isDirectory()) {
						System.out.println("�ļ���:" + file2.getAbsolutePath());
						resourceLoad(file2.getAbsolutePath());
					} else {
						System.out.println("�ļ�:" + file2.getAbsolutePath());
					}
				}
			}
		}
	}
}
