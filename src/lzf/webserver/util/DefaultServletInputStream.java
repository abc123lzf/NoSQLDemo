package lzf.webserver.util;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��17�� ����8:47:29
* @Description ��˵��
*/
public class DefaultServletInputStream extends ServletInputStream {

	private final InputStream is;
	
	public DefaultServletInputStream(InputStream is) {
		this.is = is;
	}
	
	@Override
	public boolean isFinished() {
		return true;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setReadListener(ReadListener listener) {
	}

	@Override
	public int read() throws IOException {
		return is.read();
	}

}
