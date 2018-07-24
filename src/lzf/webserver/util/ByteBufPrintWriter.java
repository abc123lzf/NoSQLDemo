package lzf.webserver.util;

import java.io.OutputStream;
import java.io.PrintWriter;

import io.netty.buffer.ByteBuf;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��22�� ����9:13:45
* @Description �̳�PrintWriter��������HTTP��Ӧ��д���ַ�
*/
public class ByteBufPrintWriter extends PrintWriter {
	
	private final ByteBuf buf;
	
	private int size = 0;

	public ByteBufPrintWriter(OutputStream out, ByteBuf buf) {
		super(out);
		this.buf = buf;
	}
	
	@Override
	public void write(int c) {
		buf.writeChar(c);
	}
	
	@Override
	public void write(char b[], int off, int len) {
		buf.writeBytes(String.valueOf(b).getBytes(), off, len);
		size += len;
	}
	
	@Override
	public void write(char b[]) {
		write(b, 0, b.length);
		size += b.length;
	}
	
	@Override
	public void write(String s, int off, int len) {
		buf.writeBytes(s.getBytes(), off, len);
		size += len;
	}
	
	@Override
	public void write(String s) {
		buf.writeBytes(s.getBytes());
		size += s.length();
	}
	
	public int getSize() {
		return size;
	}
}