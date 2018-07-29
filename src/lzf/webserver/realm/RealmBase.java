package lzf.webserver.realm;

import org.apache.tomcat.util.res.StringManager;

import lzf.webserver.Container;
import lzf.webserver.Realm;
import lzf.webserver.core.LifecycleBase;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��29�� ����10:51:02
* @Description ��Դ��֤ģ�������
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
