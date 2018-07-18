package lzf.webserver.core;

import lzf.webserver.Container;
import lzf.webserver.Engine;
import lzf.webserver.Host;
import lzf.webserver.Service;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月18日 下午4:12:01
* @Description 类说明
*/
public class StandardEngine extends ContainerBase implements Engine {
	
	private Service service = null;
	
	public StandardEngine() {
	}
	
	public StandardEngine(Service service) {
		this.service = service;
	}

	@Override
	public void setService(Service service) {
		this.service = service;
	}

	@Override
	public Service getService() {
		return service;
	}

	@Override
	protected void initInternal() throws Exception {
		for(Container c : childContainers) {
			c.init();
		}
	}

	@Override
	protected void startInternal() throws Exception {
		for(Container c : childContainers) {
			c.start();
		}
	}

	@Override
	protected void stopInternal() throws Exception {
		for(Container c : childContainers) {
			c.stop();
		}
	}

	@Override
	protected void destoryInternal() throws Exception {
		for(Container c : childContainers) {
			c.destory();
		}
	}

	/**
	 * 如果添加的容器不是Host则抛出异常
	 */
	@Override
	protected void addChildContainerCheck(Container container) throws IllegalArgumentException{
		if(!(container instanceof Host))
			throw new IllegalArgumentException(container.getClass().getName());
	}
}
