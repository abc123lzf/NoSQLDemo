package lzf.webserver.mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lzf.webserver.Context;
import lzf.webserver.Wrapper;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��19�� ����8:35:12
* @Description ��˵��
*/
public final class ContextMapper {

	private final Context context;
	
	//���洢URI·����Ϣ������"blog/my.html"
	private final Map<String, MappedWrapper> mapper = new ConcurrentHashMap<>();
	
	public ContextMapper(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}
	
	public Wrapper getWrapper(String uri) {
		
		if(context.getName().equals("ROOT")) {
			MappedWrapper mw = null;
			
			if(uri.equals("/")) {
				mw = mapper.get("/index.html");
			} else {
				mw = mapper.get(uri);
			}
			
			if(mw == null)
				return null;
			return mw.object;
		}
		
		//��ȡ�ڶ���"/"֮ǰ���ַ���
		int st = uri.indexOf('/', 1);
		//��û���ҵ��ڶ���"/"����˵��������"/demo"������URI��ֱ��ȥ���ַ�����ͷ"/"����
		if(st == -1) {
			MappedWrapper mw = mapper.get("/index.html");
			if(mw == null)
				return null;
			return mw.object;
		//����ҵ��ˣ���˵���ǽ�Ϊ���ӵ�URL������"/demo/index.jsp"��������ȡ��/index.jsp����
		} else {
			MappedWrapper mw = mapper.get(uri.substring(st, uri.length()));
			if(mw == null)
				return null;
			return mw.object;
		}
	}
	
	void addWrapper(Wrapper wrapper) {
		
		String uri = wrapper.getURIPath();
		System.out.println(uri);
		
		if(uri == null)
			throw new UnsupportedOperationException("The wrapper's URI path is empty");
		
		mapper.put(uri, new MappedWrapper(uri, wrapper));
	}
	
	void removeMapper(Wrapper wrapper) {
		String uri = wrapper.getURIPath();
		if(uri == null)
			return;
		mapper.remove(uri);
	}
}

class MappedWrapper extends MapElement<Wrapper> {
	
	public MappedWrapper(String uri, Wrapper object) {
		super(uri, object);
	}
	
}