package lzf.webserver;

import java.util.EventObject;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月9日 下午1:23:52
 * @Description 事件对象，用于封装事件的信息，作为LifecycleListener参数使用
 */
public class LifecycleEvent extends EventObject {

	private static final long serialVersionUID = -6390372529739718230L;

	// 事件类型，参数由LifecycleState.getLifecycleEvent()提供
	private final String type;

	// 事件数据，可为空
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
