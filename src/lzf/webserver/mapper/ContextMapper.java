package lzf.webserver.mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lzf.webserver.Context;
import lzf.webserver.Wrapper;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月19日 下午8:35:12
* @Description 类说明
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
		
		//截取第二个"/"之前的字符串
		int st = uri.indexOf('/', 1);
		//若没有找到第二个"/"，则说明是类似"/demo"这样的URI，直接去除字符串开头"/"即可
		if(st == -1) {
			return mapper.get("index.html").object;
		//如果找到了，则说明是较为复杂的URL，类似"/demo/index.jsp"这样，截取出demo即可
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