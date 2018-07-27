package lzf.webserver.naming;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月12日 上午10:40:10
 * @Description JNDI上下文工厂类
 */
public class ConfigObjectFactory implements ObjectFactory {
	
	

	@Override
	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment)
			throws Exception {
		return null;
	}

}
