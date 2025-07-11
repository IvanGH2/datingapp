/**
 * 
 */const adminModule = (function(){
 
 	let flaggedUsersSelected = false;
	
	function init(){
	   
	
		$('#btnGetUser').on('click', function(){
			var user = $('#username').val();
			location.href = "/manageUsers/" + user;
		});
		
		$('#changePswBtn').on('click', function(e){
			e.stopImmediatePropagation();
			e.preventDefault();
			if(checkPsw()){
				
			let params = 'newPsw=' + $('#passwordId').val() + '&_csrf=' + token;
			$.post(contextRoot + 'changePassword', params, function(response){
				if(response.status === 'SUCCESS') {
					commonModule.showAlertInfo(response.result, '.inner-message', true);
				} else {
					commonModule.showAlertInfoFail(response.result, '.inner-message', true);
				}
			});
			}
		});
		$('.thumbnail').on('click', function(e){
			e.preventDefault();	
			e.stopImmediatePropagation();
			let imgSrc = "imageUrl=" + $(this).attr('src') ;
			
			$.get("/deleteUserImage", imgSrc, function(response){
				if(response.status == 'SUCCESS') {
					commonModule.showAlertInfo(response.result, '.inner-message', true);
				}else{
					commonModule.showAlertInfoFail(response.result, '.inner-message', true);
				}
			});
		});
		$('#btnAddRole').on('click', function(e){
			e.stopImmediatePropagation();
			modifyRole(2);
		});
		$('#btnRemoveRole').on('click', function(e){
			e.stopImmediatePropagation();
			modifyRole(1);
		});
		
		$('#btnBlockUser').on('click', function(e){
			e.stopImmediatePropagation();
			let $targetUser = $('#targetUser').text();
			if(targetUser == null || targetUser.length == 0) 
				return;
			var params = '_csrf=' + token;
			$.post(contextRoot + 'blockUser/' + $targetUser, params, function(response){
				if(response.status == 'SUCCESS') {
					commonModule.showAlertInfo(response.result, '.inner-message', true);

				} else {
					commonModule.showAlertInfoFail(response.result, '.inner-message', true);
				}
			});
			
		});
		$('#btnBlockUsers').on('click', function(e){
			e.preventDefault();
			e.stopImmediatePropagation();
			let ids = [];
			$('.chBx').each(function(){
				if(this.checked){
					ids.push(this.value);
				}
			});
			
			if(ids.length > 0)
				blockUsers(ids);
		});
		$('#btnSelectAll').on('click', function(e){
				e.stopImmediatePropagation();
				flaggedUsersSelected = !flaggedUsersSelected;
				$('.chBx').each(function(){ 
					this.checked = flaggedUsersSelected;
				});
			
			if(flaggedUsersSelected )
				$('#btnSelectAll').prop('value', 'Unselect all');
			else
				$('#btnSelectAll').prop('value', 'Select all');
			
		} );
		
		$('#registerUserForm').on('submit', function(e){
			e.preventDefault();
			let $form = $(this);
			let formData = $form.serializeArray();
			$.post(contextRoot + 'processRegisterInfo', formData, function(response){
				if(response.status === 'SUCCESS') {
					commonModule.showAlertInfo(response.result, '.inner-message', true);
					$('#regModal').modal('hide');

				} else {
					commonModule.showAlertInfoFail(response.result, '.inner-message', true);
				}
			});
		});

		$('#locales').on( 'change', function(){
	
			let loc = "lang=" + $('#locales').val();
			
			if(loc.length>0){
				
				currLoc = location.href.replaceAll("lang=en", loc);
				currLoc = currLoc.replaceAll("lang=hr", loc);
				currLoc = currLoc.replaceAll("#", "");
				currLoc +=   ( !currLoc.includes('?') ) ? '?'+loc  : '&'+loc;
				
				location.href = currLoc;
				}
			});
				
		$('#signOutLink').on('click', function(e){
			
			$('#signOutForm').submit();
		});
		$('#signInLink').on('click', function(e){
			
			$('#signinModal').modal('show');
			if( errVal != null && errVal.length >0){
					$('#errMsg').text(errVal );
					$('#errMsg').addClass('is-error');
				}
		});
		$('#registerLink').on('click', function(e){
			
			$('#regModal').modal('show');
		});
		$('#retrieveCredentialsBtn').on('click', function(e){
			e.preventDefault();
			let optionSelected = -1;
			let options = document.getElementsByName("credentials");
			let len = options.length;
			for(i=0; i<len;++i){
				if(options[i].checked == true )
					optionSelected = i;	
			}
			
			if(optionSelected != -1 && validateEmail($('#emailId').val())){
							
				let params = $(retrieveCredentialsForm).serializeArray();
				$.post('/register/retrieveCredentials', params, function(result){
				
				if(result.status == 'SUCCESS'){
					$('#retrieveCredentialsModal').modal('hide');
					commonModule.showAlertInfo(result.result, '.inner-message', true);
				}else{
					commonModule.showAlertInfoFail(result.result, '.inner-message', true);				
				}		
			});
			}else{	
				let errMsg = 	optionSelected != -1 ? messageContent.getMessage(messageContent.Message.EMAIL_WRONG_FORMAT) 
				: messageContent.getMessage(messageContent.Message.RETRIEVE_OPT_NS);
				$('#retrieveCredentialsResponseTarget').text(errMsg);
				$('#retrieveCredentialsResponseTarget').addClass('textMsgFailure');
			}		
		});	
		$("#retrieveCredentialsModal").on('show.bs.modal', function (e) {
    		$("#signinModal").modal("hide");
		});
		
		$('#lostPswButton').on('click', function(e){
			
			$('#retrieveCredentialsModal').modal('show');
			
		});
		$('#registerUserButton').on('click', function(e){
			$('#regErrMsg').text('');
			if(!registerValidation() || !checkRegisterDataForConsistency()    ){	
				return;
				}
			$('#registerUserForm').submit();
		});
		function checkRegisterDataForConsistency(){
			
			$('#regErrMsg').removeClass('leftAlign', 'is-error');
			if(!checkPsw()){
				return false;
			}
			if(!validateEmail($('#email').val())){
					
					$('#regErrMsg').text(messageContent.getMessage(messageContent.Message.EMAIL_WRONG_FORMAT));
					$('#regErrMsg').addClass('is-error');
					$('#regErrMsg').addClass('leftAlign');
					$('#email').siblings('label').addClass('is-error');
					return false;
				}
			return true;
		}
		$('#activationEmailBtnMain').on('click', function(e){
			
			let aLink = $('#seModal').val();
			if(aLink == -1)
			 	$('#emailModal').modal('show');
			 else {
			 	var params = 'aLink='+aLink;
			 	$.get('/register/recreateActivationLink', params, function(result){
				$('#pMsg').text(result.result);
				if(result.status === 'SUCCESS'){
				
					$('#pMsg').removeClass('textMsgFailure');
					$('#pMsg').addClass('textMsgSuccess');
					$('#activationEmailBtnMain').prop('disabled', true);
					
				}else{	
					$('#pMsg').addClass('textMsgFailure');
					$('#pMsg').removeClass('textMsgSuccess');
					$('#activationEmailBtnMain').prop('disabled', false);
				}	
			});
			 }		
		});
		$('#activationEmailButton').on('click', function(e){
			
			//check email format
			$('#activationEmailForm').submit();
			
		});
		$('#activationEmailForm').on('submit', function(e){
			e.preventDefault();
			let params = $(this).serializeArray();
			if(!validateEmail($('#email').val())){
				$('#emailResTarget').text('The email address format is incorrect!');
				$('#emailResTarget').addClass('textMsgFailure');
				return;
			}
			$.post('/register/recreateActivationLink', params, function(result){
				
				if(result.status == 'SUCCESS'){
					$('#emailModal').modal('hide');
					$('#pMsg').text(result.result);
					$('#pMsg').removeClass('textMsgFailure');
					$('#pMsg').addClass('textMsgSuccess');
					$('#activationEmailBtnMain').prop('disabled', true);
					
				}else{
					$('#emailResTarget').text(result.result);
					$('#emailResTarget').addClass('textMsgFailure');
					$('#pMsg').removeClass('textMsgSuccess');
				}
			
			});
			
		});
		}//end init()
		function validateEmail(email){
			
			const regex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  			return regex.test(email);
		}
		function blockUsers(ids){
			
			let params = 'userIds=' + ids + '&_csrf='+token;
			$.post('/blockUsers', params, function(response){
			
			if(response.status === 'SUCCESS'){
				commonModule.showAlertInfo(response.result, '.inner-message', true);					
			}else{
				commonModule.showAlertInfoFail(response.result, '.inner-message', true);
			}
			
		});	
		}
		function registerValidation(){
			let ok = true;
			let valid;
			//process <input> tags
			let $selector = $('#registerUserForm');
			let form_input =$selector.find('input');
			$.each(form_input, function(k, v){
				valid = v.checkValidity();
				ok = ok && valid;
				if(!valid) {
					$(v).siblings('label').addClass('is-error');
				} else {
					$(v).siblings('label').removeClass('is-error');
				}
			});
			//process <select> tags
			let form_select= $selector.find('select');
			$.each(form_select, function(k, v){
				valid = v.checkValidity() && $(v).val() != '-1';
				ok = ok && valid;
				if(!valid) {
					$(v).siblings('label').addClass('is-error');
				} else {
					$(v).siblings('label').removeClass('is-error');
				}
		}); 
			return ok;
		}
		function checkPsw(){
			let psw = $('#passwordId').val();
			let pswConfirmed = $('#passwordConfirmedId').val();
			
			const regex = /^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^A-Za-z0-9]).{8,15}$/;
			
			if(psw === pswConfirmed) {
				if(regex.test(psw)){
				
					return true;
				}else{
					setIncorrectPswMsg(messageContent.getMessage(messageContent.Message.PSW_REQ_NOT_MET));					
				}			
			}else{
				setIncorrectPswMsg(messageContent.getMessage(messageContent.Message.PSW_CFRM_SAME));		
			}
			return false;
		}
		
		function setIncorrectPswMsg(msg){
				$('#regErrMsg').text(msg);
				$('#regErrMsg').addClass( 'is-error');
				$('#regErrMsg').addClass('leftAlign');
				$('#passwordId').siblings('label').addClass('is-error');
				$('#passwordConfirmedId').siblings('label').addClass('is-error');
		}
		function isInputValid(input){
			
			if(input == null || input.length == 0)
				return false;
			else
				return true;
		}
		function modifyRole(role){
			
			let $targetUser = $('#targetUser').text();
			if(targetUser == null || targetUser.length == 0) 
				return;
			let params = '_csrf=' + token;
			$.post(contextRoot + 'modifyRole/' + $targetUser + "/" + role, params, function(response){
				if(response.status == 'SUCCESS') {
					commonModule.showAlertInfo(response.result, '.inner-message', true);

				} else {
					commonModule.showAlertInfoFail(response.result, '.inner-message', true);
				}
			});
		}
	return{
		init: init
	}
	})();
	