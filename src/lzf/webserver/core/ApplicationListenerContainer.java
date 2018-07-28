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
* @author 李子帆
* @version 1.0
* @date 2018年7月28日 上午11:50:56
* @Description 存放该web应用所有的web监听器
*/
public final class ApplicationListenerContainer {

	private static final StringManager sm = StringManager.getManager(ApplicationListenerContainer.class);
	
	private static final Log log = LogFactory.getLog(ApplicationListenerContainer.class);
	
	private final StandardContext context;
	
	//监听Web应用启动和关闭的监听器
	private final List<ServletContextListener> contextListeners = new CopyOnWriteArrayList<>();
	
	//监听Application作用域的Attribute属性变化监听器
	private final List<ServletContextAttributeListener> contextAttributeListeners = new CopyOnWriteArrayList<>();
	
	//监听客户端Request请求
	private final List<ServletRequestListener> requestListeners = new CopyOnWriteArrayList<>();
	
	//监听Request作用域的Attribute属性变化监听器
	private final List<ServletRequestAttributeListener> requestAttributeListeners = new CopyOnWriteArrayList<>();
	
	//用于监听用户Session的开始和结束
	private final List<HttpSessionListener> sessionListeners = new CopyOnWriteArrayList<>();
	
	//监听Session作用域的Attribute属性变化监听器
	private final List<HttpSessionAttributeListener> sessionAttributeListeners = new CopyOnWriteArrayList<>();
	
	ApplicationListenerContainer(StandardContext context) {
		this.context = context;
	}
	
	/**
	 * 添加事件监听器
	 * @param klass 监听器类名
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
	 * SerlvetContext初始化事件触发
	 */
	public void runContextInitializedEvent() {
		
		ServletContextEvent event = new ServletContextEvent(context.getServletContext());
		
		for(ServletContextListener listener : contextListeners) {
			listener.contextInitialized(event);
		}
	}
	
	/**
	 * ServletContext销毁事件触发
	 */
	public void runContextDestoryedEvent() {
		
		ServletContextEvent event = new ServletContextEvent(context.getServletContext());
		
		for(ServletContextListener listener : contextListeners) {
			listener.contextDestroyed(event);
		}
	}
	
	/**
	 * Context作用域的Attribute添加事件
	 * @param name Attribute名称
	 * @param value Attribute值
	 */
	public void runContextAttributeAddedEvent(String name, Object value) {
		
		ServletContextAttributeEvent event = new ServletContextAttributeEvent(
				context.getServletContext(), name, value);
		
		for(ServletContextAttributeListener listener : contextAttributeListeners) {
			listener.attributeAdded(event);
		}
	}
	
	/**
	 * Context作用域的Attribute移除事件
	 * @param name Attribute名称
	 * @param value Attribute值
	 */
	public void runContextAttributeRemovedEvent(String name, Object value) {
		
		ServletContextAttributeEvent event = new ServletContextAttributeEvent(
				context.getServletContext(), name, value);
		
		for(ServletContextAttributeListener listener : contextAttributeListeners) {
			listener.attributeRemoved(event);
		}
		
	}
	
	/**
	 * Context作用域的Attribute被覆盖事件
	 * @param name Attribute名称
	 * @param value Attribute值
	 */
	public void runContextAttributeReplacedEvent(String name, Object value) {
		
		ServletContextAttributeEvent event = new ServletContextAttributeEvent(
				context.getServletContext(), name, value);
		
		for(ServletContextAttributeListener listener : contextAttributeListeners) {
			listener.attributeReplaced(event);
		}
	}
	
	/**
	 * Request对象初始化事件
	 * @param request 单次请求的Request对象
	 */
	public void runRequestInitializedEvent(ServletRequest request) {
		
		ServletRequestEvent event = new ServletRequestEvent(context.getServletContext(), request);
		
		for(ServletRequestListener listener : requestListeners) {
			listener.requestInitialized(event);
		}
	}
	
	/**
	 * Request对象销毁事件
	 * @param request 单次请求的Request对象
	 */
	public void runRequestDestroyedEvent(ServletRequest request) {
		
		ServletRequestEvent event = new ServletRequestEvent(context.getServletContext(), request);
		
		for(ServletRequestListener listener : requestListeners) {
			listener.requestDestroyed(event);
		}
	}
	
	/**
	 * Request作用域的Attribute增加事件
	 * @param request 本次请求的Request对象
	 * @param name Attribute名称
	 * @param value Attribute值
	 */
	public void runRequestAttributeAddedEvent(ServletRequest request, String name, Object value) {
		
		ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(
				context.getServletContext(), request, name, value);
		
		for(ServletRequestAttributeListener listener : requestAttributeListeners) {
			listener.attributeAdded(event);
		}
	}
	
	/**
	 * Request作用域的Attribute移除事件
	 * @param request 本次请求的Request对象
	 * @param name Attribute名称
	 * @param value Attribute值
	 */
	public void runRequestAttributeRemovedEvent(ServletRequest request, String name, Object value) {
		
		ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(
				context.getServletContext(), request, name, value);
		
		for(ServletRequestAttributeListener listener : requestAttributeListeners) {
			listener.attributeRemoved(event);
		}
	}
	
	/**
	 * Request作用域的Attribute覆盖事件
	 * @param request 本次请求的Request对象
	 * @param name Attribute名称
	 * @param value Attribute值
	 */
	public void runRequestAttributeReplacedEvent(ServletRequest request, String name, Object value) {
		
		ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(
				context.getServletContext(), request, name, value);
		
		for(ServletRequestAttributeListener listener : requestAttributeListeners) {
			listener.attributeReplaced(event);
		}
	}
	
	/**
	 * Session初始化事件
	 * @param session 该用户请求对应的Session对象
	 */
	public void runSessionInitializedEvent(HttpSession session) {
		
		HttpSessionEvent event = new HttpSessionEvent(session);
		
		for(HttpSessionListener listener : sessionListeners) {
			listener.sessionCreated(event);
		}
	}
	
	/**
	 * Session销毁事件
	 * @param session 该用户请求对应的Session对象
	 */
	public void runSessionDestroyedEvent(HttpSession session) {
		
		HttpSessionEvent event = new HttpSessionEvent(session);
		
		for(HttpSessionListener listener : sessionListeners) {
			listener.sessionDestroyed(event);
		}
	}
	
	/**
	 * Session作用域的Attribute添加事件
	 * @param session 本次请求对应的HttpSession对象
	 * @param name Attribute名称
	 * @param value Attribute值
	 */
	public void runSessionAttributeAddedEvent(HttpSession session, String name, Object value) {
		
		HttpSessionBindingEvent event = new HttpSessionBindingEvent(session, name, value);
		
		for(HttpSessionAttributeListener listener : sessionAttributeListeners) {
			listener.attributeAdded(event);
		}
	}
	
	/**
	 * Session作用域的Attribute移除事件
	 * @param session 本次请求对应的HttpSession对象
	 * @param name Attribute名称
	 * @param value Attribute值
	 */
	public void runSessionAttributeRemovedEvent(HttpSession session, String name, Object value) {
		
		HttpSessionBindingEvent event = new HttpSessionBindingEvent(session, name, value);
		
		for(HttpSessionAttributeListener listener : sessionAttributeListeners) {
			listener.attributeRemoved(event);
		}
	}
	
	/**
	 * Session作用域的Attribute替换事件
	 * @param session 本次请求对应的HttpSession对象
	 * @param name Attribute名称
	 * @param value Attribute值
	 */
	public void runSessionAttributeReplacedEvent(HttpSession session, String name, Object value) {
		
		HttpSessionBindingEvent event = new HttpSessionBindingEvent(session, name, value);
		
		for(HttpSessionAttributeListener listener : sessionAttributeListeners) {
			listener.attributeReplaced(event);
		}
	}
	
}
