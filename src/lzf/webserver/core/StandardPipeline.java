package lzf.webserver.core;

import lzf.webserver.Container;
import lzf.webserver.Lifecycle;
import lzf.webserver.LifecycleException;
import lzf.webserver.LifecycleState;
import lzf.webserver.Pipeline;
import lzf.webserver.Valve;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��17�� ����11:49:09
* @Description ��׼�ܵ�ʵ����
*/
public final class StandardPipeline extends LifecycleBase implements Pipeline {
	
	private Container container;
	
	private volatile Valve[] valves = new Valve[0];

	public StandardPipeline() {	
	}
	
	public StandardPipeline(Container container) {
		this();
		this.container = container;
	}
	
	/**
	 * @return ��һ���ܵ�����������û�йܵ��򷵻�null
	 */
	@Override
	public Valve getFirst() {
		if(valves.length == 0)
			return null;
		return valves[0];
	}

	/**
	 * @return �����ܵ���ΪStandardEngineValve��StandardHostValve��
	 */
	@Override
	public Valve getBasic() {
		return valves[valves.length - 1];
	}

	/**
	 * @param valve �����ܵ�ʵ����������Standard��ͷ��Valve
	 */
	@Override
	public void setBasic(Valve valve) throws LifecycleException {
		
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
			throw new LifecycleException("�޷����û����ܵ�������������");
			
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
	 * @param valve �ǻ����ܵ�ʵ��
	 */
	@Override
	public void addValve(Valve valve) throws LifecycleException {
		
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
			throw new LifecycleException("�޷���ӹܵ�������������");
		
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
	 * @return �ùܵ����������з�������
	 */
	@Override
	public Valve[] getValves() {
		return valves;
	}

	/**
	 * @param valve ��Ҫ�Ƴ��Ĺܵ�
	 * @throws LifecycleException 
	 */
	@Override
	public void removeValve(Valve valve) throws LifecycleException {
		
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
			throw new LifecycleException("�޷��Ƴ������ܵ�������������");
		
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
	 * @return �ùܵ�����������
	 */
	@Override
	public Container getContainer() {
		return container;
	}

	/**
	 * ���øùܵ�����������
	 * @param container ����ʵ��������ΪEngine��Host��Context��Wrapper
	 * @throws LifecycleException ������Ѿ���������ô˷���
	 */
	@Override
	public void setContainer(Container container) throws LifecycleException {
		
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
			throw new LifecycleException("�޷���������ܵ�����������������������");
		
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
		//�ܵ�������ǰ�����Ѿ�ָ��������������
		if(container == null) {
			setLifecycleState(LifecycleState.FAILED);
			throw new IllegalStateException("�ùܵ�û����������");
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