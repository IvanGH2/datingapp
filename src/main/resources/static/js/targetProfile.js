var targetProfileModule = (function(){
	
	var numSearchRecords  = 3;
	
	function init(){
		
		$('#relStatus').select2();
		$('#personalities').select2();
		$('#eduLevel').select2();
		$('#bodyType').select2();
		$('#userEmploymentId').select2();
		$('#userChildren').select2();
		$('#userCountryId').select2();
		/*$('#ageFrom').select2();
		$('#ageTo').select2();*/
		//populateSearchProfile()
		
		$('#searchUserTargetProfileBtn').on('click', function(e){
			e.preventDefault();
			if( $('#ageFrom').val() === '-1' ||  $('#ageTo').val() === '-1'){
				$('#ageFrom').siblings('label').addClass('has-danger');
				return;
			}
							
			numSearchRecords = $('#numUserSearch').val();
			$('#searchProfileForm').attr('action', '/searchUserProfile?offset=0&numRec=' + parseInt(numSearchRecords));
			$('#searchProfileForm').submit();
			
		});
		
		$('#saveUserProfileBtn').on('click', function(e){
			e.preventDefault();
			var formParams = $('#searchProfileForm').serializeArray();
			
			$.post(contextRoot + 'saveUserProfile/target', formParams, function(response){
				
				if(response.status == 'SUCCESS'){
					commonModule.showAlertInfo(response.result, '.inner-message', true);	
					
				}else{
					commonModule.showAlertInfoFail(response.result, '.inner-message', true);
				}
			
			}).fail( function(response){
				commonModule.showAlertInfoFail(response.text, '.inner-message', true);
			});
		
		});
			
		$('#targetProfileForm').on('submit', function(e){
			
			e.preventDefault();
			
			var formParams = $(this).serializeArray();
			
			$.post(contextRoot + 'saveUserProfile/target', formParams, function(response){
				
				if(response.status == 'SUCCESS'){
					commonModule.showAlertInfo(response.result, '.inner-message', true);	
					
				}else{
					commonModule.showAlertInfoFail(response.result, '.inner-message', true);
				}
			
			}).fail( function(response){
				commonModule.showAlertInfoFail(response.text, '.inner-message', true);
			});
			
		})
		
		//session.userSignedIn
		$('#userProfileLink').on('click', function(e){
			e.preventDefault();	
			params = 'ctx=100&_csrf='+token;
			$.post('/letsclick_', params, function(response){
				
				if(response.status === 'SUCCESS'){
					params = null;
					location.reload();
					
				}else{
					params = null;
				}
			
			});
			
		});
		}
	
	function populateSearchProfile(searchProfile){
			
			$('#ageFrom').val(searchProfile.ageFrom);
			
			$('#ageTo').val(searchProfile.ageTo);
			
			if(searchProfile.gender == '1')
				$('#gender-woman').attr('checked', true);
			else
				$('#gender-man').attr('checked', true);
			
			if(searchProfile.eduLevelInclude)
				$('#inclusiveEdu').attr('checked', true);
			else
				$('#exclusiveEdu').attr('checked', true)
				
			if(searchProfile.employmentStatusInclude)
				$('#inclusiveEmp').attr('checked', true);
			else
				$('#exclusiveEmp').attr('checked', true)
				
			if(searchProfile.perosnalityTypeInclude)
				$('#inclusivePersonalities').attr('checked', true);
			else
				$('#exclusivePersonalities').attr('checked', true)
			
			if(searchProfile.bodyTypeInclude)
				$('#inclusiveBody').attr('checked', true);
			else
				$('#exclusiveBody').attr('checked', true)
				
			if(searchProfile.relStatusInclude)
				$('#inclusiveRelStatus').attr('checked', true);
			else
				$('#exclusiveRelStatus').attr('checked', true)
				
			if(searchProfile.childrenStatusInclude)
				$('#inclusiveChildren').attr('checked', true);
			else
				$('#exclusiveChildren').attr('checked', true)
			
			var eduLevel = searchProfile.eduLevel.substring(1, searchProfile.eduLevel.length-1).split(", ");
			$target = $('#eduLevel');
			for(i=0;i<eduLevel.length;++i)
				setItem($target, eduLevel[i]);
			//setItem($target, 5);
			
			if(searchProfile.bodyType){
			var bodyTypes = searchProfile.bodyType.substring(1, searchProfile.bodyType.length-1).split(", ");
				$target = $('#bodyType');
				for(i=0;i<bodyTypes.length;++i)
					setItem($target, bodyTypes[i]);	
			}
			
			if(searchProfile.employmentStatus)	{
				var empStatus = searchProfile.employmentStatus.substring(1, searchProfile.employmentStatus.length-1).split(", ");
				$target = $('#userEmploymentId');
				for(i=0;i<empStatus.length;++i)
					setItem($target, empStatus[i]);	
			}
			if(searchProfile.relStatus)	{
				var relStatus = searchProfile.relStatus.substring(1, searchProfile.relStatus.length-1).split(", ");
				$target = $('#relStatus');
				for(i=0;i<relStatus.length;++i)
					setItem($target, relStatus[i]);	
			}
			if(searchProfile.childrenStatus){
				var childStatus = searchProfile.childrenStatus.substring(1, searchProfile.childrenStatus.length-1).split(", ");
				$target = $('#userChildren');
				for(i=0;i<childStatus.length;++i)
					setItem($target, childStatus[i]);	
			}
			if(searchProfile.personalityType){	
			var personTypes = searchProfile.personalityType.substring(1, searchProfile.personalityType.length-1).split(", ");
				$target = $('#personalities');
				for(i=0;i<personTypes.length;++i)
					setItem($target, personTypes[i]);	
			}	
			if(searchProfile.countries){
			var countries = searchProfile.countries.substring(1, searchProfile.countries.length-1).split(", ");
			$target = $('#userCountryId');
			for(i=0;i<countries.length;++i)
				setItem($target, countries[i]);	
			}
					
		}
	function setItem($select, value) {

            if ($select.find("option[value='" + value + "']").length) {             
                	selection = $select.val();
                    selectedValues = [];
                    if(selection != null){
                     	selectedValues = selection;
                     }
                    selectedValues.push(value)
                    $select.val(selectedValues);
                
            }  
            $select.trigger('change');
        }

	return{
		init: init,
		populateSearchProfile: populateSearchProfile
	}
	})();


/**
 * 
 */