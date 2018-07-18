package lzf.webserver.connector;

import java.io.IOException;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月12日 下午1:46:26
* @Description HTTP响应类
*/
public class Response extends ResponseBase {

	@Override
	public void sendError(int sc, String msg) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendError(int sc) throws IOException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 向客户端发送跳转指令
	 * 即向客户端发送状态码302然后跳转
	 * @param location 跳转URL，可以为绝对路径也可为相对路径
	 */
	@Override
	public void sendRedirect(String location) throws IOException {
		setStatus(302);
		headerMap.put("Refresh", location);
	}
}
