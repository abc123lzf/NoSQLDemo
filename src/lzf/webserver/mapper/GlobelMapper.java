package lzf.webserver.mapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lzf.webserver.Context;
import lzf.webserver.Host;
import lzf.webserver.Service;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��14�� ����5:02:43
* @Description ȫ��URL·����
*/
public final class GlobelMapper {

	private final Service service;
	
	private final Map<String, MappedHost> mapper = new LinkedHashMap<>();
	
	private String defaultHostName = null;
	
	private MappedHost defaultHost = null;
	
	public GlobelMapper(Service service) {
		this.service = service;
	}
	
	public synchronized void addHost(Host host) {
		MappedHost mappedHost = new MappedHost(host.getName(), host);
		mapper.put(host.getName(), mappedHost);
	}
	
	public synchronized void removeHost(Host host) {
		mapper.remove(host.getName());
	}
	
	public synchronized void removeHost(String hostName) {
		mapper.remove(hostName);
	}
	
	private MappedHost getHost(String hostName) {
		MappedHost host = mapper.get(hostName);
		if(host == null)
			return null;
		return host;
	}
	
	/**
	 * ͨ����������uri���Ҷ�Ӧ��Context����
	 * @param hostName ���������磺localhost��www.lzfnb.top
	 * @param uri �������е�URI����
	 * @return Context���� û���ҵ��򷵻�null
	 */
	public Context getContext(String hostName, String uri) {
		MappedHost host = getHost(hostName);
		
		//ͨ��������Ѱ��Host�����û���ҵ�������ֱ�ӷ���null
		if(host == null)
			return null;
		
		//���URI����"/"��˵����ROOT
		if(uri.equals("/")) {
			return (Context) host.object.getChildContainer("ROOT");
		}
		
		//��ȡ�ڶ���"/"֮ǰ���ַ���
		int st = uri.indexOf('/', 1);
		String contextName;
		//��û���ҵ��ڶ���"/"����˵��������"/demo"������uri��ֱ��ȥ���ַ�����ͷ"/"����
		if(st == -1) {
			contextName = uri.substring(1, uri.length() - 1);
		//����ҵ��ˣ���˵���ǽ�Ϊ���ӵ�URL������"/demo/index.jsp"��������ȡ��demo����
		} else {
			contextName = uri.substring(1, st);
		}
		//ͨ����ȡ����ַ���Ѱ��Context
		Context context = (Context) host.object.getChildContainer(contextName);
		if(context != null)
			return context;
		//���û���ҵ�����ת��ROOTĿ¼
		return (Context) host.object.getChildContainer("ROOT");
	}
}

abstract class MapElement<T> {
	
	public final String name;
	public final T object;
	
	public MapElement(String name, T object) {
		this.name = name;
		this.object = object;
	}
}

final class MappedHost extends MapElement<Host> {
	public String defaultContextName;
	
	public MappedHost(String name, Host object) {
		super(name, object);
	}
}

final class MappedContext extends MapElement<Context> {
	
	public MappedContext(String name, Context object) {
		super(name, object);
	}
	
}