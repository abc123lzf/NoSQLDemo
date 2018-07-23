package lzf.webserver.mapper;

import lzf.webserver.Container;
import lzf.webserver.ContainerEvent;
import lzf.webserver.ContainerListener;
import lzf.webserver.Wrapper;

public final class ContextMapperListener implements ContainerListener {
	
	private final ContextMapper mapper;
	
	public ContextMapperListener(ContextMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public void containerEvent(ContainerEvent event) {
		
		String type = event.getType();
		Object data = event.getData();
		
		if(type.equals(Container.ADD_CHILD_EVENT)) {
			
			if(data instanceof Wrapper) {
				addChildEvent((Wrapper) data);
			}
			
		} else if(type.equals(Container.REMOVE_CHILD_EVENT)) {
			
			if(data instanceof Wrapper) {
				removeChildEvent((Wrapper) data);
			}
			
		}
	}
	
	private void addChildEvent(Wrapper wrapper) {
		mapper.addWrapper(wrapper);
	}

	private void removeChildEvent(Wrapper wrapper) {
		mapper.removeMapper(wrapper);
	}
}
