package lzf.webserver.loader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��11�� ����7:30:37
* @Description WebӦ�����������ÿ��webӦ��(Context���)��Ӧһ��WebappClassLoader
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
