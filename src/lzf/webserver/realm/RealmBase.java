package lzf.webserver.realm;

import org.apache.tomcat.util.res.StringManager;

import lzf.webserver.Container;
import lzf.webserver.Realm;
import lzf.webserver.core.LifecycleBase;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月29日 上午10:51:02
* @Description 资源认证模块抽象类
*/
public abstract class RealmBase<T extends Container<?,?>> extends LifecycleBase implements Realm<T> {
	
	protected static final StringManager sm = StringManager.getManager(RealmBase.class);
	
	protected T container = null;
	
	protected boolean available = false;

	@Override
	public T getContainer() {
		return container;
	}

	@Override
	public void setContainer(T container) {
		this.container = container;
	}

	@Override
	public boolean isAvailable() {
		return available;
	}
	
	public void setAvailable(boolean available) {
		this.available = available;
	}
}
