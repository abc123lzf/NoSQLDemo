package lzf.webserver.core;

import org.apache.tomcat.util.res.StringManager;

import lzf.webserver.Engine;
import lzf.webserver.Host;
import lzf.webserver.LifecycleException;
import lzf.webserver.Service;
import lzf.webserver.mapper.GlobelMappedListener;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月18日 下午4:12:01
* @Description 类说明
*/
public class StandardEngine extends ContainerBase<Void, Host> implements Engine {
	
	private static final StringManager sm = StringManager.getManager(StandardEngine.class);
	
	private Service service = null;
	
	public StandardEngine(Service service) {
		super();
		this.service = service;
		addContainerListener(new GlobelMappedListener(service.getGlobelMapper()));
	}

	@Override
	public void setService(Service service) throws LifecycleException {
		
		if(getLifecycleState().isAvailable())
			throw new LifecycleException(sm.getString("StandardEngine.setService.e0", getName()));
		
		this.service = service;
	}

	@Override
	public Service getService() {
		return service;
	}

	@Override
	protected void initInternal() throws Exception {
		for(Host c : childContainers) {
			c.init();
		}
		pipeline.addValve(new StandardEngineValve());
	}

	@Override
	protected void startInternal() throws Exception {
		for(Host c : childContainers) {
			c.start();
		}
	}

	@Override
	protected void stopInternal() throws Exception {
		for(Host c : childContainers) {
			c.stop();
		}
	}

	@Override
	protected void destoryInternal() throws Exception {
		for(Host c : childContainers) {
			c.destory();
		}
	}

}
