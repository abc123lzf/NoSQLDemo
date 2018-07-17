package lzf.webserver.util;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月17日 下午9:38:40
* @Description 类说明
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
