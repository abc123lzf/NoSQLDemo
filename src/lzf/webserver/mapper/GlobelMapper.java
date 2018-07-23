package lzf.webserver.mapper;

import java.util.LinkedHashMap;
import java.util.Map;

import lzf.webserver.Container;
import lzf.webserver.Context;
import lzf.webserver.Host;
import lzf.webserver.Service;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��14�� ����5:02:43
* @Description ȫ��URL·������ÿ��Service����ӵ��һ��GlobelMapper����
* ������������ķ����б���ͨ��Mapped���������
*/
public final class GlobelMapper {
	
	private static final Log log = LogFactory.getLog(GlobelMapper.class);

	private final Service service;
	
	//������������Mappr�����Map
	private final Map<String, MappedHost> mapper = new LinkedHashMap<>();
	
	//����Mapper��Context��������Context��Mapper�����Map
	private final Map<MappedHost, Map<String, MappedContext>> hostMapper = new LinkedHashMap<>();
	
	public GlobelMapper(Service service) {
		this.service = service;
	}
	
	/**
	 * �������
	 * @param host
	 */
	synchronized void addHost(Host host) {
		
		MappedHost mappedHost = new MappedHost(host.getName(), host);
		mapper.put(host.getName(), mappedHost);
		
		Map<String, MappedContext> map = new LinkedHashMap<>();
		
		for(Container c : host.getChildContainers()) {
			MappedContext mc = new MappedContext(c.getName(), (Context)c);
			map.put(c.getName(), mc);
		}
		
		hostMapper.put(mappedHost, map);
	}
	
	/**
	 * ����Host�����Ƴ�MappedHost����
	 * @param host Host����
	 */
	synchronized void removeHost(Host host) {
		removeHost(host.getName());
	}
	
	/**
	 * �����������Ƴ�MappedHost����
	 * @param hostName
	 */
	synchronized void removeHost(String hostName) {
		
		MappedHost mh = getMappedHost(hostName);
		if(mh == null)
			return;
		
		hostMapper.remove(mh);
		mapper.remove(hostName);
	}
	
	/**
	 * �����������ƻ�ȡMappedHost
	 * @param hostName ������
	 * @return MappedHost���� ��û���ҵ��򷵻�null
	 */
	private MappedHost getMappedHost(String hostName) {
		MappedHost host = mapper.get(hostName);
		if(host == null)
			return null;
		return host;
	}
	
	/**
	 * ������������ȡHostʵ��
	 * @param hostName
	 * @return
	 */
	public Host getHost(String hostName) {
		MappedHost mh = getMappedHost(hostName);
		if(mh == null)
			return null;
		return mh.object;
	}
	
	/**
	 * ͨ����������uri���Ҷ�Ӧ��Context����
	 * @param hostName ���������磺localhost��www.lzfnb.top
	 * @param uri �������е�URI����
	 * @return Context���� û���ҵ��򷵻�null
	 */
	public Context getContext(String hostName, String uri) {
		System.out.println(hostName + "......." + uri);
		MappedHost host = getMappedHost(hostName);
		
		//ͨ��������Ѱ��Host�����û���ҵ�������ֱ�ӷ���null
		if(host == null)
			return null;
		
		//���URI����"/"��˵����ROOT
		if(uri.equals("/")) {
			MappedContext mc = hostMapper.get(host).get("ROOT");
			if(mc == null)
				return null;
			return mc.object;
		}
		
		//��ȡ�ڶ���"/"֮ǰ���ַ���
		int st = uri.indexOf('/', 1);
		String contextName;
		//��û���ҵ��ڶ���"/"����˵��������"/demo"������URI��ֱ��ȥ���ַ�����ͷ"/"����
		if(st == -1) {
			contextName = uri.substring(1, uri.length());
		//����ҵ��ˣ���˵���ǽ�Ϊ���ӵ�URL������"/demo/index.jsp"��������ȡ��demo����
		} else {
			contextName = uri.substring(1, st);
		}
		
		//ͨ����ȡ����ַ���(demo)Ѱ��Context
		MappedContext context = getMappedContext(host, contextName);
		
		//�����webapp������(demo) ��ôѡ��ΪROOT
		if(context == null) {
			context = getMappedContext(host, "ROOT");
			if(context == null)
				return null;
		}
		return context.object;
	}
	
	/**
	 * ��·�����м���Contextӳ���ϵ
	 * @param context Context����
	 */
	void addContext(Context context) {
		
		Host host = (Host)context.getParentContainer();
		MappedHost mappedHost = getMappedHost(host.getName());
		
		if(mappedHost == null) {
			log.error("MappedHost ӳ�䶪ʧ?");
			return;
		}
		
		Map<String, MappedContext> map = new LinkedHashMap<>();
		
		for(Container c : host.getChildContainers()) {
			MappedContext mappedContext = new MappedContext(c.getName(), context);
			map.put(c.getName(), mappedContext);
		}
		
		hostMapper.put(mappedHost, map);
	}
	
	/**
	 * ��·�������Ƴ�Contextӳ���ϵ
	 * @param context
	 */
	void removeContext(Context context) {
		Host host = (Host)context.getParentContainer();
		MappedHost mappedHost = getMappedHost(host.getName());
		
		if(mappedHost == null) {
			return;
		}
		
		hostMapper.remove(mappedHost);
	}
	
	private MappedContext getMappedContext(MappedHost mappedHost, String contextName) {
		return hostMapper.get(mappedHost).get(contextName);
	}
	
	public Service getService() {
		return service;
	}
}

/**
 * Mapper��Ӧ��Ԫ�ض���
 * @param <T> ����ʵ��
 */
abstract class MapElement<T> {
	
	public final String name;
	public final T object;
	
	public MapElement(String name, T object) {
		this.name = name;
		this.object = object;
	}
}

final class MappedHost extends MapElement<Host> {

	public MappedHost(String name, Host object) {
		super(name, object);
	}
}

final class MappedContext extends MapElement<Context> {
	
	public MappedContext(String name, Context object) {
		super(name, object);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(!(obj instanceof MappedContext)) 
			return false;
		
		MappedContext mc = (MappedContext)obj;
		
		if(name.equals(mc.name))
			return true;
		return false;
	}
}