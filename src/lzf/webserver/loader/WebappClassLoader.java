package lzf.webserver.loader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月11日 下午7:30:37
* @Description Web应用类加载器，每个web应用(Context组件)对应一个WebappClassLoader
*/
public class WebappClassLoader extends URLClassLoader {

	private String name;
	
	public WebappClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}
	
	@Override
	public String toString() {
		return "WebappClassLoader:" + name;
	}
	
	
	@Override
	public Class<?> findClass(String name) {
		InputStream is = null;
		byte[] data = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		return null;
	}
}
