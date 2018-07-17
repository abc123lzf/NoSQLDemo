package lzf.webserver.util;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��17�� ����9:38:40
* @Description ��˵��
*/
public class DefaultServletOutputStream extends ServletOutputStream {

	private final OutputStream oos;
	
	public DefaultServletOutputStream(OutputStream oos) {
		this.oos = oos;
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
		oos.write(b);
	}

}
