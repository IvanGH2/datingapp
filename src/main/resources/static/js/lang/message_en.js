/**
 * 
 */
const messageContent = (function(){
	

	const Message = {
		
		USERNAME: 		0,
		SINCE: 			1,
		FROM: 			2,
		AGE: 			3,
		LAST_ACTIVITY: 	4,
		PROFILE_COMPLETE: 5,
		SENT_ON: 6,
		VIEWED: 7,
		DIRECTION: 8,
		SEND: 9,
		PROFILE_VISITITED_ON: 10,
		MORE:	11,
		FLAG: 12,
		BLOCK_USER: 13,
		EMAIL_WRONG_FORMAT: 14,
		PSW_REQ_NOT_MET: 15,
		PSW_CFRM_SAME: 16,
		RETRIEVE_OPT_NS: 17
		
	}
	function init(){}
	function getMessage(m, attr){
		
		switch(m){
			
			case Message.USERNAME:
				return 'Username';
			case Message.SINCE:
				return 'Member since';
			case Message.FROM:
				return 'From';
			case Message.LAST_ACTIVITY:
				return 'Last activity:';
			case Message.AGE:
				return 'Age';
			case Message.PROFILE_COMPLETE:
				return 'Profile completed';
			case Message.SENT_ON:
				return 'Sent on:';
			case Message.VIEWED:
				return 'Seen:';
			case Message.DIRECTION:
				return 'incoming' == attr ? 'Received message' : 'Sent message';
			case Message.SEND:
				return 'Send message';
			case Message.PROFILE_VISITED_ON:
				return 'Profile visited on';
			case Message.MORE:
				return 'Load more messages ...';
			case Message.FLAG:
				return attr == true ? 'Unflag message' : 'Flag message';
			case Message.BLOCK_USER:
				return 'Block user';
			case Message.EMAIL_WRONG_FORMAT:
				return 'The email format is not correct or is empty';
			case Message.PSW_REQ_NOT_MET:
				return 'Password complexity requirements are not met (minimum 1 uppercase letter, 1 number and 1 special character)!';
			case Message.PSW_CFRM_SAME:
				return 'Password and confirmed password don\'t match!';
			case Message.RETRIEVE_OPT_NS:
				return 'No option to retrieve has been selected!';
				
			
		}
		return '';
	}

	return{

		getMessage : getMessage,
		Message : Message
		
	}
	})();


/**
 * 
 */