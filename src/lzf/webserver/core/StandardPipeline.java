package lzf.webserver.core;

import lzf.webserver.Container;
import lzf.webserver.Lifecycle;
import lzf.webserver.LifecycleException;
import lzf.webserver.Pipeline;
import lzf.webserver.Valve;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月17日 上午11:49:09
* @Description 标准管道实现类
*/
public class StandardPipeline extends LifecycleBase implements Pipeline {
	
	private Container container;
	private volatile Valve[] valves = new Valve[0];

	public StandardPipeline() {	
	}
	
	public StandardPipeline(Container container) {
		this();
		this.container = container;
	}
	
	@Override
	public Valve getFirst() {
		if(valves.length == 0)
			return null;
		return valves[0];
	}

	@Override
	public Valve getBasic() {
		return valves[valves.length - 1];
	}

	@Override
	public void setBasic(Valve valve) {
		Valve[] newValves = new Valve[valves.length + 1];
		synchronized(valves) {
			for(int i = 0; i < valves.length; i++) {
				newValves[i] = valves[i];
			}
			newValves[newValves.length - 1] = valve;
			this.valves = newValves;
		}
	}

	@Override
	public void addValve(Valve valve) {
		Valve[] newValves = new Valve[valves.length + 1];
		synchronized(valves) {
			newValves[0] = valve;
			for(int i = 1; i < newValves.length; i++) {
				newValves[i] = valves[i - 1];
			}
			this.valves = newValves;
		}
	}

	@Override
	public Valve[] getValves() {
		return valves;
	}

	@Override
	public void removeValve(Valve valve) {
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

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public void setContainer(Container container) {
		this.container = container;
	}

	@Override
	protected void initInternal() throws LifecycleException {
		for(Valve v : valves) {
			if(v instanceof Lifecycle)
				((Lifecycle) v).init();
		}
	}

	@Override
	protected void startInternal() throws LifecycleException {
		for(Valve v : valves) {
			if(v instanceof Lifecycle)
				((Lifecycle) v).start();
		}
	}

	@Override
	protected void stopInternal() throws LifecycleException {
		for(Valve v : valves) {
			if(v instanceof Lifecycle)
				((Lifecycle) v).stop();
		}
		
	}

	@Override
	protected void destoryInternal() throws LifecycleException {
		for(Valve v : valves) {
			if(v instanceof Lifecycle)
				((Lifecycle) v).destory();
		}
		valves = null;
	}
}
