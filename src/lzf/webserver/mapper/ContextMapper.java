package lzf.webserver.mapper;

import java.util.LinkedHashMap;
import java.util.List;
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
	
	//键存储URI路径信息，比如"blog/my.html"，该map用于匹配精准URL路径
	private final Map<String, MappedWrapper> mapper = new ConcurrentHashMap<>();
	
	//键存储URI匹配规则，用于模糊匹配查询，比如url-pattern中参数为/demo/*.jsp
	private final Map<String, MappedWrapper> patternMapper = new LinkedHashMap<>();
	
	private Boolean rootApp = null;
	
	public ContextMapper(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}
	
	/**
	 * @param uri 请求行中的完整URI
	 * @return 对应的Wrapper容器，如果没有找到则返回null
	 */
	public Wrapper getWrapper(String uri) {
		
		checkRootApp();

		if(rootApp) {
			MappedWrapper mw = null;
			
			if(uri.equals("/")) {
				mw = mapper.get("/index.html");
			} else {
				mw = mapper.get(uri);
			}
			
			if(mw == null) {
				
				for(Map.Entry<String, MappedWrapper> pattern : patternMapper.entrySet()) {
					if(uri.matches(pattern.getKey()))
						return pattern.getValue().object;
				}
				return null;
			}
			
			return mw.object;
		}
		
		//截取第二个"/"之前的字符串
		int st = uri.indexOf('/', 1);
		
		//若没有找到第二个"/"，则说明是类似"/demo"这样的URI，直接去除字符串开头"/"即可
		if(st == -1) {
			MappedWrapper mw = mapper.get(uri + "/index.html");
			
			if(mw == null)
				return null;
			
			return mw.object;
		//如果找到了，则说明是较为复杂的URL，类似"/demo/index.jsp"这样，截取出/index.jsp即可
		} else {
			
			//如果URI为这种格式"/demo/"
			if(uri.equals("/" + context.getName() + "/")) {
				
				MappedWrapper mw = mapper.get("/" + context.getName() + "/index.html");
				if(mw == null)
					return null;
				return mw.object;
			}
			
			MappedWrapper mw = mapper.get(uri);
			
			if(mw == null)
				return null;
			
			return mw.object;
		}
	}
	
	/**
	 * 向映射表中添加Wrapper容器的映射，此方法必须由ContextMappedListener监听器调用
	 * @param wrapper Wrapper对象
	 */
	void addWrapper(Wrapper wrapper) {
		
		checkRootApp();
		
		List<String> uriPatterns = wrapper.getURIPatterns();
		
		if(uriPatterns == null || uriPatterns.isEmpty())
			throw new UnsupportedOperationException("The wrapper's URI path is empty");
		
		if(rootApp) {
		
			for(String uriPattern : uriPatterns) {
				
				//检查URL是否包含通配符
				if(uriPattern.indexOf('*') == -1)
					mapper.put(uriPattern, new MappedWrapper(uriPattern, wrapper));
				else
					patternMapper.put(uriPattern, new MappedWrapper(uriPattern, wrapper));
				
			}
			
		} else {
			
			for(String uriPattern : uriPatterns) {
				
				if(uriPattern.indexOf('*') == -1)
					mapper.put(uriPattern, new MappedWrapper(uriPattern, wrapper));
				else
					patternMapper.put(uriPattern , new MappedWrapper(uriPattern, wrapper));
			}
		}
	}
	
	/**
	 * 向映射表中移除Wrapper容器的映射，此方法必须由ContextMappedListener监听器调用
	 * @param wrapper Wrapper对象
	 */
	void removeMapper(Wrapper wrapper) {
		
		List<String> uriPatterns = wrapper.getURIPatterns();
		
		if(uriPatterns == null || uriPatterns.isEmpty())
			return;
		
		String prefix = "";
		
		if(!rootApp) {
			prefix = "/" + context.getName();
		}
		
		for(String uriPattern : uriPatterns) {
			mapper.remove(prefix + uriPattern);
			patternMapper.remove(prefix + uriPattern);
		}
		
	}
	
	private void checkRootApp() {
		
		if(rootApp == null) {
			if(context.getName().equals("ROOT"))
				rootApp = true;
			else
				rootApp = false;
		}
	}
}

class MappedWrapper extends MapElement<Wrapper> {
	
	public MappedWrapper(String uri, Wrapper object) {
		super(uri, object);
	}
	
}