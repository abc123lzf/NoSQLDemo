package lzf.webserver.mapper;

import java.util.LinkedHashMap;
import java.util.List;
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
	
	//���洢URI·����Ϣ������"blog/my.html"����map����ƥ�侫׼URL·��
	private final Map<String, MappedWrapper> mapper = new ConcurrentHashMap<>();
	
	//���洢URIƥ���������ģ��ƥ���ѯ������url-pattern�в���Ϊ/demo/*.jsp
	private final Map<String, MappedWrapper> patternMapper = new LinkedHashMap<>();
	
	private Boolean rootApp = null;
	
	public ContextMapper(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}
	
	/**
	 * @param uri �������е�����URI
	 * @return ��Ӧ��Wrapper���������û���ҵ��򷵻�null
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
		
		//��ȡ�ڶ���"/"֮ǰ���ַ���
		int st = uri.indexOf('/', 1);
		
		//��û���ҵ��ڶ���"/"����˵��������"/demo"������URI��ֱ��ȥ���ַ�����ͷ"/"����
		if(st == -1) {
			MappedWrapper mw = mapper.get(uri + "/index.html");
			
			if(mw == null)
				return null;
			
			return mw.object;
		//����ҵ��ˣ���˵���ǽ�Ϊ���ӵ�URL������"/demo/index.jsp"��������ȡ��/index.jsp����
		} else {
			
			//���URIΪ���ָ�ʽ"/demo/"
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
	 * ��ӳ��������Wrapper������ӳ�䣬�˷���������ContextMappedListener����������
	 * @param wrapper Wrapper����
	 */
	void addWrapper(Wrapper wrapper) {
		
		checkRootApp();
		
		List<String> uriPatterns = wrapper.getURIPatterns();
		
		if(uriPatterns == null || uriPatterns.isEmpty())
			throw new UnsupportedOperationException("The wrapper's URI path is empty");
		
		if(rootApp) {
		
			for(String uriPattern : uriPatterns) {
				
				//���URL�Ƿ����ͨ���
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
	 * ��ӳ������Ƴ�Wrapper������ӳ�䣬�˷���������ContextMappedListener����������
	 * @param wrapper Wrapper����
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