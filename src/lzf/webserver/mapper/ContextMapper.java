package lzf.webserver.mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lzf.webserver.Context;
import lzf.webserver.Wrapper;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;
import lzf.webserver.util.StringManager;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月19日 下午8:35:12
* @Description 类说明
*/
public final class ContextMapper {
	
	private static final Log log = LogFactory.getLog(ContextMapper.class);

	private static final StringManager sm = StringManager.getManager(ContextMapper.class);
	
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
			
			//是不是尝试访问主页
			if(uri.equals("/")) {
				//mw = mapper.get("/index.html");
				mw = getWelcomeWrapper();
				if(mw != null)
					return mw.object;
				
				return null;
			} 
			
			//精确查找
			mw = mapper.get(uri);
			
			//如果没有找到则采取模糊查找
			if(mw == null) {
				
				for(Map.Entry<String, MappedWrapper> pattern : patternMapper.entrySet()) {
					if(matcher(uri,pattern.getKey()))
						return pattern.getValue().object;
				}
				
				return null;
			}
			
			return mw.object;
		}
		
		//截取第二个"/"之前的字符串
		int st = uri.indexOf('/', 1);
		
		//若没有找到第二个"/"，则说明尝试访问主页
		if(st == -1) {
			MappedWrapper mw = getWelcomeWrapper();
			
			if(mw == null)
				return null;
			
			return mw.object;
		//如果找到了，则说明是较为复杂的URL，类似"/demo/index.jsp"这样，截取出/index.jsp即可
		} else {
			
			//如果URI为这种格式"/demo/"
			if(uri.equals("/" + context.getName() + "/")) {
				
				MappedWrapper mw = getWelcomeWrapper();
				if(mw == null)
					return null;
				return mw.object;
			}
			
			MappedWrapper mw = mapper.get(uri);
			
			if(mw == null) {
				
				for(Map.Entry<String, MappedWrapper> pattern : patternMapper.entrySet()) {
					System.out.println(pattern.getKey());
					if(matcher(uri,pattern.getKey()))
						return pattern.getValue().object;
				}
				
				return null;
			}
			
			return mw.object;
		}
	}
	
	/**
	 * 当用户访问首页URI时，查找主页
	 * @return 主页MappedWrapper对象
	 */
	private MappedWrapper getWelcomeWrapper() {
		
		MappedWrapper mw = null;
		
		//获取Context容器的欢迎页面集合
		List<String> welcomeFileList = context.getWelcomeFileList();
		
		String contextName = "/";
		
		//如果不是ROOT web应用则将context名称设置为"/${contextName}/"
		if(!rootApp)
			contextName += context.getName() + "/";
		
		//先从欢迎文件页面集合查找
		if(welcomeFileList != null) {
			
			for(String file : welcomeFileList) {
				mw = mapper.get(contextName + file);
				
				if(mw != null)
					return mw;
			}
		}
		
		//查找主目录下的index.html
		mw = mapper.get(contextName + "index.html");
		if(mw != null)
			return mw;
		
		//查找主目录下的index.jsp
		mw = mapper.get(contextName + "index.jsp");
		if(mw != null)
			return mw;
		
		//查找index
		mw = mapper.get(contextName + "index");
		if(mw != null)
			return mw;
		
		//如果都没找到则采用模糊匹配表查询
		for(Map.Entry<String, MappedWrapper> pattern : patternMapper.entrySet()) {
			if(contextName.matches(pattern.getKey()))
				return pattern.getValue();
		}
		
		return null;
	}
	
	/**
	 * 向映射表中添加Wrapper容器的映射，此方法必须由ContextMappedListener监听器调用
	 * @param wrapper Wrapper对象
	 */
	void addWrapper(Wrapper wrapper) {
		
		checkRootApp();
		
		List<String> uriPatterns = wrapper.getURIPatterns();
		
		if(uriPatterns == null || uriPatterns.isEmpty())
			log.warn(sm.getString("ContextMapper.addWrapper.e0", wrapper.getName()));
		
		if(rootApp) {
		
			for(String uriPattern : uriPatterns) {
				//System.out.println(uriPattern);
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
	
	private boolean matcher(String uri, String pattern) {
		int index = pattern.indexOf('*');
		
		String st = pattern.substring(0, index);
		String ed = pattern.substring(index + 1, pattern.length());
		
		if(st.equals("")) {
			if(uri.endsWith(ed))
				return true;
			return false;
		}
		
		if(ed.equals("")) {
			if(uri.startsWith(st))
				return true;
			return false;
		}
		
		if(uri.startsWith(st) && uri.endsWith(ed))
			return true;
		return false;
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