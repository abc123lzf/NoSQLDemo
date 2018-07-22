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
		
		if(context.getName().equals("ROOT")) {
			return mapper.get(uri).object;
		}
		
		//��ȡ�ڶ���"/"֮ǰ���ַ���
		int st = uri.indexOf('/', 1);
		//��û���ҵ��ڶ���"/"����˵��������"/demo"������URI��ֱ��ȥ���ַ�����ͷ"/"����
		if(st == -1) {
			return mapper.get("index.html").object;
		//����ҵ��ˣ���˵���ǽ�Ϊ���ӵ�URL������"/demo/index.jsp"��������ȡ��demo����
		} else {
			return mapper.get(uri.substring(st, uri.length() - 1)).object;
		}
	}
	
	void addWrapper(String uri, Wrapper wrapper) {
		mapper.put(uri, new MappedWrapper(uri, wrapper));
	}
	
}

class MappedWrapper extends MapElement<Wrapper> {
	
	public MappedWrapper(String name, Wrapper object) {
		super(name, object);
	}
	
}