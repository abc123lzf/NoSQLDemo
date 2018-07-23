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
* @author 李子帆
* @version 1.0
* @date 2018年7月14日 下午5:02:43
* @Description 全局URL路由器，每个Service对象拥有一个GlobelMapper对象
* 以下添加容器的方法中必须通过Mapped监听器完成
*/
public final class GlobelMapper {
	
	private static final Log log = LogFactory.getLog(GlobelMapper.class);

	private final Service service;
	
	//主机名和主机Mappr对象的Map
	private final Map<String, MappedHost> mapper = new LinkedHashMap<>();
	
	//主机Mapper和Context容器名和Context的Mapper对象的Map
	private final Map<MappedHost, Map<String, MappedContext>> hostMapper = new LinkedHashMap<>();
	
	public GlobelMapper(Service service) {
		this.service = service;
	}
	
	/**
	 * 添加主机
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
	 * 根据Host对象移除MappedHost对象
	 * @param host Host对象
	 */
	synchronized void removeHost(Host host) {
		removeHost(host.getName());
	}
	
	/**
	 * 根据主机名移除MappedHost对象
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
		MappedHost mh = getMappedHost(hostName);
		if(mh == null)
			return null;
		return mh.object;
	}
	
	/**
	 * 通过主机名、uri查找对应的Context对象
	 * @param hostName 主机名，如：localhost、www.lzfnb.top
	 * @param uri 请求行中的URI属性
	 * @return Context对象 没有找到则返回null
	 */
	public Context getContext(String hostName, String uri) {
		System.out.println(hostName + "......." + uri);
		MappedHost host = getMappedHost(hostName);
		
		//通过主机名寻找Host，如果没有找到主机则直接返回null
		if(host == null)
			return null;
		
		//如果URI等于"/"，说明是ROOT
		if(uri.equals("/")) {
			MappedContext mc = hostMapper.get(host).get("ROOT");
			if(mc == null)
				return null;
			return mc.object;
		}
		
		//截取第二个"/"之前的字符串
		int st = uri.indexOf('/', 1);
		String contextName;
		//若没有找到第二个"/"，则说明是类似"/demo"这样的URI，直接去除字符串开头"/"即可
		if(st == -1) {
			contextName = uri.substring(1, uri.length());
		//如果找到了，则说明是较为复杂的URL，类似"/demo/index.jsp"这样，截取出demo即可
		} else {
			contextName = uri.substring(1, st);
		}
		
		//通过截取后的字符串(demo)寻找Context
		MappedContext context = getMappedContext(host, contextName);
		
		//如果该webapp不存在(demo) 那么选定为ROOT
		if(context == null) {
			context = getMappedContext(host, "ROOT");
			if(context == null)
				return null;
		}
		return context.object;
	}
	
	/**
	 * 向路由器中加入Context映射关系
	 * @param context Context容器
	 */
	void addContext(Context context) {
		
		Host host = (Host)context.getParentContainer();
		MappedHost mappedHost = getMappedHost(host.getName());
		
		if(mappedHost == null) {
			log.error("MappedHost 映射丢失?");
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
	 * 向路由器中移除Context映射关系
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