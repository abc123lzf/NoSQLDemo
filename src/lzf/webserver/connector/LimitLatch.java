package lzf.webserver.connector;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月14日 下午4:20:51
* @Description 连接数量控制器
*/
public class LimitLatch {

	private class Sync extends AbstractQueuedSynchronizer {
		private static final long serialVersionUID = 1L;
		
		@Override
		protected int tryAcquireShared(int ignored) {
			long newCount = count.incrementAndGet();
			if(newCount > limit) {
				count.decrementAndGet();
				return -1;
			}
			return 1;
		}
		
		@Override
		protected boolean tryReleaseShared(int arg) {
			count.decrementAndGet();
			return true;
		}
	}
	
	private final Sync sync = new Sync();
	private final AtomicLong count = new AtomicLong(0);
	private volatile long limit;
	
	/**
	 * 初始化一个连接限制控制器
	 * @param limit 最大连接数
	 */
	public LimitLatch(long limit) {
		this.limit = limit;
	}
	
	/**
	 * 该方法应在接收套接字前调用，该方法会将连接计数器加1，
	 * 如果超过最大连接数该方法会阻塞直到计数器小于最大连接数
	 */
	public void countUpOrAwait() throws InterruptedException {
		sync.acquireSharedInterruptibly(1);
	}
	
	/**
	 * 每当一个连接释放后应调用该方法
	 */
	public long countDown() {
		sync.releaseShared(0);
		long result = count.get();
		return result;
	}
}
