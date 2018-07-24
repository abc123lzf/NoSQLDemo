package lzf.webserver.loader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月11日 下午7:30:37
 * @Description Web应用类加载器，每个web应用(Context组件)对应一个WebappClassLoader
 */
public class WebappClassLoader extends ClassLoader {

	public static final Log log = LogFactory.getLog(WebappClassLoader.class);

	//{web-app}/WEB-INF/lib路径
	private final String lib;

	//{web-app}/WEB-INF/classes路径
	private final String classes;

	//存储键为jar包里面的class类名，值为class文件数据的Map
	private final Map<String, byte[]> map = new HashMap<>(64);

	//之前有没有调用过startRead()方法
	private volatile boolean isLoad = false;
	
	/**
	 * @param parent 父类加载器，一般为系统类加载器
	 * @param webappFolder webapp主目录
	 */
	public WebappClassLoader(ClassLoader parent, File webappFolder) {
		super(parent);
		this.classes = webappFolder.getAbsolutePath() + File.separator + "WEB-INF" + File.separator + "classes";
		this.lib = webappFolder.getAbsolutePath() + File.separator + "WEB-INF" + File.separator + "lib";
		System.out.println(classes + " " +lib); 
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
				log.error("读取Jar包：" + f.getPath() + "发生异常", e);
			}
		}
	}

	/**
	 * 从web应用的lib和classes文件夹加载类
	 * @param name 类名
	 * @return Class对象，如果未找到则抛出FileNotFoundException
	 */
	@Override
	public Class<?> findClass(String name) {
		
		if(!isLoad) {
			startRead();
			isLoad = true;
		}
		
		System.out.println(name);
		try {
			byte[] result = getClassFromFileOrMap(name);
			if (result == null) {
				throw new FileNotFoundException();
			} else {
				return defineClass(name, result, 0, result.length);
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
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
				// 去除map中的引用，避免GC无法回收无用的class文件
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
			
			String name = je.getName();

			if (name.endsWith(".class")) {

				String klass = name.replace(".class", "").replaceAll("/", ".");

				if (this.findLoadedClass(klass) != null)
					continue;

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
				map.put(klass, cc);
			}
		}
	}
	
}
