package lzf.webserver.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;
import lzf.webserver.util.StringManager;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��28�� ����11:50:56
* @Description ��Ÿ�webӦ�����е�web������
*/
public final class ApplicationListenerContainer {

	private static final StringManager sm = StringManager.getManager(ApplicationListenerContainer.class);
	
	private static final Log log = LogFactory.getLog(ApplicationListenerContainer.class);
	
	private final StandardContext context;
	
	//����WebӦ�������͹رյļ�����
	private final List<ServletContextListener> contextListeners = new CopyOnWriteArrayList<>();
	
	//����Application�������Attribute���Ա仯������
	private final List<ServletContextAttributeListener> contextAttributeListeners = new CopyOnWriteArrayList<>();
	
	//�����ͻ���Request����
	private final List<ServletRequestListener> requestListeners = new CopyOnWriteArrayList<>();
	
	//����Request�������Attribute���Ա仯������
	private final List<ServletRequestAttributeListener> requestAttributeListeners = new CopyOnWriteArrayList<>();
	
	//���ڼ����û�Session�Ŀ�ʼ�ͽ���
	private final List<HttpSessionListener> sessionListeners = new CopyOnWriteArrayList<>();
	
	//����Session�������Attribute���Ա仯������
	private final List<HttpSessionAttributeListener> sessionAttributeListeners = new CopyOnWriteArrayList<>();
	
	ApplicationListenerContainer(StandardContext context) {
		this.context = context;
	}
	
	/**
	 * ����¼�������
	 * @param klass ����������
	 */
	void addListenerClass(String klass) {
		
		ClassLoader loader = context.getWebappLoader().getClassLoader();
		
		Object listener = null;
		
		try {
			listener = loader.loadClass(klass).newInstance();
		} catch (InstantiationException e) {
			log.error(sm.getString("ApplicationListenerContainer.addListenerClass.e0", klass), e);
			return;
		} catch (IllegalAccessException e) {
			log.error(sm.getString("ApplicationListenerContainer.addListenerClass.e1", klass), e);
			return;
		} catch (ClassNotFoundException e) {
			log.error(sm.getString("ApplicationListenerContainer.addListenerClass.e2", klass), e);
			return;
		}
		
		if(listener instanceof ServletContextListener) {
			contextListeners.add((ServletContextListener)listener);
			
		} else if(listener instanceof ServletContextAttributeListener) {
			contextAttributeListeners.add((ServletContextAttributeListener) listener);
			
		} else if(listener instanceof ServletRequestListener) {
			requestListeners.add((ServletRequestListener)listener);
			
		} else if(listener instanceof ServletRequestAttributeListener) {
			requestAttributeListeners.add((ServletRequestAttributeListener) listener);
			
		} else if(listener instanceof HttpSessionListener) {
			sessionListeners.add((HttpSessionListener) listener);
			
		} else if(listener instanceof HttpSessionAttributeListener) {
			sessionAttributeListeners.add((HttpSessionAttributeListener) listener);
			
		} else {
			log.warn(sm.getString("ApplicationListenerContainer.addListenerClass.w0", klass));
		}
	}
	
	/**
	 * SerlvetContext��ʼ���¼�����
	 */
	public void runContextInitializedEvent() {
		
		ServletContextEvent event = new ServletContextEvent(context.getServletContext());
		
		for(ServletContextListener listener : contextListeners) {
			listener.contextInitialized(event);
		}
	}
	
	/**
	 * ServletContext�����¼�����
	 */
	public void runContextDestoryedEvent() {
		
		ServletContextEvent event = new ServletContextEvent(context.getServletContext());
		
		for(ServletContextListener listener : contextListeners) {
			listener.contextDestroyed(event);
		}
	}
	
	/**
	 * Context�������Attribute����¼�
	 * @param name Attribute����
	 * @param value Attributeֵ
	 */
	public void runContextAttributeAddedEvent(String name, Object value) {
		
		ServletContextAttributeEvent event = new ServletContextAttributeEvent(
				context.getServletContext(), name, value);
		
		for(ServletContextAttributeListener listener : contextAttributeListeners) {
			listener.attributeAdded(event);
		}
	}
	
	/**
	 * Context�������Attribute�Ƴ��¼�
	 * @param name Attribute����
	 * @param value Attributeֵ
	 */
	public void runContextAttributeRemovedEvent(String name, Object value) {
		
		ServletContextAttributeEvent event = new ServletContextAttributeEvent(
				context.getServletContext(), name, value);
		
		for(ServletContextAttributeListener listener : contextAttributeListeners) {
			listener.attributeRemoved(event);
		}
		
	}
	
	/**
	 * Context�������Attribute�������¼�
	 * @param name Attribute����
	 * @param value Attributeֵ
	 */
	public void runContextAttributeReplacedEvent(String name, Object value) {
		
		ServletContextAttributeEvent event = new ServletContextAttributeEvent(
				context.getServletContext(), name, value);
		
		for(ServletContextAttributeListener listener : contextAttributeListeners) {
			listener.attributeReplaced(event);
		}
	}
	
	/**
	 * Request�����ʼ���¼�
	 * @param request ���������Request����
	 */
	public void runRequestInitializedEvent(ServletRequest request) {
		
		ServletRequestEvent event = new ServletRequestEvent(context.getServletContext(), request);
		
		for(ServletRequestListener listener : requestListeners) {
			listener.requestInitialized(event);
		}
	}
	
	/**
	 * Request���������¼�
	 * @param request ���������Request����
	 */
	public void runRequestDestroyedEvent(ServletRequest request) {
		
		ServletRequestEvent event = new ServletRequestEvent(context.getServletContext(), request);
		
		for(ServletRequestListener listener : requestListeners) {
			listener.requestDestroyed(event);
		}
	}
	
	/**
	 * Request�������Attribute�����¼�
	 * @param request ���������Request����
	 * @param name Attribute����
	 * @param value Attributeֵ
	 */
	public void runRequestAttributeAddedEvent(ServletRequest request, String name, Object value) {
		
		ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(
				context.getServletContext(), request, name, value);
		
		for(ServletRequestAttributeListener listener : requestAttributeListeners) {
			listener.attributeAdded(event);
		}
	}
	
	/**
	 * Request�������Attribute�Ƴ��¼�
	 * @param request ���������Request����
	 * @param name Attribute����
	 * @param value Attributeֵ
	 */
	public void runRequestAttributeRemovedEvent(ServletRequest request, String name, Object value) {
		
		ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(
				context.getServletContext(), request, name, value);
		
		for(ServletRequestAttributeListener listener : requestAttributeListeners) {
			listener.attributeRemoved(event);
		}
	}
	
	/**
	 * Request�������Attribute�����¼�
	 * @param request ���������Request����
	 * @param name Attribute����
	 * @param value Attributeֵ
	 */
	public void runRequestAttributeReplacedEvent(ServletRequest request, String name, Object value) {
		
		ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(
				context.getServletContext(), request, name, value);
		
		for(ServletRequestAttributeListener listener : requestAttributeListeners) {
			listener.attributeReplaced(event);
		}
	}
	
	/**
	 * Session��ʼ���¼�
	 * @param session ���û������Ӧ��Session����
	 */
	public void runSessionInitializedEvent(HttpSession session) {
		
		HttpSessionEvent event = new HttpSessionEvent(session);
		
		for(HttpSessionListener listener : sessionListeners) {
			listener.sessionCreated(event);
		}
	}
	
	/**
	 * Session�����¼�
	 * @param session ���û������Ӧ��Session����
	 */
	public void runSessionDestroyedEvent(HttpSession session) {
		
		HttpSessionEvent event = new HttpSessionEvent(session);
		
		for(HttpSessionListener listener : sessionListeners) {
			listener.sessionDestroyed(event);
		}
	}
	
	/**
	 * Session�������Attribute����¼�
	 * @param session ���������Ӧ��HttpSession����
	 * @param name Attribute����
	 * @param value Attributeֵ
	 */
	public void runSessionAttributeAddedEvent(HttpSession session, String name, Object value) {
		
		HttpSessionBindingEvent event = new HttpSessionBindingEvent(session, name, value);
		
		for(HttpSessionAttributeListener listener : sessionAttributeListeners) {
			listener.attributeAdded(event);
		}
	}
	
	/**
	 * Session�������Attribute�Ƴ��¼�
	 * @param session ���������Ӧ��HttpSession����
	 * @param name Attribute����
	 * @param value Attributeֵ
	 */
	public void runSessionAttributeRemovedEvent(HttpSession session, String name, Object value) {
		
		HttpSessionBindingEvent event = new HttpSessionBindingEvent(session, name, value);
		
		for(HttpSessionAttributeListener listener : sessionAttributeListeners) {
			listener.attributeRemoved(event);
		}
	}
	
	/**
	 * Session�������Attribute�滻�¼�
	 * @param session ���������Ӧ��HttpSession����
	 * @param name Attribute����
	 * @param value Attributeֵ
	 */
	public void runSessionAttributeReplacedEvent(HttpSession session, String name, Object value) {
		
		HttpSessionBindingEvent event = new HttpSessionBindingEvent(session, name, value);
		
		for(HttpSessionAttributeListener listener : sessionAttributeListeners) {
			listener.attributeReplaced(event);
		}
	}
	
}
