package lzf.webserver.startup;

import java.io.File;

import lzf.webserver.Context;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月26日 下午12:52:32
* @Description 服务器运行时常量类
*/
public final class ServerConstant {
	
	private static ServerConstant runtimeConstant = null;

	// 主运行目录
	private final File main = new File(System.getProperty("user.dir"));
	
	// 配置文件目录
	private final File conf = new File(main, "conf");
	
	// 日志文件目录
	private final File log = new File(main, "log");
	
	// WEB应用存放路径
	private final File webapp = new File(main, "webapps");
	
	//存放JSP类文件文件夹
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
		
		//路径为 /work/${hostname}/${contextname}
		File file = new File(work, context.getParentContainer().getName() + File.separator 
				+ context.getName());
		
		if(!file.exists())
			file.mkdirs();
		
		return file;
	}
}
