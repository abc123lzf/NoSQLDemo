package lzf.webserver.connector;

import java.io.IOException;
import java.util.Locale;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月12日 下午1:46:26
* @Description HTTP响应类
*/
public abstract class Response extends ResponseBase {
	//状态码对应的附加消息
	protected String statusMsg;
	//这个响应已经发送给客户端了吗？
	protected volatile boolean committed = false;
	
	public Response() {
		super();
	}
	
	/**
	 * 向客户端发送响应对象，由容器调用
	 */
	public abstract void sendResponse();
	
	/**
	 * 向客户端发送指定状态码并清空响应，调用此方法后该响应不应再被写入
	 * @param sc 响应码
	 */
	@Override
	public synchronized final void sendError(int sc, String msg) throws IOException {
		committed = true;
		sendError0(sc, msg);
	}
	
	/**
	 * 应包含写入客户端Socket的方法
	 */
	protected abstract void sendError0(int sc, String msg);
	
	/**
	 * 立刻向客户端发送指定状态码指定错误HTML页面，然后清空响应，调用此方法后该响应不应再被写入
	 * @param sc 响应码
	 */
	@Override
	public synchronized final void sendError(int sc) throws IOException {
		committed = true;
		sendError0(sc);
	}
	
	/**
	 * 应包含写入客户端Socket的方法
	 */
	protected abstract void sendError0(int sc);

	/**
	 * 重定向页面
	 * @param location 跳转URL，可以为绝对路径也可为相对路径
	 */
	@Override
	public final void sendRedirect(String location) throws IOException {
		setStatus(302);
		headerMap.put("Location", location);
	}
	
	/**
	 * 设置响应状态码
	 * @param sc 状态码 范围从100~500
	 * @param sm 状态码描述
	 */
	@Override
	public final void setStatus(int sc, String sm) {
		this.statusMsg = sm;
	}
	
	/**
	 * 清空Response的所有数据
	 */
	@Override
	public void reset() {
		if(isCommitted())
			throw new IllegalStateException();
		headerMap.clear();
		status = 0;
	}

	/**
	 * 该Response对象提交给客户端了吗？
	 */
	@Override
	public boolean isCommitted() {
		return committed;
	}
	
	@Override
	public void setLocale(Locale loc) {
		// TODO Auto-generated method stub

	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}
}
