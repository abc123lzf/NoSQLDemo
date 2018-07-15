package lzf.webserver.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��15�� ����8:58:28
* @Description Enumerationʵ���࣬�贫��Iteratorʵ��
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
