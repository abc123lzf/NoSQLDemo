package lzf.webserver;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月12日 下午1:33:57
* @Description 管道接口
* 管道应包含在Container容器中，由Container负责调用其中的阀门，其中基础阀门应位于最后
* 非基础阀门按添加顺序依次调用(从最后添加的阀门开始)，基础阀门调用完成后，再调用下一层容器的第一个阀门
*/
public interface Pipeline {

	/**
	 * 返回第一个阀门
	 * @return 第一个阀门
	 */
	public Valve getFirst();
	
	/**
	 * 返回基础阀门(即最后一个阀门)
	 * @return 基础阀门
	 */
	public Valve getBasic();
	
	/**
	 * 设置基础阀门(即最后一个阀门)
	 * @param 基础阀门实例
	 * @throws LifecycleException 当组件已经启动后调用此方法
	 */
	public void setBasic(Valve valve) throws LifecycleException;
	
	/**
	 * 向管道添加非基础阀门，添加在阀门最前端
	 * @param valve 非基础阀门
	 */
	public void addValve(Valve valve) throws LifecycleException;
	
	/**
	 * 获取管道中所有阀门的数组
	 * @return List数组
	 * @throws LifecycleException 当组件已经启动后调用此方法
	 */
	public Valve[] getValves();
	
	/**
	 * 移除指定的阀门，若没有找到则不做任何调整
	 * @param valve 指定的阀门对象
	 * @throws LifecycleException 当组件已经启动后调用此方法
	 */
	public void removeValve(Valve valve) throws LifecycleException;
	
	/**
	 * 返回该管道所属的容器
	 * @return 容器实例，可以为Engine、Host、Context、Wrapper
	 */
	public Container getContainer();
	
	/**
	 * 设置该管道所属的容器
	 * @param container 容器实例，可以为Engine、Host、Context、Wrapper
	 * @throws LifecycleException 当组件已经启动后调用此方法
	 */
	public void setContainer(Container container) throws LifecycleException;
}