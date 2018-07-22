package lzf.webserver.mapper;

import lzf.webserver.Container;
import lzf.webserver.ContainerEvent;
import lzf.webserver.ContainerListener;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月22日 上午9:12:12
* @Description 容器事件映射监听器
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
