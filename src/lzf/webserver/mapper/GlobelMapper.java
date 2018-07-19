package lzf.webserver.mapper;

import java.util.LinkedHashMap;
import java.util.Map;

import lzf.webserver.Context;
import lzf.webserver.Host;
import lzf.webserver.Service;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��14�� ����5:02:43
* @Description ȫ��URL·������ÿ��Service����ӵ��һ��GlobelMapper����
*/
public final class GlobelMapper {

	private final Service service;
	
	private final Map<String, MappedHost> mapper = new LinkedHashMap<>();
	
	public GlobelMapper(Service service) {
		this.service = service;
	}
	
	/**
	 * �������
	 * @param host
	 */
	public synchronized void addHost(Host host) {
		MappedHost mappedHost = new MappedHost(host.getName(), host);
		mapper.put(host.getName(), mappedHost);
	}
	
	/**
	 * ����Host�����Ƴ�MappedHost����
	 * @param host Host����
	 */
	public synchronized void removeHost(Host host) {
		mapper.remove(host.getName());
	}
	
	/**
	 * �����������Ƴ�MappedHost����
	 * @param hostName
	 */
	public synchronized void removeHost(String hostName) {
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
		return getMappedHost(hostName).object;
	}
	
	/**
	 * ͨ����������uri���Ҷ�Ӧ��Context����
	 * @param hostName ���������磺localhost��www.lzfnb.top
	 * @param uri �������е�URI����
	 * @return Context���� û���ҵ��򷵻�null
	 */
	public Context getContext(String hostName, String uri) {
		MappedHost host = getMappedHost(hostName);
		
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
		//��û���ҵ��ڶ���"/"����˵��������"/demo"������URI��ֱ��ȥ���ַ�����ͷ"/"����
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
	
}