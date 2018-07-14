package lzf.webserver.connector;
/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��14�� ����8:47:05
* @Description ���ӽ�����״̬
*/
public enum HandlerState {

	NEW(0),
	INITIALIZING(1),
	INITIALIZED(2),
	STARTING(3),
	STARTED(4),
	STOPPING(5),
	STOPPED(6),
	ERR(-1);
	
	private final int step;
	
	private HandlerState(int step) {
		this.step = step;
	}

	public int getStep() {
		return step;
	}
	
	public boolean after(HandlerState state) {
		if(state.getStep() >= this.step)
			return true;
		return false;
	}
}
