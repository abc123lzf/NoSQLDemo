package lzf.webserver.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;
import lzf.webserver.util.StringManager;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月11日 下午7:30:37
 * @Description Web应用类加载器，每个web应用(Context组件)对应一个WebappClassLoader
 */
public class WebappClassLoader extends ClassLoader {
	
	private static final StringManager sm = StringManager.getManager(WebappClassLoader.class);

	private static final Log log = LogFactory.getLog(WebappClassLoader.class);

	//{web-app}/WEB-INF/lib路径
	private final String lib;

	//{web-app}/WEB-INF/classes路径
	private final String classes;

	//存储键为jar包里面的class类名，值为class文件数据的Map
	private final Map<String, byte[]> map = new ConcurrentHashMap<>(128);
	
	//存储Jar包xml、properties等其它文件资源
	private final Map<String, byte[]> fileMap = new ConcurrentHashMap<>(64);

	//之前有没有调用过startRead()方法
	private boolean isLoad = false;
	
	/**
	 * @param parent 父类加载器，一般为系统类加载器
	 * @param webappFolder webapp主目录
	 */
	WebappClassLoader(ClassLoader parent, File webappFolder) {
		super(parent);
		
		this.classes = webappFolder.getAbsolutePath() + File.separator + "WEB-INF" + File.separator + "classes";
		this.lib = webappFolder.getAbsolutePath() + File.separator + "WEB-INF" + File.separator + "lib";
	}
	
	/**
	 * 读取文件，在WebappLoader初始化过程中调用
	 */
	private void startRead() {
		List<File> list = scanLibDir();
		
		for (File f : list) {
			JarFile jar;
			try {
				jar = new JarFile(f);
				readJar(jar);
			} catch (IOException e) {
				log.error(sm.getString("WebappClassLoader.startRead.e0", f.getAbsolutePath()), e);
			}
		}
	}

	/**
	 * 从web应用的lib和classes文件夹加载类
	 * @param name 类名
	 * @return Class对象
	 * @throws ClassNotFoundException 没有在lib和classes目录下找到类文件
	 */
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {

		byte[] result = getClassFromFileOrMap(name);
		
		if (result == null) {
			throw new ClassNotFoundException();
		} else {
			return defineClass(name, result, 0, result.length);
		}
		
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		checkLoad();
		return super.loadClass(name);
	}
	
	/**
	 * 检查此类加载器有没有加载lib目录下的Jar包,如果没有加载则启动加载过程
	 */
	private void checkLoad() {	
		if(!isLoad) {
			synchronized(this) {
				if(!isLoad) {
					startRead();
					isLoad = true;
				}
			}
		}
	}

	/**
	 * @param name 类名
	 * @return 该类文件的二进制数据
	 */
	private byte[] getClassFromFileOrMap(String name) {

		//将类名转换为类的路径
		String classPath = classes + File.separator + name.replace('.', File.separatorChar) + ".class";
		
		File file = new File(classPath);

		//如果在web-inf\classes文件夹下找到了这个class文件
		if (file.exists()) {

			InputStream input = null;
			
			//将文件转换为二进制数组并返回
			try {
				input = new FileInputStream(file);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				int bufferSize = 4096;
				byte[] buffer = new byte[bufferSize];
				int bytesNumRead = 0;

				while ((bytesNumRead = input.read(buffer)) != -1) {
					baos.write(buffer, 0, bytesNumRead);
				}

				return baos.toByteArray();
				
			} catch (FileNotFoundException e) {
				log.error("", e);
			} catch (IOException e) {
				log.error("", e);
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						log.error("", e);
					}
				}
			}

		//如果该web-inf\classes文件夹没有这个类，那么从Jar中的class文件查找
		} else {
			if (map.containsKey(name)) {
				// 去除map中的引用，避免GC无法回收无用的class文件缓存
				return map.remove(name);
			}
		}
		
		//如果都没有找到，直接返回null
		return null;
	}

	/**
	 * @return web-inf\lib目录下所有的JAR文件File对象集合
	 */
	private List<File> scanLibDir() {

		List<File> list = new ArrayList<File>();
		File[] files = new File(lib).listFiles();

		for (File f : files) {
			if (f.isFile() && f.getName().endsWith(".jar"))
				list.add(f);
		}

		return list;
	}

	/**
	 * 读取Jar包中所有的class文件并转换为二进制数据存入Map中
	 * @param jar Jar包文件对象
	 * @throws IOException
	 */
	private void readJar(JarFile jar) throws IOException {

		Enumeration<JarEntry> en = jar.entries();

		while (en.hasMoreElements()) {

			JarEntry je = en.nextElement();
			
			if(je.isDirectory())
				continue;
			
			String name = je.getName();

			if (name.endsWith(".class")) {

				String klass = name.replace(".class", "").replace("/", ".");

				if (this.findLoadedClass(klass) != null)
					continue;

				byte[] content = readJarEntry(jar, je);
				
				map.put(klass, content);
				
			} else {
				
				byte[] content = readJarEntry(jar, je);
				
				fileMap.put(name, content);
			}
		}
	}
	
	
	/**
	 * 将Jar包中的文件转换成二进制数组
	 * @param jar JarFile对象
	 * @param je 该Jar文件的JarEntry对象
	 * @return 二进制数组
	 * @throws IOException 当读取失败时
	 */
	private byte[] readJarEntry(JarFile jar, JarEntry je) throws IOException {
		
		InputStream is = jar.getInputStream(je);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int bufferSize = (int)je.getSize();
		byte[] buffer = new byte[bufferSize];

		int bytesNumRead = 0;
		
		while ((bytesNumRead = is.read(buffer)) != -1) {
			baos.write(buffer, 0, bytesNumRead);
		}

		byte[] cc = baos.toByteArray();
		is.close();
		
		return cc;
	}
	
	
	@Override
	public InputStream getResourceAsStream(String name) {
		
		checkLoad();
		InputStream is = super.getResourceAsStream(name);
		
		if(is != null) {
			return is;
		}
		
		byte[] b = fileMap.get(name);
		
		if(b != null) {
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
			return bais;
		}
		
		return null;
	}
}
