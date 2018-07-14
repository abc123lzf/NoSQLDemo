package lzf.webserver;

import java.util.EventObject;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��13�� ����10:21:09
* @Description �����¼���
*/
public class ContainerEvent extends EventObject {

	private static final long serialVersionUID = 4968846913171717314L;
	
	// �¼����ͣ�������Container.getEvent()�ṩ
	private final String type;

	// �¼����ݣ���Ϊ��
	private final Object data;

	public ContainerEvent(Container source, String type, Object data) {
		super(source);
		this.type = type;
		this.data = data;
	}

	public String getType() {
		return type;
	}

	public Object getData() {
		return data;
	}
}