package lzf.webserver.naming;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

/**
 * @author ���ӷ�
 * @version 1.0
 * @date 2018��7��12�� ����10:40:10
 * @Description JNDI�����Ĺ�����
 */
public class ConfigObjectFactory implements ObjectFactory {
	
	

	@Override
	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment)
			throws Exception {
		return null;
	}

}
