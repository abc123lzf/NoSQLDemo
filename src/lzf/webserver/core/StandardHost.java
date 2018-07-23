package lzf.webserver.core;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lzf.webserver.Container;
import lzf.webserver.Context;
import lzf.webserver.Engine;
import lzf.webserver.Host;
import lzf.webserver.mapper.GlobelMappedListener;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月18日 下午6:02:08
* @Description 类说明
*/
public class StandardHost extends ContainerBase implements Host {

	public static final File DEFAULT_CONTEXT_FOLDER = new File("webapps");
	
	private File appBaseFolder = DEFAULT_CONTEXT_FOLDER;
	
	public StandardHost(Engine parentConatiner) {
		super(parentConatiner);
		addContainerListener(new GlobelMappedListener(((Engine)getParentContainer())
				.getService().getGlobelMapper()));
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
		findWebappAndLoad();
		
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

	private void findWebappAndLoad() {
		
		if(appBaseFolder.exists()) {
			File[] folders = appBaseFolder.listFiles();
			for(File folder : folders) {
				if(folder.isDirectory()) {
					StandardContext context = (StandardContext) StandardContext.createContextByFolder(this, folder);
					if(context != null)
						addChildContainer(context);
				}
			}
		}
	}
}
