package lzf.webserver.loader;

import java.io.File;

import lzf.webserver.Context;
import lzf.webserver.LifecycleException;
import lzf.webserver.Loader;
import lzf.webserver.core.LifecycleBase;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月21日 下午4:30:10
* @Description web应用载入器，包括XML文件解析，静态资源文件的读取，类加载实现
*/
public class WebappLoader extends LifecycleBase implements Loader {
	
	//web应用URL路径
	public static final String WEBAPP_URL = "file:/" + System.getProperty("user.dir") + File.separator + "webapps";
	
	//
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
		if(context == null)
			return;
		
		if(getLifecycleState().isAvailable()) {
			throw new LifecycleException("The webAppLoader is running");
		}
		
		synchronized(this) {
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
	
	protected void resourceLoader() {
		
	}
}
