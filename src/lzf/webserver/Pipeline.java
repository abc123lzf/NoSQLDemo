package lzf.webserver;
/**
* @author 李子帆
* @version 1.0
* @date 2018年7月12日 下午1:33:57
* @Description 管道接口
*/
public interface Pipeline {

	//返回第一个阀门
	public Valve getFirst();
	
	//返回基础阀门(即最后一个阀门)
	public Valve getBase();
	
	//设置基础阀门
	public void setBase(Valve valve);
	
	//向管道添加阀门
	public void addValve(Valve valve);
}
