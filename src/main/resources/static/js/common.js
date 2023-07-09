var commonModule = (function(){
	userSignedIn = false;
	sseSet = false;
	function init(){
			
		//session.userSignedIn
		
		$('#userProfileLink').on('click', function(e){
			e.preventDefault();	
			params = 'ctx=100&_csrf='+token;
			$.post('/letsclick_', params, function(response){
				
				if(response.status == 'SUCCESS'){
					params = null;
					location.reload();
					
				}else{
					params = null;
				}
			
			});
			
		});

		}
		
		function initSse(){					
					var source = new EventSource("/getSse");
					source.onmessage = function(event) {
					
			        var evData = JSON.parse(event.data);
					console.log(evData.evNum);
				
					if(evData.evNum > 0){
						
						if(evData.evType === 'msg'){
							$('#numNewMsg').text(evData.evNum);
							$('#sseMsg').removeClass('nvis');	
						}else{
							$('#numViewMsg').text(evData.evNum);
							$('#sseView').removeClass('nvis');
						}
					}else{
						if(evData.evType === 'msg')
							$('#sseMsg').addClass('nvis');	
						else
							$('#sseView').addClass('nvis');				
					}					
				}; 
			
		}
	function showAlertInfo(poruka, selector, append, fade){
		var $html = $('<div class="alert alert-info2">' + '<button type="button" class="close" data-dismiss="alert">&times;</button>' + poruka + '</div>');
		
		showAlertMsg($html, selector, append, fade);
							
	}
	function showAlertInfoFail(poruka, selector, append, fade){
		var $html =  $('<div class="alert alert-info2-fail">' + '<button type="button" class="close" data-dismiss="alert">&times;</button>' + poruka + '</div>');
		
		showAlertMsg($html, selector, append, fade);
		
	}
	function showAlertMsg($html, selector, append, fade){
	
		var _selector = ".inner-message";

		if(selector) {
			_selector = selector;
		}

		if($(_selector).find('.alert').length >= 5) {
			$(_selector).find('.alert:first').alert('close');
		}

		if(append) {
			$(_selector).append($html);
		} else {
			$(_selector).html($html);
		}

		if(fade == false) {
			return;
		}

		$($html).fadeTo(10000, 0.3, function(){
			$($html).alert('close');
		});
	}
	function otvoriModal(selector){
		$(selector).modal({
			show: true,
			backdrop: 'static'
		});
	}

	return{
		init: init,
		showAlertInfoFail: showAlertInfoFail,
		showAlertInfo: showAlertInfo,
		initSse: initSse
	}
	})();


