package lzf.webserver.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tomcat.util.res.StringManager;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��26�� ����2:30:47
* @Description JSP����������������WebappLoader����õ�JSP��
*/
public class JspClassLoader extends ClassLoader {
	
	private static final StringManager sm = StringManager.getManager(JspClassLoader.class);
	
	private static final Log log = LogFactory.getLog(JspClassLoader.class);
	
	private final String jspWorkPath;
	
	private final Map<String, byte[]> map = new ConcurrentHashMap<>(24);
	
	private boolean isLoad = false;
	
	/**
	 * @param parent ���������������ΪWebappClassLoader
	 * @param jspWorkPath ����õ�jsp class�ļ����·��,�ṹ��/work/${HostName}/${contextName}
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
			synchronized(this) {
				if(!isLoad) {
					startRead(new File(jspWorkPath));
					isLoad = true;
				}
			}
		}
		
		byte[] classBytes = map.get(name);
			
		if(classBytes == null) {
			throw new ClassNotFoundException(sm.getString("JspClassLoader.findClass.e0", name));
		} else {
			map.remove(name);
			return defineClass(name, classBytes, 0, classBytes.length);
		}
		
	}
	
	/**
	 * �����б���õ�JSP class�ļ������ڴ�
	 * @param file JSP class�ļ�����·�����ڱ��������У�·��Ϊ/work/${HostName}/${contextName}
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
	 * ��ȡ����õ�JSP���ļ���ת��Ϊ�ֽ�����
	 * @param file ��JSP���ļ�·��
	 * @return ��JSP���ļ����ֽ�����
	 */
	private byte[] loadJspClassFile(File file) {
		
		try {
			@SuppressWarnings("resource")
			FileInputStream fis = new FileInputStream(file);
			
			byte[] b = new byte[(int) file.length()];
			fis.read(b);
			return b;
			
		} catch (FileNotFoundException e) {
			log.error(sm.getString("JspClassLoader.loadJspClassFile.e0", file.getAbsolutePath()), e);
		} catch (IOException e) {
			log.error(sm.getString("JspClassLoader.loadJspClassFile.e1", file.getAbsolutePath()), e);
		}
		
		return null;
	}
}
