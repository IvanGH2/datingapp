var fileUploadModule = (function () {
	var jqXHRArray = new Array();
	
	/*var _counter = 0;

	function getFileId() {
		return 'ufi' + _counter++;
	}*/

	function resetUpload() {
		if (jqXHRArray.length == 0) {
			commonModule.showAlertInfo('Arhiviranje mora biti u tijeku za odustajanje.');
		}

		for (var i = 0; i < jqXHRArray.length; i++) {
			jqXHRArray[i].abort();
		}

		jqXHRArray = new Array();
	}
	function uploadPhotos(filePond){

	 params = [ { name: '_csrf', 
      			  value: 'b411beab-83a0-499a-9121-dcd6afdafb69' }, { name: 'echo', 
      			  value: 10 } ];
	
    var formData = new FormData();
    formData.append( '_csrf', 
      			  $('#csrf').val());
    formData.append( 'echo', 
      			  10  );			   
    pondFiles = filePond.getFiles();
    files = [];
        for (i = 0; i < pondFiles.length; i++) {
            formData.append( 'file', pondFiles[i].file  );	
        }	
	}
	function init() {
		
		$('#personalities').select2();
		FilePond.registerPlugin(
  				FilePondPluginFileValidateType,
  				FilePondPluginImageExifOrientation,
  				FilePondPluginImagePreview
			);
		var filePond = FilePond.create( $('#photoUploaderIn') );
        filePond.setOptions({
    		maxFiles: 3,
    		required: true,
    		allowMultiple: true,
    		allowProcess: false,
    		instantUpload: false
		});
  
        $('#photoUploaderIn').hide();
       	$('#phu_wrapper').prepend(filePond.element);	
       
       	$('#saveUserProfileBtn').on('click', function(e){
       		e.preventDefault();
       		$('#userProfileForm').submit();
       	});
		$('#userProfileForm').on('submit', function(e){
			e.preventDefault();	
				
			formData = new FormData($(this)[0]);
			personalitiesValues = $('#personalities').val();
			if(personalitiesValues){
				for(i=0; i<personalitiesValues.length;++i){
					formData.append( 'personalities', personalitiesValues[i]  );
				}
			}
			//add photos
			pondFiles = filePond.getFiles();
			if (pondFiles){
        		for (i = 0; i < pondFiles.length; i++) {
        			file = pondFiles[i].file;
        			formData.append(  'file', file );	
            	
        		}
        	}
        	//add photoLinks
       		$('div#imgLinks_wrapper input[type="text"]').each(function(){		
				var link = $(this).val();
				if (link.length > 0) {
					formData.append( 'photoLinks', link  );
				} 
			});
			//$("#ajax_loader").show();
			$.ajax({
                url: contextRoot + 'saveUserProfile',
                type: 'POST',
                data: formData,
                dataType: 'json',
                contentType: false,
                cache: false,
                processData: false,
                success: function (response) {
                	if(response.status == 'SUCCESS')
                    	commonModule.showAlertInfo(response.result, '.inner-message', true);
                    else
                    	commonModule.showAlertInfoFail(response.result, '.inner-message', true);
					
                },
                error: function (response) {
                    commonModule.showAlertInfoFail('Unknown error', '.inner-message', true);
                }
            }
        );
			
		});
		
       	//addPhotoLinksBtn
       	$("#addPhotoLinksBtn").on('click', function (e) {
        	e.preventDefault();
        	
        	if( $('#imgLinks_wrapper input').length >= 5 ) return;	//max 5 links
        	
        	var photoLink = '<p class="dummy"><input class="imgLinks" type="text"> <button title="Remove" class="imgLinksRemoveBtn" type="button"><i class="glyphicon glyphicon-remove"></i></button></p>';
        	
        	$('#imgLinks_wrapper').append(photoLink);
        	$('.imgLinksRemoveBtn').on('click', function(e){
        		e.preventDefault();
        		$(this).closest('p').remove();      		
        		$(this).prev('input').remove();	
        		$(this).remove();
        	});
        });
       	$("#photoUploadBtn").on('click', function (e) {
        	e.preventDefault();        	
        	uploadPhotos(filePond);
        });
	}

	return {
		resetUpload: resetUpload,
		init: init
	}
})();/**
 * 
 */