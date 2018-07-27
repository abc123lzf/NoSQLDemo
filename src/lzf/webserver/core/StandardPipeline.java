package lzf.webserver.core;

import lzf.webserver.Container;
import lzf.webserver.Lifecycle;
import lzf.webserver.LifecycleException;
import lzf.webserver.LifecycleState;
import lzf.webserver.Pipeline;
import lzf.webserver.Valve;
import lzf.webserver.util.StringManager;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月17日 上午11:49:09
* @Description 标准管道实现类
*/
public final class StandardPipeline extends LifecycleBase implements Pipeline {
	
	private static final StringManager sm = StringManager.getManager(StandardPipeline.class);
	
	private Container<?, ?> container;
	
	private volatile Valve[] valves = new Valve[0];

	public StandardPipeline() {	
	}
	
	public StandardPipeline(Container<?, ?> container) {
		this();
		this.container = container;
	}
	
	/**
	 * @return 第一个管道，若容器内没有管道则返回null
	 */
	@Override
	public Valve getFirst() {
		if(valves.length == 0)
			return null;
		return valves[0];
	}

	/**
	 * @return 基础管道，为StandardEngineValve、StandardHostValve等
	 */
	@Override
	public Valve getBasic() {
		return valves[valves.length - 1];
	}

	/**
	 * @param valve 基础管道实例，必须是Standard开头的Valve
	 */
	@Override
	public void setBasic(Valve valve) throws LifecycleException {
		
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
			throw new LifecycleException(sm.getString("StandardPipeline.setBasic.e0", container.getName()));
			
		for(Valve v : valves) {
			if(v == valve)
				return;
		}
		
		Valve[] newValves = new Valve[valves.length + 1];
		
		synchronized(valves) {
			for(int i = 0; i < valves.length; i++) {
				newValves[i] = valves[i];
			}
			newValves[newValves.length - 1] = valve;
			this.valves = newValves;
		}
	}

	/**
	 * @param valve 非基础管道实例
	 */
	@Override
	public void addValve(Valve valve) throws LifecycleException {
		
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
			throw new LifecycleException(sm.getString("StandardPipeline.addValve.e0", container.getName()));
		
		for(Valve v : valves) {
			if(v == valve)
				return;
		}
		
		Valve[] newValves = new Valve[valves.length + 1];
		
		synchronized(valves) {
			newValves[0] = valve;
			for(int i = 1; i < newValves.length; i++) {
				newValves[i] = valves[i - 1];
			}
			this.valves = newValves;
		}
	}

	/**
	 * @return 该管道包含的所有阀门数组
	 */
	@Override
	public Valve[] getValves() {
		return valves;
	}

	/**
	 * @param valve 需要移除的管道
	 * @throws LifecycleException 
	 */
	@Override
	public void removeValve(Valve valve) throws LifecycleException {
		
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
			throw new LifecycleException(sm.getString("StandardPipeline.removeValve.e0", container.getName()));
		
		int index = -1;
		synchronized(valves) {
			for(int i = 0; i < valves.length; i++) {
				if(valves[i] == valve)
					index = i;
			}
			if(index == -1)
				return;
			Valve[] newValves = new Valve[valves.length - 1];
			for(int i = 0, j = 0; i < valves.length; i++) {
				if(i == index)
					continue;
				newValves[j++] = valves[i];
			}
			this.valves = newValves;
		}
	}

	/**
	 * @return 该管道所属的容器
	 */
	@Override
	public Container<?, ?> getContainer() {
		return container;
	}

	/**
	 * 设置该管道所属的容器
	 * @param container 容器实例，可以为Engine、Host、Context、Wrapper
	 * @throws LifecycleException 当组件已经启动后调用此方法
	 */
	@Override
	public void setContainer(Container<?, ?> container) throws LifecycleException {
		
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
			throw new LifecycleException(sm.getString("StandardPipeline.setContainer.e0", container.getName()));
		
		this.container = container;
	}

	@Override
	protected void initInternal() throws Exception {

		for(Valve v : valves) {
			if(v instanceof Lifecycle)
				((Lifecycle) v).init();
		}
	}

	@Override
	protected void startInternal() throws Exception {
		//管道在启动前必须已经指定好所属的容器
		if(container == null) {
			setLifecycleState(LifecycleState.FAILED);
			throw new IllegalStateException(sm.getString("StandardPipeline.startInternal.e0"));
		}
		
		for(Valve v : valves) {
			if(v instanceof Lifecycle)
				((Lifecycle) v).start();
		}
	}

	@Override
	protected void stopInternal() throws Exception {
		for(Valve v : valves) {
			if(v instanceof Lifecycle)
				((Lifecycle) v).stop();
		}
		
	}

	@Override
	protected void destoryInternal() throws Exception {
		for(Valve v : valves) {
			if(v instanceof Lifecycle)
				((Lifecycle) v).destory();
		}
		valves = null;
	}
}