package lzf.webserver.naming;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月27日 下午8:53:38
* @Description 类说明
*/
public class Config implements Referenceable, Serializable {

	private static final long serialVersionUID = 1L;
	
	//配置文件中允许配置的"属性"
	protected static Set<String> properties = new HashSet<>();
	
	static {
		properties.add("name");
		properties.add("sources");
	}
	
	private String name;
	
	private String sources;
	
	protected Config() {
	}
	
	protected Config(String name) {
		this.setName(name);
	}

	@Override
	public Reference getReference() throws NamingException {
		
		Reference reference = new Reference(Config.class.getName(), ConfigObjectFactory.class.getName(), null);
		
		reference.add(new StringRefAddr("name", this.name));
		reference.add(new StringRefAddr("name", this.sources));
		
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSources() {
		return sources;
	}

	public void setSources(String sources) {
		this.sources = sources;
	}

	public static boolean contains(String property) {
		return properties.contains(property);
	}
}
