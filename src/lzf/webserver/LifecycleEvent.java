package lzf.webserver;

import java.util.EventObject;

/**
 * @author ���ӷ�
 * @version 1.0
 * @date 2018��7��9�� ����1:23:52
 * @Description �¼��������ڷ�װ�¼�����Ϣ����ΪLifecycleListener����ʹ��
 */
public class LifecycleEvent extends EventObject {

	private static final long serialVersionUID = -6390372529739718230L;

	// �¼����ͣ�������LifecycleState.getLifecycleEvent()�ṩ
	private final String type;

	// �¼����ݣ���Ϊ��
	private final Object data;

	public LifecycleEvent(Lifecycle source, String type, Object data) {
		super(source);
		this.data = data;
		this.type = type;
	}

	public Lifecycle getLifecycle() {
		return (Lifecycle) super.getSource();
	}

	public Object getData() {
		return this.data;
	}

	public String getType() {
		return this.type;
	}
}
