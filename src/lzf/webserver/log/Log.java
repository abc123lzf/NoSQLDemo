package lzf.webserver.log;
/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��11�� ����6:00:47
* @Description ��������־�ӿ�
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
