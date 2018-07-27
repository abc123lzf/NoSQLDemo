package lzf.webserver.naming;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月27日 下午9:15:06
* @Description 类说明
*/
public class ConfigInitialContextFactory implements InitialContextFactory {
	
	protected static final String PREFIX = "config.";
	
	protected static final String NAME_SUFFIX = ".name";
	
	protected static final String SOURCES_SUFFIX = ".sources";
	
	@Override
	public Context getInitialContext(Hashtable<?, ?> env) throws NamingException {
		return null;
	}

}
