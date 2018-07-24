package lzf.webserver.util;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

import io.netty.buffer.ByteBuf;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月18日 下午3:17:16
* @Description 类说明
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
