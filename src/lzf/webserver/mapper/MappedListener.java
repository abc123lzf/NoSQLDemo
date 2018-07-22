package lzf.webserver.mapper;

import lzf.webserver.Container;
import lzf.webserver.ContainerEvent;
import lzf.webserver.ContainerListener;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��22�� ����9:12:12
* @Description �����¼�ӳ�������
*/
public class MappedListener implements ContainerListener {
	
	public MappedListener() {
		
	}

	@Override
	public void containerEvent(ContainerEvent event) {
		String type = event.getType();
		Object data = event.getData();
		
		if(type.equals(Container.ADD_CHILD_EVENT)) {
			addChildEvent();
		} else if(type.equals(Container.REMOVE_CHILD_EVENT)) {
			removeChildEvent();
		}
	}
	
	private void addChildEvent() {
		
	}

	private void removeChildEvent() {
		
	}
}
