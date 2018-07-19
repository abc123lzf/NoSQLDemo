package lzf.webserver.util;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��19�� ����1:49:47
* @Description XML�ļ���������(DOM4J����)
*/
public class XMLUtil {

	public static Element getXMLRoot(File xmlPath) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(xmlPath);
		Element root = document.getRootElement();
		return root;
	}
	
}
