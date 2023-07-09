package ngd.controller.display;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ngd.model.UserMessage;
import ngd.utility.CurrentUserUtility;
import ngd.utility.DateTimeUtil;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMessageDisplay {
	
	private Boolean incomingMsg;
	
	private String msgContent;
	
	private Boolean msgViewed;
	
	private Boolean msgFlag;
	
	private String date;
	
	private Integer msgId;
	
public static List<UserMessageDisplay> from(final List<UserMessage> userMsg) {
		
		List<UserMessageDisplay> userMsgDisplay = new ArrayList<>();
		
		for(UserMessage msg : userMsg) {
			userMsgDisplay.add(from(msg));
		}
		return userMsgDisplay;
	}
	public static UserMessageDisplay from(final UserMessage userMsg) {
		
		Integer currUserId = CurrentUserUtility.getCurrentUser().getUserId();
		return UserMessageDisplay.builder()				
				.incomingMsg(userMsg.getSrcUserId().intValue() == currUserId.intValue() ? Boolean.FALSE : Boolean.TRUE)
				.date(DateTimeUtil.formatDate(userMsg.getDateCreated(), "dd-MM-yyyy HH:mm"))
				.msgViewed(userMsg.getMsgViewed())
				.msgFlag(userMsg.getMsgFlag())
				.msgContent(userMsg.getMsgTxt())
				.msgId(userMsg.getId())
				.build();
	}
	
	public static Map<String, List<UserMessageDisplay>> from(final Map<String, List<UserMessage>> userMessages){
		
		Map<String, List<UserMessageDisplay>> mapUserMsg = new HashMap<>();
			
		final Iterator<String> iterator = userMessages.keySet().iterator();
		
		while(iterator.hasNext()) {
			String key = (String)iterator.next();
			List<UserMessage> currList = userMessages.get(key);
			List<UserMessageDisplay> currUserDisplayList = new ArrayList<>(currList.size());
			for(final UserMessage uMsg : currList) {
				UserMessageDisplay userMsgDisplay = from(uMsg);
				currUserDisplayList.add(userMsgDisplay);
			}
			mapUserMsg.put(key, currUserDisplayList);
		}	
		return mapUserMsg;
		
	}

}
