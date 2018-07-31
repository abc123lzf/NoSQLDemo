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
 * @author ���ӷ�
 * @version 1.0
 * @date 2018��7��11�� ����7:30:37
 * @Description WebӦ�����������ÿ��webӦ��(Context���)��Ӧһ��WebappClassLoader
 */
public class WebappClassLoader extends ClassLoader {
	
	private static final StringManager sm = StringManager.getManager(WebappClassLoader.class);

	private static final Log log = LogFactory.getLog(WebappClassLoader.class);

	//{web-app}/WEB-INF/lib·��
	private final String lib;

	//{web-app}/WEB-INF/classes·��
	private final String classes;

	//�洢��Ϊjar�������class������ֵΪclass�ļ����ݵ�Map
	private final Map<String, byte[]> map = new ConcurrentHashMap<>(128);
	
	//�洢Jar��xml��properties�������ļ���Դ
	private final Map<String, byte[]> fileMap = new ConcurrentHashMap<>(64);

	//֮ǰ��û�е��ù�startRead()����
	private boolean isLoad = false;
	
	/**
	 * @param parent �����������һ��Ϊϵͳ�������
	 * @param webappFolder webapp��Ŀ¼
	 */
	WebappClassLoader(ClassLoader parent, File webappFolder) {
		super(parent);
		
		this.classes = webappFolder.getAbsolutePath() + File.separator + "WEB-INF" + File.separator + "classes";
		this.lib = webappFolder.getAbsolutePath() + File.separator + "WEB-INF" + File.separator + "lib";
	}
	
	/**
	 * ��ȡ�ļ�����WebappLoader��ʼ�������е���
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
	 * ��webӦ�õ�lib��classes�ļ��м�����
	 * @param name ����
	 * @return Class����
	 * @throws ClassNotFoundException û����lib��classesĿ¼���ҵ����ļ�
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
	 * �������������û�м���libĿ¼�µ�Jar��,���û�м������������ع���
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
	 * @param name ����
	 * @return �����ļ��Ķ���������
	 */
	private byte[] getClassFromFileOrMap(String name) {

		//������ת��Ϊ���·��
		String classPath = classes + File.separator + name.replace('.', File.separatorChar) + ".class";
		
		File file = new File(classPath);

		//�����web-inf\classes�ļ������ҵ������class�ļ�
		if (file.exists()) {

			InputStream input = null;
			
			//���ļ�ת��Ϊ���������鲢����
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

		//�����web-inf\classes�ļ���û������࣬��ô��Jar�е�class�ļ�����
		} else {
			if (map.containsKey(name)) {
				// ȥ��map�е����ã�����GC�޷��������õ�class�ļ�����
				return map.remove(name);
			}
		}
		
		//�����û���ҵ���ֱ�ӷ���null
		return null;
	}

	/**
	 * @return web-inf\libĿ¼�����е�JAR�ļ�File���󼯺�
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
	 * ��ȡJar�������е�class�ļ���ת��Ϊ���������ݴ���Map��
	 * @param jar Jar���ļ�����
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
	 * ��Jar���е��ļ�ת���ɶ���������
	 * @param jar JarFile����
	 * @param je ��Jar�ļ���JarEntry����
	 * @return ����������
	 * @throws IOException ����ȡʧ��ʱ
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
