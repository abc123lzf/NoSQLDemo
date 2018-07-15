package lzf.webserver.connector;

public abstract class RequestBase {
	
	protected static class HttpLine {
		protected String method;
		protected String uri;
		protected String version;
	}
	
	protected static class HttpHead {
		protected String accept;
		protected String accpetCharset;
		protected String acceptEncoding;
		protected String acceptLanguage;
		protected String acceptRanges;
		protected String x;
	}
}
