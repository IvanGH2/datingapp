/**
 * 
 */
var userContent = (function(){
	
	startRecord = 0;
	numRecords  = 3;
	
	msgStartRecord = 0;
	msgNumRecords = 3;
	
	viewStartRecord = 0;
	viewNumRecords = 3;
	
	userMsgStartRec = 0;
	userMsgNumRec = 3;
	
	numMsgUsers = 0;
	numUsers = 0;
	msgList = [];
	
	contentSrc = '';

	function init(){
	
	contentSrc = $('#contentSrc').val();
	$(document).on('click', '.btn-more', function(e){
		e.stopImmediatePropagation();
		//e.preventDefault();
		var val = $(this).val();
		var id =  $(this).attr('id');
		var msgStart= parseInt($(this).attr('data-msgStart'));
		//userMsgStartRec = userMsgStartRec + userMsgNumRec;
		userMsgStartRec = msgStart + userMsgNumRec;
		params = "target=" + val + '&offsetMsg=' + userMsgStartRec;
		$.get('/targetMessages', params, function(response){
				
			if(response.status === 'SUCCESS'){
					var btnId = id.charAt(id.length-1);
					var msgIds = [];
					populateConversationsForTargetUser(response.result, msgIds, btnId);
					$('#btnMore'+btnId).attr('data-msgStart', userMsgStartRec);
					updateMsgStatus(msgIds);
					//populateConversations(response.result, numRec, next);
					
			}else{
					if(response.result)
						commonModule.showAlertInfoFail(response.result, '.inner-message', true);
			}
			
		});
		
	});
	$(document).on('click', '.btn-flag', function(e){
		e.stopImmediatePropagation();
		val = $(this).val();
		$.get('/flagMsg/'+val, null, function(response){
				
			if(response.status == 'SUCCESS'){				
					commonModule.showAlertInfo(response.result, '.inner-message', true);	
					if(response.result == true){
						$obj.text(messageContent.getMessage(messageContent.Message.FLAG, false));
					}else{
						$obj.text(messageContent.getMessage(messageContent.Message.FLAG, true));
					}
					
			}else{
					if(response.result)
						commonModule.showAlertInfoFail(response.result, '.inner-message', true);
			}
			
	});
	});
	
	$(document).on('click', '.btn-msg', function(e){	
	  msgTarget = $(this).val();
	  $('#sendMsgModal').modal().show();
	});
	
	$('#sendUserMsgBtn').unbind();
	$('#sendUserMsgBtn').on('click', function(e){

		params = 'msg=' + $('#msgContent').val() + '&_csrf='+token;
		$.post('/sendMsg/'+msgTarget, params, function(response){
			$('#sendMsgModal').modal('hide');
			if(response.status == 'SUCCESS'){
				commonModule.showAlertInfo(response.result, '.inner-message', true);			
			
			}else{
				commonModule.showAlertInfoFail(response.result, '.inner-message', true);
			}
			
		});	
	});
	$(document).on('click', 'a.usernameLink', function(e){
	//$('a.usernameLink').on('click', function(e){
			e.preventDefault();	
			e.stopImmediatePropagation();
			var href = $(this).attr('href');
			$.get('/getUserProfile'+href, null, function(response){
				
				if(response.status == 'SUCCESS'){
					
					member = response.result;
					$('#userProfileModal').modal().show();
					$('#memberUsername').text(member.username);
					$('#age').text(member.age);
					$('#from').text(member.from);
					$('#education').text(member.education);
					$('#bodyType').text(member.bodyType);
					$('#personality').text(member.personalities);
					$('#profession').text(member.profession);
					$('#relStatus').text(member.relStatus);
					$('#message').text(member.message);
					$('#hobbies').text(member.hobbies);
					$('#imgContainer').empty();
					if(member.photos != null && member.photos.length > 0){
						var imgHtml = '';
						var cls;
						for(i=0; i<member.photos.length; ++i){
							 cls = (i==0) ? 'class="item imgSlider active"' : 'class="item imgSlider"';
						 	imgHtml = imgHtml + '<div ' + cls + '><img class="slider"   src=' + member.photos[i] +'></img></div>';
						}
						$('#imgContainer').append(imgHtml);
						
					}else{
						$('#imgContainer').empty();
						params = null;
					}
					
				}else{
					$('#imgContainer').empty();
					params = null;
				}
			
			});
			
		});
		
		$('#prevMemberBtn').unbind();
		$('#nextMemberBtn').unbind();
		$('#prevMemberBtn').on('click', function(e){
			//e.preventDefault();	
			$('#nextMemberBtn').prop('disabled', false);
			$('#nextMemberBtn').removeClass('disabled');
			var src = $('#contentSrc').val();
			if(startRecord > 0)
				startRecord = startRecord - numRecords >= 0 ? startRecord - numRecords : 0;
			retrieveMembers(startRecord, src);
			if(startRecord == 0){
				$('#prevMemberBtn').prop('disabled', true);
				$('#prevMemberBtn').addClass('disabled');
				}
		});
		
		$('#lastMemberBtn').on('click', function(){
		
		var src = $('#contentSrc').val();
		var numUsers = $('#numMatchingUsers').val();
		startRecord  = numUsers - numRecords >= 0 ? numUsers - numRecords : 0;
		
		retrieveMembers(startRecord,  src);
		$('#nextMemberBtn').prop('disabled', true);
		$('#nextMemberBtn').addClass('disabled');
		if(numUsers <= numRecords){
			$('#prevMemberBtn').prop('disabled', true);
			$('#prevMemberBtn').addClass('disabled');
		}else{
			$('#prevMemberBtn').prop('disabled', false);
			$('#prevMemberBtn').removeClass('disabled');
		}
		
	});
	
		$('#firstMemberBtn').on('click', function(){
		
		startRecord  =  0;
		var src = $('#contentSrc').val();
		retrieveMembers(startRecord, src);
		
		$('#prevMemberBtn').prop('disabled', true);
		$('#prevMemberBtn').addClass('disabled');
		numUsers = $('#numMatchingUsers').val();
		if(numUsers <= startRecord + numRecords){
				$('#nextMemberBtn').prop('disabled', true);
				$('#nextMemberBtn').addClass('disabled');
		}else{
			
				$('#nextMemberBtn').prop('disabled', false);
				$('#nextMemberBtn').removeClass('disabled');
		}
		
		});
		$('#nextMemberBtn').on('click', function(){
			var src = $('#contentSrc').val();
			if(contentSrc != src){
				startRecord = 0;
			}
			
			startRecord = startRecord + numRecords;
		
			retrieveMembers(startRecord, src);
			$('#prevMemberBtn').prop('disabled', false);
			$('#prevMemberBtn').removeClass('disabled');
		});
		
		$('#firstMsgBtn').on('click', function(){
		
		msgStartRecord  =  0;
		msgNumRecords = parseInt($('#selNumMessages').val());
		retrieveMessages(msgStartRecord, msgNumRecords, 0);
		
		$('#prevMsgBtn').prop('disabled', true);
		$('#prevMsgBtn').addClass('disabled');
		
		if(numMsgUsers <= msgStartRecord + msgNumRecords){
				$('#nextMsgBtn').prop('disabled', true);
				$('#nextMsgBtn').addClass('disabled');
		}else{
			
				$('#nextMsgBtn').prop('disabled', false);
				$('#nextMsgBtn').removeClass('disabled');
		}
		
	});
	$('#lastMsgBtn').on('click', function(){
		
		numMsgUsers = $('#numMsgUsers').val();
		msgNumRecords = parseInt($('#selNumMessages').val());
		msgStartRecord  = numMsgUsers - msgNumRecords >= 0 ? numMsgUsers - msgNumRecords : 0;
		
		retrieveMessages(msgStartRecord, msgNumRecords, 0);
		$('#nextMsgBtn').prop('disabled', true);
		$('#nextMsgBtn').addClass('disabled');
		if(numMsgUsers <= msgNumRecords){
			$('#prevMsgBtn').prop('disabled', true);
			$('#prevMsgBtn').addClass('disabled');
		}else{
			$('#prevMsgBtn').prop('disabled', false);
			$('#prevMsgBtn').removeClass('disabled');
		}
		
	});
	$('#nextMsgBtn').on('click', function(){
			//e.preventDefault();	
			
			msgStartRecord  +=  msgNumRecords;
			msgNumRecords = parseInt($('#selNumMessages').val());
			//userMsgStartRec = userMsgStartRec + userMsgNumRec;
		
			retrieveMessages(msgStartRecord, msgNumRecords, 0);
			$('#prevMsgBtn').prop('disabled', false);
			$('#prevMsgBtn').removeClass('disabled');
			numMsgUsers = $('#numMsgUsers').val();
			if(numMsgUsers <= msgStartRecord + msgNumRecords){
				$(this).prop('disabled', true);
				$(this).addClass('disabled');
			}
	});
	$('#prevMsgBtn').on('click', function(e){
			//e.preventDefault();	
			msgNumRecords = parseInt($('#selNumMessages').val());
			if(msgStartRecord > 0){
				msgStartRecord  = msgStartRecord - msgNumRecords;
				if(msgStartRecord < 0) msgStartRecord = 0;
			}
				
			//userMsgStartRec = userMsgStartRec + userMsgNumRec;
			retrieveMessages(msgStartRecord, msgNumRecords, 0);
			if(msgStartRecord == 0){
				$('#prevMsgBtn').prop('disabled', true);
				$('#prevMsgBtn').addClass('disabled');
			}
			$('#nextMsgBtn').removeClass('disabled');
			$('#nextMsgBtn').prop('disabled', false);
			
	});
	
	$('#firstViewBtn').on('click', function(){
		
		viewStartRecord  =  0;
		numViewUsers = parseInt($('#numCtxViews').val());
		viewNumRecords = parseInt($('#selNumViews').val());
		console.log('viewNumRecords' + viewNumRecords);
		retrieveViews(viewStartRecord, viewNumRecords, numViewUsers);	
		
		if(numViewUsers <= viewStartRecord){
				$('#nextViewBtn').prop('disabled', true);
				$('#nextViewBtn').addClass('disabled');
		}else{
			
				$('#nextViewBtn').prop('disabled', false);
				$('#nextViewBtn').removeClass('disabled');
		}
		$('#prevViewBtn').prop('disabled', true);
		$('#prevViewBtn').addClass('disabled');
		$(this).prop('disabled', true);
		$(this).addClass('disabled');
		$('#lastViewBtn').prop('disabled', false);
		$('#lastViewBtn').removeClass('disabled');
		
	});
	$('#lastViewBtn').on('click', function(){
				
		numViewUsers = parseInt($('#numCtxViews').val());
		viewNumRecords = parseInt($('#selNumViews').val());
		console.log('viewNumRecords' + viewNumRecords);
		viewStartRecord  = numViewUsers - viewNumRecords >= 0 ? numViewUsers - viewNumRecords : 0;
		retrieveViews(viewStartRecord, viewNumRecords, numViewUsers);
		$('#nextViewBtn').prop('disabled', true);
		$('#nextViewBtn').addClass('disabled');
		$(this).prop('disabled', true);
		$(this).addClass('disabled');
		if(numViewUsers <= viewNumRecords){
			$('#prevViewBtn').prop('disabled', true);
			$('#prevViewBtn').addClass('disabled');
		}else{
			
			$('#prevViewBtn').prop('disabled', false);
			$('#prevViewBtn').removeClass('disabled');
		}
		
	});
	$('#nextViewBtn').on('click', function(e){
			
			e.stopImmediatePropagation();
			viewNumRecords = parseInt($('#selNumViews').val());
			console.log('viewNumRecords' + viewNumRecords);
			viewStartRecord  = viewStartRecord + viewNumRecords;
		
			retrieveViews(viewStartRecord, viewNumRecords);
			$('#prevViewBtn').prop('disabled', false);
			$('#prevViewBtn').removeClass('disabled');
			$('#firstViewBtn').prop('disabled', false);
			$('#firstViewBtn').removeClass('disabled');
			numViewUsers = parseInt($('#numCtxViews').val());
			if(numViewUsers <= viewStartRecord ){
				$(this).prop('disabled', true);
				$(this).addClass('disabled');
			}
	});
	$('#prevViewBtn').on('click', function(e){
			//e.preventDefault();	
			viewNumRecords = parseInt($('#selNumViews').val());
			console.log('viewNumRecords' + viewNumRecords);
			viewStartRecord  = viewStartRecord - viewNumRecords >= 0 ? viewStartRecord - viewNumRecords : 0;
			numViewUsers = parseInt($('#numCtxViews').val());
			retrieveViews(viewStartRecord, viewNumRecords, numViewUsers);
			if(viewStartRecord == 0){
				$('#prevViewBtn').prop('disabled', true);
				$('#prevViewBtn').addClass('disabled');
			}
			$('#nextViewBtn').removeClass('disabled');
			$('#nextViewBtn').prop('disabled', false);
			$('#lastViewBtn').prop('disabled', false);
			$('#lastViewBtn').removeClass('disabled');
			
	});
		
		$('#prevViewBtn').prop('disabled', true);
		$('#prevViewBtn').addClass('disabled');
		$('#prevMemberBtn').prop('disabled', true);
		$('#prevMemberBtn').addClass('disabled');
		
		$('#prevMsgBtn').prop('disabled', true);
		$('#prevMsgBtn').addClass('disabled');
		//ovo premjestiti negdje drugdje
		numMsgUsers = $('#numMsgUsers').val();
		
		if(numMsgUsers <= msgNumRecords){
			$('#nextMsgBtn').prop('disabled', true);
			$('#nextMsgBtn').addClass('disabled');
		}
		
			
	}	
	function retrieveMembers(offset, src){
		
		if(src === 'search'){
			searchMembers(offset);
			return;
		}
		numRecords = parseInt($('#selNumSearch').val());
		params = "offset=" + offset + "&numRec="+numRecords;
		$.get('/getUserProfiles', params, function(response){
				
				if(response.status == 'SUCCESS'){
					populateMembers(response.result);
					
				}else{
					if(response.result)
						commonModule.showAlertInfoFail(response.result, '.inner-message', true);
				}
			
			});
	}
	function searchMembers(offset){
		numRecords = parseInt($('#selNumSearch').val());
		params = "offset=" + offset + "&numRec="+numRecords;
		var formParams = $('#searchProfileForm').serializeArray();
		$.get('/fetchUserProfiles', params, function(response){
				
				if(response.status == 'SUCCESS'){
					
					populateMembers(response.result);
					
				}else{
					if(response.result)
						commonModule.showAlertInfoFail(response.result, '.inner-message', true);
				}
			
			});
	}
	function fetchMessages(offset, numRec, offsetMsg){
			params = "offset=" + offset+'&numRec='+numRec+ '&offsetMsg=' + offsetMsg;
			$.get('/messages', params, function(response){
				
				if(response.status == 'SUCCESS'){
					//var rStr = JSON.stringify(response.result);
					msgList = response.result;
					var msgIds = [];
					populateConversations(response.result, numRec, msgIds);
					console.log(msgIds)
					updateMsgStatus(msgIds);
				}else{
					if(response.result)
						commonModule.showAlertInfoFail(response.result, '.inner-message', true);
				}
			
			});
	}
	function retrieveViews(offset, numRec, numViews){//numViews se ne koristi ovdje
		
		page = $('#viewPage').val();
		loc = "PROFILE_VIEWS_DST" === page ? '/getUserProfiles/targetViews' : '/getUserProfiles/srcViews';
		
		params = "offset=" + offset +'&numRec='+numRec;
			$.get(loc, params, function(response){
				
				if(response.status === 'SUCCESS'){
					msgList = response.result;
					populateViews(response.result, numRec);
					//startViewRec = offset + numRec;
				}else{
					if(response.result)
						commonModule.showAlertInfoFail(response.result, '.inner-message', true);
				}
			
			});
	}
	function retrieveMessages(offset, numRec, offsetMsg){
		
		numMsgUsers = parseInt($('#numMsgUsers').val(), 10);
		if(numMsgUsers > msgNumRecords){
			$('#pageSpacer').removeClass('nvis');
		}else{
		
			$('#pageSpacer').addClass('nvis');
		}
		if(numMsgUsers <= msgNumRecords){
			$('#nextMsgBtn').prop('disabled', true);
			$('#nextMsgBtn').addClass('disabled');
		}
		var goDb = true;
		currList = msgList.length - (offset + numRec);
		remList = numMsgUsers - offset;
		if(/*currList >= 0 || */ remList <= 0){
			goDb = false;
		}
		if(goDb){
			params = "offset=" + offset+'&numRec='+numRec+ '&offsetMsg=' + offsetMsg;
			$.get('/messages', params, function(response){
				
				if(response.status === 'SUCCESS'){
					//var rStr = JSON.stringify(response.result);
					msgList = response.result;
					var msgIds= [];
					populateConversations(response.result, numRec, msgIds);
					console.log(msgIds)
					updateMsgStatus(msgIds);
					//msgNumRecordsPrev = msgNumRecords;
					
				}else{
					if(response.result)
						commonModule.showAlertInfoFail(response.result, '.inner-message', true);
				}		
			});
		}else{
			//da li ovdje ikad više ide?
			console.log('shouldnt be here')
			//populateConversations(response.result, numRec);
		}
	}
	function updateMsgStatus(msgIds){
		
		if(msgIds.length > 0){			
			params = 'msgIds=' + msgIds + '&_csrf='+token;
			$.post('/updateMsgStatus', params, function(response){
			
			if(response.status === 'SUCCESS'){
				console.log("msg status updated")
			
			}else{
				console.log("msg status not updated")			
			}			
			});	
		}
	}
	function populateViews(views, next){
		
		var viewHtml = "";
		if(views != null && views.length>0){
			$('#profile_views_wrapper').empty();
			if(next && views.length < numRecords){
				$('#nextViewBtn').prop('disabled', true);
				$('#nextViewBtn').addClass('disabled');
				}
			for(i=0; i<views.length;++i){
			onlineHtml = views[i].online ? '&nbsp;<span class="gCol">Online!</span>' : '';	
			viewHtml = viewHtml + '<br><div  class="row"> <div class="col-md-4"> \
			<label for="member.username" >' + messageContent.getMessage(messageContent.Message.USERNAME) + '</label> \
			<span>&nbsp;</span><a class="usernameLink" href=/' + views[i].username + '><span>'+ views[i].username + '</span></a>'+ onlineHtml +'</div> \
			<div class="col-md-4"><label for="profileComplete">'+messageContent.getMessage(messageContent.Message.PROFILE_COMPLETE)+'</label> \
			<span>&nbsp;</span><span class="userProfileSel">' + views[i].profileCompletion  +'</span></div>\
			<div class="col-md-4"><label for="memberJoined">'+messageContent.getMessage(messageContent.Message.SINCE)+'</label> \
			<span>&nbsp;</span><span class="userProfileSel">' + views[i].since + '</span></div> </div> <br><div class="row"> \
			<div class="col-md-4"> \
    					<label for="memberAge" >'+messageContent.getMessage(messageContent.Message.AGE)+'</label> \
    					<span>&nbsp;</span><span class="userProfileSel">' + views[i].age + '</span> \
    		</div> \
			<div class="col-md-4"> \
    		<label for="memberFrom" >'+messageContent.getMessage(messageContent.Message.FROM)+'</label> \
    		<span>&nbsp;</span><span class="userProfileSel">' + views[i].from + '</span> \
    		</div> \
    		<div class="col-md-4"> \
    		<label for="memberLastSeen" >'+messageContent.getMessage(messageContent.Message.LAST_ACTIVITY)+'</label> \
    		<span>&nbsp;</span><span class="userProfileSel">' + views[i].lastActivity + '</span></div></div> <br><div class="row">';
    		 var thick = (i == views.length-1) ? 'class="thick"' : ''; 		
			 viewHtml =  viewHtml + '<div class="col-md-4"><label>'+messageContent.getMessage(messageContent.Message.PROFILE_VISITED_ON)+
									'</label><span>&nbsp;</span><span class="userProfileSel">' + views[i].profileVisitedOn + '</span> \
									</div><div class="col-md-4"></div> <div class="col-md-4"><button type="button" \
									class="btn-msg" value="'+ views[i].username + '"">'+ messageContent.getMessage(messageContent.Message.SEND)+'</button> </div></div><hr '+thick+'> ';    					    		 		    		 
    	
			}
			
			$('#profile_views_wrapper').append(viewHtml);
			/*for(i=0; i<views.length;++i){
				$('#profileComplete'+i).attr('src','data:image;base64,' + getImgAsBase64(views[i].profileCompleted));
			}*/
		}
		
	}
	function populateMembers(members){
		
		var memberHtml = "";
		if(members != null && members.length>0){
			$('#members_wrapper').empty();
			/*if(next && members.length < numRecords){
				$('#nextMemberBtn').prop('disabled', true);
				$('#nextMemberBtn').addClass('disabled');
				}*/
			for(i=0; i<members.length;++i){
			onlineHtml = members[i].online ? '&nbsp;<span class="gCol">Online!</span>' : '';	
			memberHtml = memberHtml + '<br><div  class="row"> <div class="col-md-4"> \
			<label for="member.username" >' + messageContent.getMessage(messageContent.Message.USERNAME) + '</label> \
			<span>&nbsp;</span><a class="usernameLink" href=/' + members[i].username + '><span>'+ members[i].username + '</span></a>' + onlineHtml + '</div> \
			<div class="col-md-4"><label for="profileComplete">'+messageContent.getMessage(messageContent.Message.PROFILE_COMPLETE)+'</label> \
			<span>&nbsp;</span><span class="userProfileSel">' + members[i].profileCompletion  +'</span></div>\
			<div class="col-md-4"><label for="memberJoined">'+messageContent.getMessage(messageContent.Message.SINCE)+'</label> \
			<span>&nbsp;</span><span class="userProfileSel">' + members[i].since + '</span></div> </div> <br><div class="row"> \
			<div class="col-md-4"> \
    					<label for="memberAge" >'+messageContent.getMessage(messageContent.Message.AGE)+'</label> \
    					<span>&nbsp;</span><span class="userProfileSel">' + members[i].age + '</span> \
    		</div> \
			<div class="col-md-4"> \
    		<label for="memberFrom" >'+messageContent.getMessage(messageContent.Message.FROM)+'</label> \
    		<span>&nbsp;</span><span class="userProfileSel">' + members[i].from + '</span> \
    		</div> \
    		<div class="col-md-4"> \
    		<label for="memberLastSeen" >'+messageContent.getMessage(messageContent.Message.LAST_ACTIVITY)+'</label> \
    		<span>&nbsp;</span><span class="userProfileSel">' + members[i].lastActivity + '</span></div></div> <br><div class="row">';
    		 var thick = (i == members.length-1) ? 'class="thick"' : ''; 		
			 memberHtml =  memberHtml + '<div class="col-md-8"></div> <div class="col-md-4"><button type="button" class="btn-msg" value="'+ members[i].username + '">'+ messageContent.getMessage(messageContent.Message.SEND)+'</button> </div></div><hr '+thick+'> ';    					    		 		    		 
			}
			
			$('#members_wrapper').append(memberHtml);
		}
		
	}
	
	function populateConversationsForTargetUser(msg,  msgIds, index){

		cnvHtml = populateUserMessages( msg, msgIds);
		$('#msgCon'+ index).append(cnvHtml);
	}
	function populateUserMessages(msg, msgIds){
		suffix = 0;
		cnvHtml = '';
		
		for(j=0; j<msg.length;++j){
					suffix += j;
					var incoming = msg[j].incomingMsg;
					var seen = msg[j].msgViewed;
					var hint = incoming ? 'title="Incoming message"' : 'title="Outgoing message"';
					if(incoming && !seen ){
						msgIds.push( msg[j].msgId );
					}
					dirImgSrcHtml = ' src="data:image;base64,' + getMsgDirImg(incoming) + '" ';
					viewImgSrcHtml = ' src="data:image;base64,' + getImgAsBase64(seen) + '" ';
					
					flagMsgHtml = incoming ? '<div class="col-md-2"><button class="btn-flag" value="'+ msg[j].msgId + '">'+messageContent.getMessage(messageContent.Message.FLAG, msg[j].msgFlag)+'</button></div>' : ''; 
					cnvHtml += '<br><div class="row"><div class="col-md-1"><img '+ dirImgSrcHtml + hint +'id="msgDirection' + suffix + '"> \
							</div><div class="col-md-4"><span>' + messageContent.getMessage(messageContent.Message.SENT_ON) + '</span><span>&nbsp;</span>';
					cnvHtml += '<span class="userProfileSel">' + msg[j].date + '</span></div><div class="col-md-2"><span>' + messageContent.getMessage(messageContent.Message.VIEWED) +'</span><span>&nbsp;</span>';
					cnvHtml += '<img id="msgSeen' + suffix  +'"' + viewImgSrcHtml + '></div></div>';
					//cnvHtml += '<img id="msgSeen' + suffix + '"></div><div class="col-md-2"><button class="btn-flag" value="'+ msg[j].msgId + '">'+messageContent.getMessage(messageContent.Message.FLAG, msg[j].msgFlag)+'</button></div></div>';
					cnvHtml += '<br><div class="row"><div class="col-md-1"></div><div class="col-md-10"><p class="msgContent">' + msg[j].msgContent + '</p></div></div>';
					cnvHtml += '<br><div class="row">' + flagMsgHtml + '</div><hr>';
					cnvHtml += '<br><input type="hidden" id="msgId'+suffix+'" >';
					
			}	
		return cnvHtml;	
	}
	function populateConversations(conversations, numRec, msgIds){
	
		cnvHtml = '';
	    suffix = 0;
		if(conversations != null && conversations.length>0){
			$('#members_msg_wrapper').empty();
			var len = numRec > conversations.length ? conversations.length : numRec;
			for(i=0; i<len;++i){//${conversations[i].msgTarget}
				var msg = conversations[i].userMsgDisplay;//<a class="usernameLink" href="#"><span>'+ members[i].username + '</span></a>
				suffix += i;
				var msgStart = $('#btnMore'+i).attr('data-msgStart') == null ? 0 : $('#btnMore'+i).attr('data-msgStart');
				onlineHtml = conversations[i].online ? '&nbsp;<span class="gCol">Online!</span>' : '';
				cnvHtml += '<div class="row"><div class="col-md-2"><a class="usernameLink" href=/' + conversations[i].msgTarget + '><span>'+ conversations[i].msgTarget + '</span></a>'+ onlineHtml + '</div>';
				cnvHtml += '<div class="col-md-7"></div>';
				cnvHtml += '<div class="col-md-2"><button class="btn-msg" value="'+ conversations[i].msgTarget + '">'+messageContent.getMessage(messageContent.Message.SEND)+'</button></div>';
				cnvHtml += '<div class="col-md-1"></div></div>';
				//cnvHtml += '<div class="col-md-2"><button class="btn-flag" value="'+ conversations[i].msgTarget + '">'+messageContent.getMessage(messageContent.Message.BLOCK_USER)+'</button></div></div>';
				cnvHtml += '<div class="msgContainer">';
				cnvHtml += '<div id="msgCon' + i + '" >';
				cnvHtml += populateUserMessages( msg, msgIds);
				
				cnvHtml += '</div>';
				cnvHtml += '<div class="row"><div class="col-md-5"></div><div  class="col-md-2"><button data-msgStart="'+msgStart+'"id="btnMore' + i + '" \
				class="btn-more"  value="' + conversations[i].msgTarget + '">'+messageContent.getMessage(messageContent.Message.MORE)+'</button></div><div class="col-md-5"></div></div></div>';
				if(i < len-1)
					cnvHtml += '<hr class="thick" >';
				
			}
			$('#members_msg_wrapper').append(cnvHtml);
		}
	}	
	function getMsgDirImg(incoming){
		
		var incImg ="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";
		
		var outImg = "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";
	
		return incoming  ? incImg : outImg;
	}
	function getImgAsBase64(checkImg){
		
	var cancel = "iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAEDElEQVR42t2VXUxbZRjHn/ecfqwt \
	Hx3tagcU27UdpcgQUGJQRooKmkyz6IUmhlKpi9F4s6mL0xGzuEQvjO7O7AMsXaZuUROzOLMa2BTN \
	thhBZj8o4/DRobRIIdsoLbXw+pwDVMUz1ERv9ia96PM+z//3fPUtgf/5kFsT8DHLFmsaGnwJjvt2 \
	NhJ5toXSJTG/DgBGq9Mdzq+puTd+/nzT48nkxN8CPgIwsAA99udaLLFL38N0X9CTAHA/DfAnyGEU \
	34gMTYXVVfigg/rfOcKhQ+MTAFdvCjgBUMwAnCt91GEuu9tBfs1TwIUj79HpwEjXLEJeWIEcQvFN \
	vHiZyVXncoOMSGnoh2/I4Aenh9HB8RTAhCjACxC0OKptluoGoJmVhPUFcOm4B4aCo94wQjJoqkRx \
	c5nRWfskSs3MCW5Mbg6MDPVB+NQXg04AuyjgfYCjG426ZyofaqaLKXb1jrLFOuL75BQEQ2PHeUOF \
	7faWxp2P0aXobNZHbiok/Sc/hHhw9Bi2c5co4C1MRIPZFRg3tdq315P0daEKStGPMW2G02c+Bwa/ \
	7mh+mCxFpuhqvFyvpsFzPSQejnhuYJV7/jCvvwz5YBaibbXdVQML8bRgX8SPxFIo4Ba5SQTxVAaU \
	5kIIdH8JcS7axYvvXbMMomt6ACEF/BCNGlep3UZT0aTgJ0QSQlXFxQQUSsrkZ0io+2uYHo55cBLu \
	fWvEbwrgT/sypFNr1DhtNivMR64DkcuBYtaJ8XGQalmYTKYhPj7rRfG2dhHxdQH7EZCHAB0CrBsk \
	JDEYW70Seq+y6emVVIZMjU13JRHw+r8BvLIs3nGbUeuqUkjpQmiS0JU5CFvFN4rfHHsR6ccqYqO/ \
	eBDifuOftOhlfqVRXG/SueoVMsgEJ4SM+chEeQkfQJWBCGFWKpGgrTeVoVHuZ74S95vrDXmPQsbk \
	JtMdm81Frc0KKYB/LNuTG9ussDuREEIOqVSQe3no98BtW+EsVhIdHu9Sbtni3stx4mv6WpH2mEGt \
	du+UsFQyEM7+iK7dWU5eRPFrV8a8fKTaanK+m6Oief3+rE8Gfc7KNgCj1nS2+HxuUcD+El2wtrKi \
	rPFCP4XpGeFurqqCvpRIkKmhES/2vi2zvK6dhVvNLW/nqIiq7/JykVoNudhQBz9+NxDaHYmIPxXt \
	SrmBnV/ouef+7eb6gQDMlxTBwcQ8cOFhPvO2Myv9bcI5SRBiK7U496mUoIz8BL2Vd8DF7q84ViZz \
	tKfT4o8df3DdDGjsqX6kyeIfGQd/INyFnW/7bM3wdiAkHyFV9tLWuopy6jv5KYezajyw3nO9el41 \
	6Y1ULvWlJmd643PJXd7FJdEdb2VZptZgOGo3m+8L+f0PPB+LXV3rc4v+J/+X5zduKY8ohxjKHgAA \
	AABJRU5ErkJggg";
	
	var check = "iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAADJElEQVR42u2Ta0iTURjHn7Ob79w7 \
	3XRzW61ypYmlWbbEym4Lxe4X6SpFiF2QjCIiooyQqA9dsILEroSBVFYYlDqsRDOtMJMu0tScmptZ \
	rS3X5ru9e097C6RypWD1yT+cT+fh/3vOef4Pgn8sNAT4LwDFTgWREp+yrcncdFW/TW/8qwAigxAI \
	lIJrOatyFmeXZje3Nrbq8Cnc9lcAZAbJJ4YTBem69OWTR08Gs8UMWUVZTfAB5tiO2joGBQjYGMAT \
	qUX5KxNWro5WR2MM+JtXl7ULl9SV3KncUrnQJyByf+TypJik7cV1xemGgwaDL3PJJglXrBRfXBA/ \
	f12EYiwwDMaYwYjxYMivzXcbW4xru493F/YBBGwNWDIpOuZK8vhEwfUnRe0mo1lnOmFq/rEmKC2I \
	E6gKPDN3yqy0UNlIYE0Zj9eI4UDRi1ueZw31aUwek99nBvJMebJGo76pi5pJIC5g2kOj8tqalrdt \
	Jp0512xka4JTg5F0uPT0DK128zC5Ejweb9c0AB/4uMJQxTx++XQTlUdd+LGhXkD4nvA7idq4ZK5A \
	ABwefDuYZqDy4bPmtybzHFePu0MqleQkxE/IlKtk4PHeseYEEkL9mwZcUV+T4TzrzPv1O3sB5AZy \
	TIhCcn+2NnYEj+BjLh8Qh+e9phhcde+1wWLrvj99WsRmWaiENWc7x0IkRq2tnYz+yYPt9vP2U77m \
	9dMMROtFEWql/N70qeEqTADi8hH7EgxfMOp+1wOSMNLbsQgjmosohxO/b3eB/lH1LutF27Hfpa1P \
	ikSponEaVcjd2BkhSsrPAV4IRhw2ghhomgar/SOmnC6E22X4+SvLXsulT4f/FGefe0CuICdoVMFl \
	YYlumdP/ixfyvRKzaXFzgH4hg0ePuw5YC2zZ/e3LbxeNXEbGhqmD9VFrmCCHyI4Qxzt4mgOuahku \
	L+s8ZLth29ef+R8BrPwX+cdFjpKXajNB4hQ6wHVXBsWFHUd6aGo3VUrhQQNYEfOIqbFjVSUTEyTi \
	y+caT1Ie1w6qzDUg8wEBWMmXiuPEQr8pXZ/tufbbPcxAzQcMGIyGAP3qKzm/QSiqSB44AAAAAElF \
	TkSuQmCC";

	return checkImg ? check : cancel;
		
	}
	return{
		init: init,
		retrieveMessages: retrieveMessages,
		retrieveViews: retrieveViews
	}
	})();


/**
 * 
 */