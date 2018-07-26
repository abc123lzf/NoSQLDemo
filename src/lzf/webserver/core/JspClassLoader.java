package lzf.webserver.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月26日 下午2:30:47
* @Description JSP类加载器，负责加载WebappLoader编译好的JSP类
*/
public class JspClassLoader extends ClassLoader {
	
	public static final Log log = LogFactory.getLog(JspClassLoader.class);
	
	private final String jspWorkPath;
	
	private final Map<String, byte[]> map = new ConcurrentHashMap<>(24);
	
	private volatile boolean isLoad = false;
	/**
	 * 
	 * @param parent 父类加载器，设置为WebappClassLoader
	 * @param jspWorkPath 编译好的jsp class文件存放路径,结构：/work/${HostName}/${contextName}
	 */
	JspClassLoader(ClassLoader parent, File jspWorkPath) {
		
		super(parent);
		
		if(!jspWorkPath.exists())
			jspWorkPath.mkdir();
		
		this.jspWorkPath = jspWorkPath.getAbsolutePath();
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		
		if(!isLoad) {
			startRead(new File(jspWorkPath));
			isLoad = true;
		}
		
		byte[] classBytes = map.get(name);
			
		if(classBytes == null) {
			throw new ClassNotFoundException();
		} else {
			map.remove(name);
			return defineClass(name, classBytes, 0, classBytes.length);
		}
		
	}
	
	/**
	 * 将所有编译好的JSP class文件读入内存
	 * @param file JSP class文件工作路径，在本服务器中，路径为/work/${HostName}/${contextName}
	 */
	private void startRead(File file) {
		
		if(file.exists()) {
			File[] files = file.listFiles();
			
			if(files.length == 0) {
				return;
			} else {
				
				for(File file2 : files) {
					
					if(file2.isDirectory()) {
						startRead(file2);
					} else {
						if(file2.getName().endsWith(".class")) {
							
							String klass = file2.getAbsolutePath().replace(jspWorkPath + File.separator, "")
									.replace(".class", "").replace(File.separator, ".");
							
							if(this.findLoadedClass(klass) != null)
								continue;
						
							byte[] b = loadJspClassFile(file2);
							if(b == null)
								continue;
							
							map.put(klass, b);
						}
					}
				}
			}
		}
		
	}
	
	/**
	 * 读取编译好的JSP类文件并转换为字节数组
	 * @param file 该JSP类文件路径
	 * @return 该JSP类文件的字节数组
	 */
	private byte[] loadJspClassFile(File file) {
		
		try {
			@SuppressWarnings("resource")
			FileInputStream fis = new FileInputStream(file);
			
			byte[] b = new byte[(int) file.length()];
			fis.read(b);
			return b;
			
		} catch (FileNotFoundException e) {
			log.error("找不到资源文件：" + file.getAbsolutePath(), e);
		} catch (IOException e) {
			log.error("无法读入资源文件：" + file.getAbsolutePath() , e);
		}
		
		return null;
	}
}
