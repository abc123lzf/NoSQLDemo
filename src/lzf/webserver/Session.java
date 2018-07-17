package lzf.webserver;

import lzf.webserver.session.SessionManagerBase;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月15日 下午3:42:44
* @Description 会话接口
*/
public interface Session {

	/**
	 * 获取SessionId
	 * @return SessionId
	 */
	public String getId();
	
	/**
	 * 获取该Session创建时间戳
	 * @return 时间戳
	 */
	public long getCreationTime();
	
	/**
	 * 获取Session对象最大空闲生存时间
	 * @return 时间戳
	 */
	public int getMaxInactiveInterval();
	
	/**
	 * 获取该Session对象最后一次访问的时间戳
	 * @return 时间戳
	 */
	public long getLastAccessedTime();
	
	/**
	 * 更新最后一次访问时间，同时会更改isNew变量
	 */
	public void updateLastAccessedTime();
	
	/**
	 * 更改Session UUID
	 * @return 新生成的ID
	 */
	public String changeId();
	
	/**
	 * 获取该Session对象的管理器
	 * @return Session对象管理器
	 */
	public SessionManagerBase getSessionManager();
}
