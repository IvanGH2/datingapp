/**
 * 
 */
/**
 * 
 */
var messageContent = (function(){
	
	//export let Message;

	const Message = {
		
		USERNAME: 0,
		SINCE: 1,
		FROM: 2,
		AGE: 3,
		LAST_ACTIVITY: 4,
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
	//function init(){}
	function getMessage(m, attr){
		
		switch(m){
			
			case Message.USERNAME:
				return 'Korisničko ime';
			case Message.SINCE:
				return 'Član od';
			case Message.FROM:
				return 'Iz';
			case Message.LAST_ACTIVITY:
				return 'Zadnja aktivnost:';
			case Message.AGE:
				return 'Dob';
			case Message.PROFILE_COMPLETE:
				return 'Profil gotov';
			case Message.SENT_ON:
				return 'Poslano:';
			case Message.VIEWED:
				return 'Pogledana:';
			case Message.DIRECTION:
				return 'incoming' == attr ? 'Primljena poruka' : 'Poslana poruka';
			case Message.SEND:
				return 'Pošaljite poruku';
			case Message.PROFILE_VISITED_ON:
				return 'Profil posjećen ';
			case Message.MORE:
				return 'Učitaj više poruka...';
			case Message.FLAG:
				return attr == true ? 'Odznači poruku' : 'Označi poruku';
			case Message.BLOCK_USER:
				return 'Blokiraj korisnika';
			case Message.EMAIL_WRONG_FORMAT:
				return 'Email format nije ispravan ili nije napunjen';
			case Message.PSW_REQ_NOT_MET:
				return 'Zahtjevi kompleksnosti lozinke nisu ispunjeni(min 1 veliko slovo, 1 broj i specijalni znak)';
			case Message.PSW_CFRM_SAME:
				return 'Zaporka te potvrđena zaporka nisu iste!';
			case Message.RETRIEVE_OPT_NS:
				return 'Nije odbarana niti jedna opcija za vratiti!';
		}
		return '';
	}

	return{

		getMessage : getMessage,
		Message : Message
	//	init: init
	
	}
	})();


/**
 * 
 */