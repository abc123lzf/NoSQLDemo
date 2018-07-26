package lzf.webserver.startup;

import java.io.File;

import lzf.webserver.Context;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��26�� ����12:52:32
* @Description ����������ʱ������
*/
public final class ServerConstant {
	
	private static ServerConstant runtimeConstant = null;

	// ������Ŀ¼
	private final File main = new File(System.getProperty("user.dir"));
	
	// �����ļ�Ŀ¼
	private final File conf = new File(main, "conf");
	
	// ��־�ļ�Ŀ¼
	private final File log = new File(main, "log");
	
	// WEBӦ�ô��·��
	private final File webapp = new File(main, "webapps");
	
	//���JSP���ļ��ļ���
	private final File work = new File(main, "work");
	
	private ServerConstant() {
		if(!conf.exists())
			conf.mkdirs();
		
		if(!log.exists())
			log.mkdirs();
	}
	
	public static ServerConstant getConstant() {
		
		if(runtimeConstant == null) {
			synchronized(ServerConstant.class) {
				runtimeConstant = new ServerConstant();
			}
		}
		
		return runtimeConstant;
	}
	
	public File getMain() {
		return main;
	}
	
	public File getConf() {
		return conf;
	}
	
	public File getLog() {
		return log;
	}
	
	public File getWebapp() {
		return webapp;
	}
	
	public File getServerXml() {
		return new File(conf, "server.xml");
	}
	
	public File getJspWorkPath(Context context) {
		
		//·��Ϊ /work/${hostname}/${contextname}
		File file = new File(work, context.getParentContainer().getName() + File.separator 
				+ context.getName());
		
		if(!file.exists())
			file.mkdirs();
		
		return file;
	}
}
