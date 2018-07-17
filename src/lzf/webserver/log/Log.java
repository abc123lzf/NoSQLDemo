package lzf.webserver.log;
/**
* @author 李子帆
* @version 1.0
* @date 2018年7月11日 下午6:00:47
* @Description 服务器日志接口
*/
public interface Log {
	
	public boolean isTraceEnabled();
	public boolean isDebugEnabled();
	public boolean isInfoEnabled();
	public boolean isWarnEnabled();
	public boolean isErrorEnabled();
	public boolean isFatalEnabled();
	
	public void trace(Object msg);
	public void trace(Object msg, Throwable t);
	public void debug(Object msg);
	public void debug(Object msg, Throwable t);
	public void info(Object msg);
	public void info(Object msg, Throwable t);
	public void warn(Object msg);
	public void warn(Object msg, Throwable t);
	public void error(Object msg);
	public void error(Object msg, Throwable t);
	public void fatal(Object msg);
	public void fatal(Object msg, Throwable t);
}
