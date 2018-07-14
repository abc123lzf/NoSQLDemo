package lzf.webserver.connector;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��14�� ����4:20:51
* @Description ��������������
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
	 * ��ʼ��һ���������ƿ�����
	 * @param limit ���������
	 */
	public LimitLatch(long limit) {
		this.limit = limit;
	}
	
	/**
	 * �÷���Ӧ�ڽ����׽���ǰ���ã��÷����Ὣ���Ӽ�������1��
	 * �����������������÷���������ֱ��������С�����������
	 */
	public void countUpOrAwait() throws InterruptedException {
		sync.acquireSharedInterruptibly(1);
	}
	
	/**
	 * ÿ��һ�������ͷź�Ӧ���ø÷���
	 */
	public long countDown() {
		sync.releaseShared(0);
		long result = count.get();
		return result;
	}
}
