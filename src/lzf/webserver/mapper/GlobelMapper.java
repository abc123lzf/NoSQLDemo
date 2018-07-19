package lzf.webserver.mapper;

import java.util.LinkedHashMap;
import java.util.Map;

import lzf.webserver.Context;
import lzf.webserver.Host;
import lzf.webserver.Service;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月14日 下午5:02:43
* @Description 全局URL路由器，每个Service对象拥有一个GlobelMapper对象
*/
public final class GlobelMapper {

	private final Service service;
	
	private final Map<String, MappedHost> mapper = new LinkedHashMap<>();
	
	public GlobelMapper(Service service) {
		this.service = service;
	}
	
	/**
	 * 添加主机
	 * @param host
	 */
	public synchronized void addHost(Host host) {
		MappedHost mappedHost = new MappedHost(host.getName(), host);
		mapper.put(host.getName(), mappedHost);
	}
	
	/**
	 * 根据Host对象移除MappedHost对象
	 * @param host Host对象
	 */
	public synchronized void removeHost(Host host) {
		mapper.remove(host.getName());
	}
	
	/**
	 * 根据主机名移除MappedHost对象
	 * @param hostName
	 */
	public synchronized void removeHost(String hostName) {
		mapper.remove(hostName);
	}
	
	/**
	 * 根据主机名称获取MappedHost
	 * @param hostName 主机名
	 * @return MappedHost对象 如没有找到则返回null
	 */
	private MappedHost getMappedHost(String hostName) {
		MappedHost host = mapper.get(hostName);
		if(host == null)
			return null;
		return host;
	}
	
	/**
	 * 根据主机名获取Host实例
	 * @param hostName
	 * @return
	 */
	public Host getHost(String hostName) {
		return getMappedHost(hostName).object;
	}
	
	/**
	 * 通过主机名、uri查找对应的Context对象
	 * @param hostName 主机名，如：localhost、www.lzfnb.top
	 * @param uri 请求行中的URI属性
	 * @return Context对象 没有找到则返回null
	 */
	public Context getContext(String hostName, String uri) {
		MappedHost host = getMappedHost(hostName);
		
		//通过主机名寻找Host，如果没有找到主机则直接返回null
		if(host == null)
			return null;
		
		//如果URI等于"/"，说明是ROOT
		if(uri.equals("/")) {
			return (Context) host.object.getChildContainer("ROOT");
		}
		
		//截取第二个"/"之前的字符串
		int st = uri.indexOf('/', 1);
		String contextName;
		//若没有找到第二个"/"，则说明是类似"/demo"这样的URI，直接去除字符串开头"/"即可
		if(st == -1) {
			contextName = uri.substring(1, uri.length() - 1);
		//如果找到了，则说明是较为复杂的URL，类似"/demo/index.jsp"这样，截取出demo即可
		} else {
			contextName = uri.substring(1, st);
		}
		//通过截取后的字符串寻找Context
		Context context = (Context) host.object.getChildContainer(contextName);
		if(context != null)
			return context;
		//如果没有找到，则转至ROOT目录
		return (Context) host.object.getChildContainer("ROOT");
	}
	
	public Service getService() {
		return service;
	}
}

/**
 * Mapper对应的元素对象
 * @param <T> 容器实例
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