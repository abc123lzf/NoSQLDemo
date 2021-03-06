package lzf.webserver.util;

import java.io.File;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import lzf.webserver.startup.ServerConstant;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月19日 下午1:49:47
* @Description XML文件解析工具(DOM4J解析)
*/
public class XMLUtil {

	public static Element getXMLRoot(File xmlPath) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(xmlPath);
		Element root = document.getRootElement();
		return root;
	}
	
	public static void makeDefaultServerXmlFile() throws IOException {
		
		File xmlFile = ServerConstant.getConstant().getServerXml();
		xmlFile.delete();
		xmlFile.createNewFile();
		
		//TODO
	}
}
