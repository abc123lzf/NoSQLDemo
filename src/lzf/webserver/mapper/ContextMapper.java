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
* @author ���ӷ�
* @version 1.0
* @date 2018��7��19�� ����8:35:12
* @Description ��˵��
*/
public final class ContextMapper {
	
	private static final Log log = LogFactory.getLog(ContextMapper.class);

	private static final StringManager sm = StringManager.getManager(ContextMapper.class);
	
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
			
			//�ǲ��ǳ��Է�����ҳ
			if(uri.equals("/")) {
				//mw = mapper.get("/index.html");
				mw = getWelcomeWrapper();
				if(mw != null)
					return mw.object;
				
				return null;
			} 
			
			//��ȷ����
			mw = mapper.get(uri);
			
			//���û���ҵ����ȡģ������
			if(mw == null) {
				
				for(Map.Entry<String, MappedWrapper> pattern : patternMapper.entrySet()) {
					if(matcher(uri,pattern.getKey()))
						return pattern.getValue().object;
				}
				
				return null;
			}
			
			return mw.object;
		}
		
		//��ȡ�ڶ���"/"֮ǰ���ַ���
		int st = uri.indexOf('/', 1);
		
		//��û���ҵ��ڶ���"/"����˵�����Է�����ҳ
		if(st == -1) {
			MappedWrapper mw = getWelcomeWrapper();
			
			if(mw == null)
				return null;
			
			return mw.object;
		//����ҵ��ˣ���˵���ǽ�Ϊ���ӵ�URL������"/demo/index.jsp"��������ȡ��/index.jsp����
		} else {
			
			//���URIΪ���ָ�ʽ"/demo/"
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
	 * ���û�������ҳURIʱ��������ҳ
	 * @return ��ҳMappedWrapper����
	 */
	private MappedWrapper getWelcomeWrapper() {
		
		MappedWrapper mw = null;
		
		//��ȡContext�����Ļ�ӭҳ�漯��
		List<String> welcomeFileList = context.getWelcomeFileList();
		
		String contextName = "/";
		
		//�������ROOT webӦ����context��������Ϊ"/${contextName}/"
		if(!rootApp)
			contextName += context.getName() + "/";
		
		//�ȴӻ�ӭ�ļ�ҳ�漯�ϲ���
		if(welcomeFileList != null) {
			
			for(String file : welcomeFileList) {
				mw = mapper.get(contextName + file);
				
				if(mw != null)
					return mw;
			}
		}
		
		//������Ŀ¼�µ�index.html
		mw = mapper.get(contextName + "index.html");
		if(mw != null)
			return mw;
		
		//������Ŀ¼�µ�index.jsp
		mw = mapper.get(contextName + "index.jsp");
		if(mw != null)
			return mw;
		
		//����index
		mw = mapper.get(contextName + "index");
		if(mw != null)
			return mw;
		
		//�����û�ҵ������ģ��ƥ����ѯ
		for(Map.Entry<String, MappedWrapper> pattern : patternMapper.entrySet()) {
			if(contextName.matches(pattern.getKey()))
				return pattern.getValue();
		}
		
		return null;
	}
	
	/**
	 * ��ӳ��������Wrapper������ӳ�䣬�˷���������ContextMappedListener����������
	 * @param wrapper Wrapper����
	 */
	void addWrapper(Wrapper wrapper) {
		
		checkRootApp();
		
		List<String> uriPatterns = wrapper.getURIPatterns();
		
		if(uriPatterns == null || uriPatterns.isEmpty())
			log.warn(sm.getString("ContextMapper.addWrapper.e0", wrapper.getName()));
		
		if(rootApp) {
		
			for(String uriPattern : uriPatterns) {
				//System.out.println(uriPattern);
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