package lzf.webserver.mapper;

import lzf.webserver.Container;
import lzf.webserver.ContainerEvent;
import lzf.webserver.ContainerListener;
import lzf.webserver.Context;
import lzf.webserver.Engine;
import lzf.webserver.Host;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月22日 上午9:12:12
* @Description 容器事件映射监听器，应处于Engine级别监听层
*/
public class GlobelMappedListener implements ContainerListener {
	
	private final GlobelMapper mapper;
	
	public GlobelMappedListener(GlobelMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public void containerEvent(ContainerEvent event) {
		
		String type = event.getType();
		Object data = event.getData();
		
		if(type.equals(Container.ADD_CHILD_EVENT)) {
			
			if(data instanceof Engine)
				hostAddEvent((Host)data);
			else if(data instanceof Context)
				contextAddEvent((Context)data);
			
		} else if(type.equals(Container.REMOVE_CHILD_EVENT)) {
			
			if(data instanceof Host)
				hostRemoveEvent((Host)data);
			else if(data instanceof Context)
				contextRemoveEvent((Context)data);
			
		}
	}
	
	private void hostAddEvent(Host host) {
		mapper.addHost(host);
	}

	private void hostRemoveEvent(Host host) {
		mapper.removeHost(host);
	}
	
	private void contextAddEvent(Context context) {
		mapper.addContext(context);
	}
	
	private void contextRemoveEvent(Context context) {
		mapper.removeContext(context);
	}
}
