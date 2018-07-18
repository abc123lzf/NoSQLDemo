package lzf.webserver.core;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lzf.webserver.Container;
import lzf.webserver.Context;
import lzf.webserver.Host;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月18日 下午6:02:08
* @Description 类说明
*/
public class StandardHost extends ContainerBase implements Host {

	public static final File DEFAULT_CONTEXT_FOLDER = new File((String)System.getProperty("user.dir") 
			+ File.separator + "webapps");
	
	private List<Context> contexts = new CopyOnWriteArrayList<>();
	private File appBaseFolder = DEFAULT_CONTEXT_FOLDER;
	
	@Override
	public File getWebappBaseFolder() {
		return appBaseFolder;
	}

	@Override
	public void setWebappBaseFolder(File folder) {
		this.appBaseFolder = folder;
	}

	@Override
	protected void addChildContainerCheck(Container container) throws IllegalArgumentException {
		if(!(container instanceof Context))
			throw new IllegalArgumentException();
	}

	@Override
	protected void initInternal() throws Exception {
		for(Context context: contexts) {
			context.init();
		}
	}

	@Override
	protected void startInternal() throws Exception {
		for(Context context: contexts) {
			context.start();
		}
	}

	@Override
	protected void stopInternal() throws Exception {
		for(Context context: contexts) {
			context.stop();
		}
	}

	@Override
	protected void destoryInternal() throws Exception {
		for(Context context: contexts) {
			context.destory();
		}
	}

}
