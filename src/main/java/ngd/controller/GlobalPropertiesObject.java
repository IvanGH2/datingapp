package ngd.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;


@Component
public class GlobalPropertiesObject {
	
	@Value("${app.version}")
	private String version;
	
	public String getVersion() {
		return version;
	}
	
	@Value("${ngd.msg.user.count}")
	private Integer numMsgUsers;
	
	@Value("${ngd.user.views.count}")
	private Integer numUserViewsCount;
	
	@Value("${ngd.user.search.count}")
	private Integer numUserSearchCount;
	
	@ModelAttribute("msg_user_count")
	public Integer getNumMsgUsers() {
		return numMsgUsers;
	}
	@ModelAttribute("user_view_count")
	public Integer getNumUserViewsCount() {
		return numUserViewsCount;
	}
	
	@ModelAttribute("user_search_count")
	public Integer getNumUserSearchCount() {
		return numUserSearchCount;
	}
}
