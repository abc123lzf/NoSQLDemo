package lzf.webserver;
/**
* @author 李子帆
* @version 1.0
* @date 2018年7月21日 下午4:40:23
* @Description 加载器接口，即web应用载入器，包括XML文件解析，静态资源文件的读取，类加载实现
* @see lzf.webserver.core.WebappLoader
*/
public interface Loader {

	/**
	 * @return 加载器所属的类加载器
	 */
	public ClassLoader getClassLoader();
	
	/**
	 * @return 该web应用的JSP类加载器
	 */
	public ClassLoader getJspClassLoader();
	
	/**
	 * @return 加载器对应的Context容器
	 */
	public Context getContext();
	
	/**
	 * @param context 设置加载器所属的Context容器
	 */
	public void setContext(Context context) throws LifecycleException;
	
	/**
	 * @return Context容器支持重加载吗(热插拔JSP、Servlet、Jar等)
	 */
	public boolean getReloadable();
	
	/**
	 * @param reloadable 设置重加载(热插拔选项)
	 */
	public void setReloadable(boolean reloadable) throws LifecycleException;
}
