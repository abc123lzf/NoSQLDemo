package lzf.webserver.mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lzf.webserver.Context;
import lzf.webserver.Wrapper;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��19�� ����8:35:12
* @Description ��˵��
*/
public final class ContextMapper {

	private final Context context;
	
	private final Map<String, MappedWrapper> mapper = new ConcurrentHashMap<>();
	
	public ContextMapper(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}
	
	public Wrapper getWrapper(String uri) {
		return mapper.get(uri).object;
	}
	
	public void addWrapper(String uri, Wrapper wrapper) {
		mapper.put(uri, new MappedWrapper(uri, wrapper));
	}
}

class MappedWrapper extends MapElement<Wrapper> {
	
	public MappedWrapper(String name, Wrapper object) {
		super(name, object);
	}
	
}