package lzf.webserver.util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月22日 上午9:13:45
* @Description 继承PrintWriter，用于向HTTP响应体写入字符
*/
public class ByteBufPrintWriter extends PrintWriter {
	
	private final ByteBuf buf;
	
	//响应体大小
	private int size = 0;
	
	//字符输出流编码方式
	private Charset encoding;

	public ByteBufPrintWriter(OutputStream out, ByteBuf buf, String encoding) {
		super(out);
		this.buf = buf;
		this.encoding = Charset.forName(encoding);
	}
	
	@Override
	public void write(int c) {
		buf.writeChar(c);
	}
	
	@Override
	public void write(char[] b, int off, int len) {
		buf.writeBytes(String.valueOf(b).getBytes(encoding), off, len);
		size += len;
	}
	
	@Override
	public void write(char[] b) {
		write(b, 0, b.length);
		size += b.length;
	}
	
	@Override
	public void write(String s, int off, int len) {
		buf.writeBytes(s.getBytes(encoding), off, len);
		size += len;
		
	}
	
	@Override
	public void write(String s) {
		buf.writeBytes(s.getBytes(encoding));
		size += s.length();
	}
	
	public int getSize() {
		return size;
	}
}