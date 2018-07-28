package lzf.webserver.users;

import lzf.webserver.Group;
/**
* @author 李子帆
* @version 1.0
* @date 2018年7月28日 下午10:01:35
* @Description 类说明
*/
public abstract class GroupBase implements Group {
	
	protected String groupName = null;
	
	protected String description = null;

	@Override
	public String getName() {
		return getGroupname();
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getGroupname() {
		return groupName;
	}

	@Override
	public void setGroupname(String groupname) {
		this.groupName = groupname;
	}

}
