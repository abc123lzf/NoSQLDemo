package lzf.webserver.core;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import lzf.webserver.Container;
import lzf.webserver.Context;
import lzf.webserver.Wrapper;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月20日 下午5:39:04
* @Description 最小的容器，用于保存单个Servlet
*/
public class StandardWrapper extends ContainerBase implements Wrapper {
	
	private final Context context;
	
	private long availableTime = 0L;
	
	private int loadOnStartup = 0;
	
	private String servletClass;
	
	private Servlet servlet;
	
	private final Map<String, String> parameterMap = new LinkedHashMap<>();
	
	public StandardWrapper(Context context) {
		this.context = context;
	}

	@Override
	public long getAvailable() {
		return availableTime;
	}

	@Override
	public void setAvailable(long available) {
		this.availableTime = available;
	}

	@Override
	public int getLoadOnStartup() {
		return loadOnStartup;
	}

	@Override
	public void setLoadOnStartup(int value) {
		this.loadOnStartup = value;
	}

	@Override
	public String getServletClass() {
		if(servletClass == null) {
			if(servlet != null)
				return servlet.getClass().getName();
			return null;
		}
		return servletClass;
	}

	@Override
	public void setServletClass(String servletClass) {
		this.servletClass = servletClass;
	}

	@Override
	public boolean isUnavailable() {
		if(availableTime == 0)
			return false;
		long time = System.currentTimeMillis();
		if(time < availableTime)
			return true;
		return false;
	}

	@Override
	public void addInitParameter(String name, String value) {
		parameterMap.put(name, value);
	}

	@Override
	public String getInitParameter(String name) {
		return parameterMap.get(name);
	}

	@Override
	public String[] getInitParameters() {
		String[] parameters = new String[parameterMap.size()];
		int i = 0;
		for(String parameter : parameterMap.keySet()) {
			parameters[i++] = parameter;
		}
		return parameters;
	}

	@Override
	public void removeInitParameter(String name) {
		parameterMap.remove(name);
	}

	@Override
	public Servlet allocate() throws ServletException {
		if(servlet == null)
			return null;
		try {
			return servlet.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void load() throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unload() throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addServlet(Servlet servlet) {
		// TODO Auto-generated method stub

	}

	@Override
	public Servlet getServlet() {
		return servlet;
	}

	@Override
	public boolean isSingleThreadModel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void addChildContainerCheck(Container container) throws IllegalArgumentException {
		throw new IllegalArgumentException("Wrapper not support to add child container");
	}

	@Override
	protected void initInternal() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void startInternal() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void stopInternal() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void destoryInternal() throws Exception {
		// TODO Auto-generated method stub

	}

}
