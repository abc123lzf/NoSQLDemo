package lzf.webserver.util;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

import io.netty.buffer.ByteBuf;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��18�� ����3:17:16
* @Description ��˵��
*/
public class ByteBufOutputStream extends ServletOutputStream {

	private final ByteBuf buf;
	
	private int size = 0;
	
	public ByteBufOutputStream(ByteBuf buf) {
		this.buf = buf;
		int x = buf.capacity();
	}
	
	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setWriteListener(WriteListener listener) {
	}

	@Override
	public void write(int b) throws IOException {
		buf.writeByte(b);
		size++;
	}
	
	public int getSize() {
		return size;
	}
}
