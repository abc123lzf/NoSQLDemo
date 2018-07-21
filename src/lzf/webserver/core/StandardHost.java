package lzf.webserver.core;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lzf.webserver.Container;
import lzf.webserver.Context;
import lzf.webserver.Engine;
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
	
	private File appBaseFolder = DEFAULT_CONTEXT_FOLDER;
	
	public StandardHost(Engine engine) {
		super.parentContainer = engine;
	}
	
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
		for(Container context: childContainers) {
			context.init();
		}
		pipeline.addValve(new StandardHostValve());
	}

	@Override
	protected void startInternal() throws Exception {
		for(Container context: childContainers) {
			context.start();
		}
	}

	@Override
	protected void stopInternal() throws Exception {
		for(Container context: childContainers) {
			context.stop();
		}
	}

	@Override
	protected void destoryInternal() throws Exception {
		for(Container context: childContainers) {
			context.destory();
		}
	}

}
