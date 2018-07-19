package lzf.webserver.mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月14日 下午5:02:43
* @Description URL路由器
*/
public final class Mapper {

	private final Map<String, Object> mapper = new ConcurrentHashMap<>();
	
	
	public static class Element {
		
	}
}
