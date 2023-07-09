package ngd.controller.display;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ngd.controller.CommonObject;
import ngd.model.UserMessage;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMessageWrapper {
	
	private List<UserMessageDisplay> userMsgDisplay;
	
	private String msgTarget;
	
	private Boolean online;
	
	public static Integer numUserMsg = 0;
	
	public static UserMessageWrapper from(String key, List<UserMessageDisplay> value) {
		
		return UserMessageWrapper.builder()
				.msgTarget(key)
				.online(CommonObject.sseUsers.containsKey(key))
				.userMsgDisplay(value)
				.build();
				
	}
	public static List<UserMessageWrapper> from(final Map<String, List<UserMessageDisplay>> userMessages) {
		
		List<UserMessageWrapper> listMsg = new ArrayList<>();
		
		final Iterator<String> iterator = userMessages.keySet().iterator();
		
		while(iterator.hasNext()) {
			String key = (String)iterator.next();
			List<UserMessageDisplay> currList = userMessages.get(key);
			listMsg.add(from(key, currList));
		}
		
		return listMsg;
	}

}
