package lzf.webserver.core;

import lzf.webserver.Container;
import lzf.webserver.Engine;
import lzf.webserver.Host;
import lzf.webserver.Service;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��18�� ����4:12:01
* @Description ��˵��
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
	 * �����ӵ���������Host���׳��쳣
	 */
	@Override
	protected void addChildContainerCheck(Container container) throws IllegalArgumentException{
		if(!(container instanceof Host))
			throw new IllegalArgumentException(container.getClass().getName());
	}
}
