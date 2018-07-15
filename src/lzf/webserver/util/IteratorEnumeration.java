package lzf.webserver.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月15日 下午8:58:28
* @Description Enumeration实现类，需传入Iterator实例
*/
public class IteratorEnumeration<T> implements Enumeration<T> {

	private final Iterator<T> iterator;
	
	public IteratorEnumeration(Iterator<T> iterator) {
		this.iterator = iterator;
	}
	
	@Override
	public boolean hasMoreElements() {
		return iterator.hasNext();
	}

	@Override
	public T nextElement() {
		return iterator.next();
	}
}
