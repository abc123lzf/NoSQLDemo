package lzf.webserver.log;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月11日 下午6:25:04
* @Description 客户端访问日志记录接口，一般用作Valve组件
*/
public interface AccessLog {

	public void log(ServletRequest request, ServletResponse response);
	
}
