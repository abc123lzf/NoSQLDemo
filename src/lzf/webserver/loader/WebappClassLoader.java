package lzf.webserver.loader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��11�� ����7:30:37
* @Description WebӦ�����������ÿ��webӦ��(Context���)��Ӧһ��WebappClassLoader
*/
public class WebappClassLoader extends ClassLoader {

	private final File webappFolder;
	
	public WebappClassLoader(ClassLoader parent, File webappFolder) {
		super(parent);
		this.webappFolder = webappFolder;
	}
	
	@Override
	protected Class<?> findClass(String name) {
		
		String p = name.replaceAll("\\.", "\\\\") + ".class";
		
		byte[] data = null;
		
		InputStream is = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			is = new FileInputStream(new File(webappFolder, p));
			int c = 0;
			while((c = is.read()) != -1) {
				baos.write(c);
			}
			data = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return this.defineClass(name, data, 0, data.length);
	}
	
}
