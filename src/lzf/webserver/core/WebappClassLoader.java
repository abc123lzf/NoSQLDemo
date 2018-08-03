package lzf.webserver.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;
import lzf.webserver.util.StringManager;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月11日 下午7:30:37
 * @Description Web应用类加载器，每个web应用(Context组件)对应一个WebappClassLoader
 */
public final class WebappClassLoader extends URLClassLoader {
	
	private static final StringManager sm = StringManager.getManager(WebappClassLoader.class);

	private static final Log log = LogFactory.getLog(WebappClassLoader.class);

	//{web-app}/WEB-INF/lib路径
	final String lib;

	//{web-app}/WEB-INF/classes路径
	final String classes;
	
	/**
	 * @param parent 父类加载器，一般为系统类加载器
	 * @param webappFolder webapp主目录
	 * @param libURLs WEB-INF\lib下所有Jar包的数组
	 */
	WebappClassLoader(ClassLoader parent, File webappFolder, URL[] urls) {
		super(urls, parent);
		this.lib = webappFolder.getAbsolutePath() + File.separator + "WEB-INF" + File.separator + "lib";
		this.classes = webappFolder.getAbsolutePath() + File.separator + "WEB-INF" + File.separator + "classes";
	}


	/**
	 * 从web应用的lib和classes文件夹加载类
	 * @param name 类名
	 * @return Class对象
	 * @throws ClassNotFoundException 没有在lib和classes目录下找到类文件
	 */
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {

		try {
			Class<?> klass = super.findClass(name);
			return klass;
		} catch(ClassNotFoundException e) {
			String classPath = classes + File.separator + name.replace('.', 
					File.separatorChar) + ".class";
			
			File file = new File(classPath);
			
			if(!file.exists())
				throw new ClassNotFoundException();
			
			InputStream input = null;
			try {
				input = new FileInputStream(file);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int read = 0;
				
				byte[] b = new byte[(int)file.length()];

				while((read = input.read(b)) != -1) {
					baos.write(b, 0, read);
				}

				return this.defineClass(name, b, 0, b.length);
			} catch (FileNotFoundException e0) {
				throw new ClassNotFoundException();
			} catch (IOException e0) {
				log.error("" , e0);
				
			}finally {
				try {
					input.close();
				} catch (IOException e1) {
					log.warn(sm.getString("WebappClassLoader.findClass.e0"), e1);
				}
			}
			
			throw new ClassNotFoundException();
		}
	}
	
	/*
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		System.out.println(name);
		return super.loadClass(name);
	}
	
	@Override
	public URL getResource(String name) {
		System.out.println(name);
		return super.getResource(name);
	}
	
	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		Enumeration<URL> en = super.getResources(name);
		System.out.println("r:" + name + " " + en.hasMoreElements());
		return en;
	}*/
}
