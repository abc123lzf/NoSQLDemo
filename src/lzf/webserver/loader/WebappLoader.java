package lzf.webserver.loader;

import java.io.File;

import lzf.webserver.Context;
import lzf.webserver.LifecycleException;
import lzf.webserver.Loader;
import lzf.webserver.core.LifecycleBase;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月21日 下午4:30:10
 * @Description web应用载入器，包括XML文件解析，静态资源文件的读取，类加载实现
 */
public class WebappLoader extends LifecycleBase implements Loader {

	public static final Log log = LogFactory.getLog(WebappLoader.class);
	
	public static final String WEBAPP_PATH = System.getProperty("user.dir") + File.separator + "webapps";
	// web应用URL路径
	public static final String WEBAPP_URL = "file:/" + WEBAPP_PATH;

	// 该Web加载器所属的Context容器
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
						System.out.println("文件夹:" + file2.getAbsolutePath());
						resourceLoad(file2.getAbsolutePath());
					} else {
						System.out.println("文件:" + file2.getAbsolutePath());
					}
				}
			}
		}
	}
}
