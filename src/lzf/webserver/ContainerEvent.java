package lzf.webserver;

import java.util.EventObject;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月13日 上午10:21:09
* @Description 容器事件类
*/
public class ContainerEvent extends EventObject {

	private static final long serialVersionUID = 4968846913171717314L;
	
	// 事件类型，参数由Container.getEvent()提供
	private final String type;

	// 事件数据，可为空
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