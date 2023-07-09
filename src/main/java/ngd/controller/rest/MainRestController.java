package ngd.controller.rest;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import ngd.controller.CommonObject;
import ngd.controller.SessionAttr;
import ngd.controller.UserProfileHelper;
import ngd.controller.display.MemberDisplay;
import ngd.controller.display.UserMessageDisplay;
import ngd.controller.display.UserMessageWrapper;
import ngd.controller.photos.PhotoRetriever;
import ngd.controller.response.JsonResponse;
import ngd.controller.response.JsonResponse.ResponseStatus;
import ngd.controller.rest.model.TargetProfileForm;
import ngd.controller.rest.model.UserProfileForm;
import ngd.mail.MailSenderService;
import ngd.model.ActivationLink;
import ngd.model.BlockedUser;
import ngd.model.IActivationLinkRepository;
import ngd.model.IBlockedUserRepository;
import ngd.model.IUserMessagesLastRepository;
import ngd.model.IUserMessagesRepository;
import ngd.model.IUserProfilePhotosRepository;
import ngd.model.IUserProfileRepository;
import ngd.model.IUserProfileViewsRepository;
import ngd.model.IUserRepository;
import ngd.model.IUserTargetProfileRepository;
import ngd.model.TargetProfile;
import ngd.model.User;
import ngd.model.UserMessage;
import ngd.model.UserMsgLastActivity;
import ngd.model.UserProfile;
import ngd.model.UserProfilePhotos;
import ngd.model.UserProfileViews;
import ngd.model.nativequery.NativeQuery;
import ngd.model.nativequery.model.TargetUserBasicInfo;
import ngd.model.status.EDirection;
import ngd.model.status.EUserProfile.EPersonality;
import ngd.model.status.EUserRole;
import ngd.utility.CurrentUserUtility;
import ngd.utility.MessageUtil;
import ngd.utility.RandomString;
import ngd.utility.StringProcessor;

@RestController
public class MainRestController {
	
	private static final Logger logger = LoggerFactory.getLogger(MainRestController.class);
	
	private ExecutorService sseService = Executors.newCachedThreadPool();	 

	@Autowired
	private IUserRepository userRepository;
	
	@Autowired
	private IBlockedUserRepository blockedUserRepository;
	
	@Autowired
	private IActivationLinkRepository activeLinkRepository;
	
	@Autowired
	private IUserProfileRepository iUserProfileRepository;
	
	@Autowired
	private IUserTargetProfileRepository iUserTargetProfileRepository;
	
	@Autowired
	private IUserProfilePhotosRepository iUserProfilePhotosRepository;
	
	@Autowired
	private NativeQuery nativeQuery;
	
	@Autowired
	private PhotoRetriever photoRetriever;
	
	@Autowired
	private IUserMessagesRepository userMessageRepository;
	
	@Autowired
	private IUserMessagesLastRepository userMessageLastRepository;
	
	@Autowired
	private IUserProfileViewsRepository userProfileViewsRepository;
	
	@Autowired 
	private MailSenderService mailSender;
	

	@GetMapping("/retrieveUser")
	public JsonResponse retrieveUser(@PathVariable String username) {
		
		JsonResponse response = new JsonResponse();
		response.setStatus(JsonResponse.ResponseStatus.FAIL);
		
		  try {
			User user = userRepository.findOneByUsername(username);
			if(user != null) {
				response.setResult(user);
				response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
			}
		  }catch (Exception ex) {
			  logger.error(ex.getMessage());
			 // response.setResult(user);		  
			  }
		
		return response;
	}
	private void populateLink(ActivationLink aLink) {
		aLink.setLinkConsumed(false);
		aLink.setTimeExpires(new Timestamp(System.currentTimeMillis()+(1000*3600)));
		aLink.setActivationLink(UUID.randomUUID().toString());
	}
	private class SseEvent {
		
		public String evType;
		
		public Integer evNum;
		
		public SseEvent(String type, Integer num) {
			this.evNum = num;
			this.evType = type;
		}
	}
	private synchronized void sendSseMsg(final String target, final String evType, final Integer num) {
		
		final SseEmitter clientEmitter = CommonObject.sseUsers.get(target);
		
		if(clientEmitter == null) {
			System.out.println("getsse user msg emitter " + target);
			return;
		}
		sseService.execute(() -> {
            try {
            	if(num >0) {
            		System.out.println("getsse user msg target " + target);
            		clientEmitter.send(new SseEvent(evType, num));	
            	}            	            
            } catch (Exception ex) {
            	System.out.println("getsse endpoint exception " + target);
            	clientEmitter.completeWithError(ex);
            }
        });
	}
	@GetMapping("/getSse")
	public  SseEmitter getSse(HttpServletResponse response){
		
		final String currUser = CurrentUserUtility.getCurrentUser().getUsername();
		System.out.println("getsse endpoint user " + currUser);
		final SseEmitter emitter = CommonObject.sseUsers.get(currUser);
		
		emitter.onTimeout( () -> { emitter.complete(); } );
		emitter.onCompletion( () -> { 	
			if(CommonObject.sseUsers.containsKey(currUser))
				CommonObject.sseUsers.put(currUser, new SseEmitter());
			System.out.println("getsse endpoint completion user " + currUser);
			
		});
		emitter.onError( (ex) -> { 
			//CommonObject.onlineUsers.put(currUser, new SseEmitter());
			System.out.println("getsse endpoint onErr user " + currUser);
			emitter.completeWithError(ex); 
			}); 
		
		//currEmitter.complete();
		System.out.println("getsse endpoint return");
		return emitter;
	}
	@GetMapping("/deleteUserImage")
	public JsonResponse deleteUserImage(@RequestParam String imageUrl) {
		JsonResponse response = new JsonResponse();
		response.setStatus(JsonResponse.ResponseStatus.FAIL);
		boolean found = false;
		System.out.println("inside delete image");
		if(imageUrl != null && !imageUrl.isEmpty()) {
			
			try {
				final Integer userId = CurrentUserUtility.getCurrentUser().getUserId();
				//lets first look in the db
				final UserProfilePhotos userPhoto = iUserProfilePhotosRepository.findOneByUserIdAndLinkToPhoto(userId, imageUrl);
				if(userPhoto != null) {
					
					iUserProfilePhotosRepository.delete(userPhoto);
					found = true;
				}else {//lets check the file system
					
					final String imgName = imageUrl.substring(imageUrl.lastIndexOf('/'));
					System.out.println(imgName);
					found = photoRetriever.deleteUserPhoto(userId, imgName);
					
				}
				if(found) {
					response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
					response.setResult(MessageUtil.message("photos.delete.success"));
				}else
					response.setResult(MessageUtil.message("photos.delete.fail"));
			}catch(Exception e) {
				
				response.setResult(e.getMessage());
				 logger.error(e.getMessage());
			}
		}
		
		return response;
	}
	@GetMapping("/register/recreateActivationLink")
	public JsonResponse createAndSendActivationLink(@RequestParam(name="aLink") Integer id) {
		JsonResponse response = new JsonResponse();
		response.setStatus(JsonResponse.ResponseStatus.FAIL);
		try {
			ActivationLink aLink = activeLinkRepository.findOneById(id);
			
			if(aLink != null) {
				
				populateLink(aLink);
				activeLinkRepository.save(aLink);
				response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
				response.setResult(MessageUtil.message("register.activation.success.response.msg"));
			}
			
		}catch(Exception e) {
			
			response.setResult(e.getMessage());
			logger.error(e.getMessage());
		}
		return response;
	}
	@PostMapping("/register/recreateActivationLink")
	public @ResponseBody JsonResponse createAndSendActivationLink(@RequestParam(name="userEmail") String userEmail, HttpServletRequest request) {
		
		JsonResponse response = new JsonResponse();
		response.setStatus(JsonResponse.ResponseStatus.FAIL);
		System.out.println("post recreate link");
		try {		
			User user =userRepository.findOneByEmail(userEmail);
			
			if(user != null) {
				
				ActivationLink aLink = activeLinkRepository.findOneByUserId(user.getId());
				
				if(aLink != null) {					
					populateLink(aLink);
					activeLinkRepository.save(aLink);
					
				}else {
					//this should never happen but we can recreate the link all the same
					aLink = activeLinkRepository.save(ActivationLink.builder().userId(user.getId())
							.timeCreated(Timestamp.from(Instant.now()))
							.timeExpires(new Timestamp(System.currentTimeMillis()+(1000*3600)))
							.linkConsumed(false)
							.activationLink(UUID.randomUUID().toString())
							.build());
					
				}
				System.out.println("UUID has been created " + aLink.getActivationLink());

				response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
				response.setResult(MessageUtil.message("register.activation.recreate.msg", userEmail));
				final String msg = MessageUtil.message("register.mail.activation", "http://localhost:8080/register/userActivate/" + aLink.getActivationLink());
				mailSender.sendMail(msg, user.getEmail(), "activation link");
				
			}else {
				//user not found having the supplied email
				response.setResult(MessageUtil.message("register.activation.fail.noemail.response.msg"));
			
			}
			
		}catch(Exception e) {
			
			response.setResult(e.getMessage());
			logger.error(e.getMessage());
		}
		 
		return response;
		
	}
	@PostMapping("/changePassword")
	public JsonResponse changePassword(@RequestParam(name="newPsw", required=true) String newPsw) {
		
		JsonResponse response = new JsonResponse();
		response.setStatus(JsonResponse.ResponseStatus.FAIL);
		final User user = userRepository.findOneById(CurrentUserUtility.getCurrentUser().getUserId());		
		try {	
			//make sure the old and new passwords are different
			if(new BCryptPasswordEncoder().matches(newPsw, user.getPassword())) {
				response.setResult(MessageUtil.message("userProfile.password.change.samepsw"));
				return response;
			}
			user.setPassword(new BCryptPasswordEncoder().encode(newPsw));
			userRepository.save(user);
			response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
			response.setResult(MessageUtil.message("userProfile.password.change.success"));
		}catch(Exception e) {
			response.setResult(MessageUtil.message("userProfile.password.change.failed"));
			logger.error(e.getMessage());
			
		}
		return response;
	}
	 
	@PostMapping("/register/retrieveCredentials")
	public @ResponseBody JsonResponse retrievePassword(@RequestParam(name="userEmail") String userEmail, @RequestParam(name="credentials") final Integer infoToSend) {
		final int pswLength = 8;
		JsonResponse response = new JsonResponse();
		response.setStatus(JsonResponse.ResponseStatus.FAIL);
		String retrievedUsername = null, genPsw = null;
		System.out.println("retrieve credentials");
		try {		
			final User user = userRepository.findOneByEmail(userEmail);
	
			if(user != null) {
				
				RandomString randomSeq = new RandomString(pswLength);
				
				switch(infoToSend) {		
				
					case 0:
						genPsw = randomSeq.nextString();
						break;
					case 1:
						retrievedUsername = user.getUsername();
						break;
					case 2:
						genPsw = randomSeq.nextString();
						retrievedUsername = user.getUsername();
						break;					
				
				}
				if(genPsw != null ) {
					
					System.out.println(genPsw);
					logger.info("generated password for user " + userEmail + " is " + genPsw);
					user.setPassword(new BCryptPasswordEncoder().encode(genPsw));
					//user.setActive(true);
					userRepository.save(user);
					final String msg = infoToSend == 0 ? MessageUtil.message("login.password.retrieved", genPsw)
													   : MessageUtil.message("login.both.retrieved", retrievedUsername, genPsw);
					mailSender.sendMail(msg, userEmail,  MessageUtil.message("login.retrieve.mail.subject"));
				}else if(retrievedUsername != null) {
					logger.info("retrieved username for user " + userEmail);					
					mailSender.sendMail(MessageUtil.message("login.username.retrieved", retrievedUsername), 
							userEmail, MessageUtil.message("login.retrieve.mail.subject"));
				}
					//send an email containing the retrieved credentials
					response.setResult(MessageUtil.message("lostpsw.response.success.msg", userEmail));
					response.setStatus(JsonResponse.ResponseStatus.SUCCESS);							
			}else {
				//user not found having the supplied email
					response.setResult(MessageUtil.message("register.activation.fail.noemail.response.msg"));	
			}
			
		}catch(Exception e) {
			response.setResult(MessageUtil.message("exception.default"));
			logger.error(e.getMessage());
		}
		return response;
		
	}
	
	@PostMapping("/saveUserProfile/target")
	@Transactional(rollbackOn=Exception.class)
	public @ResponseBody JsonResponse saveTargetUserProfile(@ModelAttribute("targetProfileForm") TargetProfileForm targetProfileForm) {
		
		JsonResponse response = new JsonResponse();
		response.setStatus(JsonResponse.ResponseStatus.FAIL);
		
		boolean profileExists = false;
		try {
			
			Timestamp now = Timestamp.from(Instant.now());
			boolean update = false;
				
			TargetProfile targetProfile = iUserTargetProfileRepository.findOneByUserId(CurrentUserUtility.getCurrentUser().getUserId());
			
			if(targetProfile == null) {
				targetProfile = new TargetProfile();
			}else {
				profileExists = true;
			}
			
			if( targetProfileForm.getCountries() != null ) {
				targetProfile.setCountries(targetProfileForm.getCountries().toString());
				targetProfile.setCountryInclude(targetProfileForm.getCountriesInclude());
				update = true;
			}
			
			if( targetProfileForm.getAgeFrom() != null ) {
				targetProfile.setAgeFrom(targetProfileForm.getAgeFrom());
				update = true;
			}
			
			if( targetProfileForm.getAgeTo() != null ) {
				targetProfile.setAgeTo(targetProfileForm.getAgeTo());
				update = true;
			}
			
			if(targetProfileForm.getEduLevels() != null) {
				final List<String> eduList = new ArrayList<>(targetProfileForm.getEduLevels().size());
				targetProfileForm.getEduLevels().forEach(v ->  eduList.add(v.getValue().toString()) );
				targetProfile.setEduLevel(eduList.toString());
				targetProfile.setEduLevelInclude(targetProfileForm.getEduLevelsInclude());
				update = true;
			}
			
			if(targetProfileForm.getBodyTypes() != null) {
				final List<String> bodyList = new ArrayList<>(targetProfileForm.getBodyTypes().size());
				targetProfileForm.getBodyTypes().forEach(v -> bodyList.add(v.getValue().toString()));
				targetProfile.setBodyType(bodyList.toString());
				targetProfile.setBodyTypeInclude(targetProfileForm.getBodyTypesInclude());
				update = true;
			}
			
			if(targetProfileForm.getPersonalities() != null && !targetProfileForm.getPersonalities().isEmpty()) {
				final List<String> personalitiesList = new ArrayList<>(targetProfileForm.getPersonalities().size());
				targetProfileForm.getPersonalities().forEach(v -> personalitiesList.add(v.getValue().toString()));
				targetProfile.setPersonalityType(personalitiesList.toString());
				targetProfile.setPersonalityTypeInclude(targetProfileForm.getPersonalitiesInclude());
				update = true;
			}
			
			if(targetProfileForm.getChildrenStatuses() != null) {
				final List<String> childrenList = new ArrayList<>(targetProfileForm.getChildrenStatuses().size());
				targetProfileForm.getChildrenStatuses().forEach(v -> childrenList.add(v.getValue().toString()));
				targetProfile.setChildrenStatus(childrenList.toString());
				targetProfile.setChildrenStatusInclude(targetProfileForm.getChildrenStatusInclude());
				update = true;
			}
			
			if(targetProfileForm.getEmploymentStatuses() != null) {
				final List<String> employmentList = new ArrayList<>(targetProfileForm.getEmploymentStatuses().size());
				targetProfileForm.getEmploymentStatuses().forEach(v -> employmentList.add(v.getValue().toString()));
				targetProfile.setEmploymentStatus(employmentList.toString());
				targetProfile.setEmploymentStatusInclude(targetProfileForm.getEmploymentStatusInclude());
				update = true;
			}
			
			if(targetProfileForm.getRelStatuses() != null) {
				final List<String> relList = new ArrayList<>(targetProfileForm.getRelStatuses().size());
				targetProfileForm.getRelStatuses().forEach(v -> relList.add(v.getValue().toString()));
				targetProfile.setRelStatus(relList.toString());
				targetProfile.setRelStatusInclude(targetProfileForm.getRelStatusInclude());
				update = true;
			}
			
			if(update) {
				targetProfile.setUserId(CurrentUserUtility.getCurrentUser().getUserId());
				targetProfile.setDateChanged(now);
				if(!profileExists)targetProfile.setDateCreated(now);
				
				iUserTargetProfileRepository.save(targetProfile);
			}
			
			
			response.setResult(MessageUtil.message("userProfile.target.save.success"));
			response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
		}catch(Exception e) {
			
			response.setResult(MessageUtil.message("userProfile.target.save.fail"));
			response.setStatus(JsonResponse.ResponseStatus.FAIL);
			logger.error(e.getMessage());
		}
		return response;
	}
	
	@PostMapping("/saveUserProfile")
	@Transactional(rollbackOn=Exception.class)
	public @ResponseBody JsonResponse saveUserProfile(@ModelAttribute("userProfileForm") UserProfileForm userProfileForm, MultipartHttpServletRequest request) {
		
		JsonResponse response = new JsonResponse();

		boolean profileExists = false;
		try {
			
			Timestamp now = Timestamp.from(Instant.now());
			
			UserProfile userProfile = iUserProfileRepository.findOneByUserId(CurrentUserUtility.getCurrentUser().getUserId());
			boolean update = false;
			if(userProfile == null)
				userProfile = new UserProfile();
			else
				profileExists = true;
			
			if( userProfileForm.getCountry() != null && !userProfileForm.getCountry().equals("n/a")) {
				
				userProfile.setCountry(userProfileForm.getCountry());
				update = true;
			}
			
			
			if( !userProfileForm.getResidence().isEmpty() ) {
				userProfile.setCity(userProfileForm.getResidence());
				update = true;
			}
			
			if( userProfileForm.getEBodyType() != null ) {
				userProfile.setBodyType(userProfileForm.getEBodyType().getValue());
				update = true;
			}
			
			if( userProfileForm.getEEduLevel() != null ) {
				userProfile.setEduLevel(userProfileForm.getEEduLevel().getValue());
				update = true;
			}
			if( userProfileForm.getERelStatus() != null ) {
				userProfile.setRelStatus(userProfileForm.getERelStatus().getValue());
				update = true;
			}
			
			if( userProfileForm.getHobbies() != null && userProfileForm.getHobbies().length() > 0) {
				userProfile.setHobbies(userProfileForm.getHobbies());
				update = true;
			}
			
			if( userProfileForm.getPersonalities() != null && !userProfileForm.getPersonalities().isEmpty()) {
				List<String> perList = new ArrayList<>(userProfileForm.getPersonalities().size());
				
				userProfileForm.getPersonalities().forEach(v -> perList.add(v.getValue().toString()));
				userProfile.setPersonalityType(perList.toString());
				update = true;
			}
			if( userProfileForm.getMessage() != null  && userProfileForm.getMessage().length() > 0 ) {
				userProfile.setUserMsg(userProfileForm.getMessage());
				update = true;
			}
			if(userProfileForm.getEEmploymentStatus() != null) {
				userProfile.setEmpStatus(userProfileForm.getEEmploymentStatus().getValue());
				update = true;
			}
			if( !userProfileForm.getProfession().isEmpty() ) {
				userProfile.setProfession(userProfileForm.getProfession());
				update = true;
			}
			
			final boolean hasFiles = request.getFiles("file") != null ? !request.getFiles("file").isEmpty() : false;
			if( hasFiles ) {
				if(photoRetriever.savePhotos(request)) {
					userProfile.setPhotosAvailable(true);
					update = true;
				}else {
					update = false;
					response.setResult(photoRetriever.getPhotoSaveFailedReason());
					response.setStatus(JsonResponse.ResponseStatus.FAIL);
					return response;
				}  
			}
			//saving userProfile
			if(update) {
				userProfile.setUserId(CurrentUserUtility.getCurrentUser().getUserId());
				userProfile.setDateChanged(now);
				if(!profileExists) userProfile.setDateCreated(now);
				
				iUserProfileRepository.save(userProfile);
			}
			
			//creating and then saving userProfilePhotos
			if(userProfileForm.getPhotoLinks() != null) {
				for(final String link : userProfileForm.getPhotoLinks()) {
					final UserProfilePhotos userProfilePhotos = UserProfilePhotos.builder()
					.userId(userProfile.getUserId())
					.dateChanged(now)
					.dateCreated(now)
					.linkToPhoto(link)
					.build();
					
				iUserProfilePhotosRepository.save(userProfilePhotos);
				}
			}
			response.setResult(MessageUtil.message("userProfile.save.success"));
			response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
		}catch(Exception e) {
			
			response.setResult(MessageUtil.message("userProfile.save.fail"));
			response.setStatus(JsonResponse.ResponseStatus.FAIL);
			logger.error(e.getMessage());
		}
		
		return response;
	}
	@GetMapping("/getUserProfile/{username}")
	public @ResponseBody JsonResponse getUserProfileByUsername(@PathVariable("username")String username) {
		
		System.out.println("/getUserProfile/"+username);
		JsonResponse response = new JsonResponse();
		boolean newView = true;			
		try {
			MemberDisplay memberInfo = nativeQuery.getUserProfileByUsername(username);

			if(memberInfo == null)
				throw new UsernameNotFoundException(username);
			
			memberInfo.setPersonalities(StringProcessor.convertToLocalPersonalityNames(memberInfo.getPersonalities()));
	
			User targetUser = userRepository.findOneByUsername(username);
			
			UserProfileViews usrPrViews = userProfileViewsRepository.findOneBySrcUserIdAndDstUserId(CurrentUserUtility.getCurrentUser().getUserId(), targetUser.getId());
			
			if(usrPrViews == null ) {
				usrPrViews = UserProfileViews.builder()
					.srcUserId(CurrentUserUtility.getCurrentUser().getUserId())
					.dstUserId(targetUser.getId())
					.dateViewed(Timestamp.from(Instant.now()))
					.build();
				
			}else {
				usrPrViews.setDateViewed(Timestamp.from(Instant.now()));
				newView = false;
			}
			
			userProfileViewsRepository.save(usrPrViews);	
			final List<Integer> seenList;
			if( newView == true && (seenList = CommonObject.seenViews.get(username)) != null) {
				seenList.add(CurrentUserUtility.getCurrentUser().getUserId());
				sendSseMsg(username, "views", seenList.size());
			}

			response.setResult(memberInfo);
			response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
		}catch(Exception e) {
			response.setStatus(JsonResponse.ResponseStatus.FAIL);
			response.setResult(MessageUtil.message("userProfile.get.fail"));
			logger.error(e.getMessage());
		}

		return response;
	}
	@GetMapping("/getUserProfiles/targetViews")
	public @ResponseBody JsonResponse getUserTargetProfiles(@RequestParam("offset")Integer offset, 
			@RequestParam("numRec")Integer numRec, HttpSession session) throws IOException, IllegalArgumentException, IllegalAccessException {
		
		JsonResponse response = new JsonResponse();
		response.setStatus(JsonResponse.ResponseStatus.FAIL);
		
		/*System.out.println("method: /getUserProfiles/targetViews");
		System.out.println("offset: "+offset);
		System.out.println("numRec: "+numRec);
		System.out.println("dstViewCount: "+ session.getAttribute(SessionAttr.USER_DST_VIEWS_COUNT));*/
	//	final int numRecords = 3;
		final Integer numViews = (Integer) session.getAttribute(SessionAttr.USER_DST_VIEWS_COUNT);
		//final Integer currUserId = CurrentUserUtility.getCurrentUser().getUserId();
		if(offset >= numViews) {
			response.setResult("No more records");
			return response;
		}
		try {
	//	List<UserProfileViews> targetUsers = userProfileViewsRepository.findAllBySrcUserId(currUserId);	
		List<MemberDisplay> memDisplay = UserProfileHelper.getProfileViewsInfo(EDirection.outgoing, nativeQuery, userRepository, 
				iUserProfileRepository, numRec, offset);
		session.setAttribute(SessionAttr.PAGE_USER_VIEWS_COUNT, numRec);
		response.setResult(memDisplay);
		response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
		}catch(Exception e) {
			response.setResult(MessageUtil.message("userProfile.views.get.fail"));
			logger.error(e.getMessage());
		}

		return response;
	}
	@GetMapping("/getUserProfiles/srcViews")
	public @ResponseBody JsonResponse getUserSrcProfiles(@RequestParam("offset")Integer offset, 
			@RequestParam("numRec")Integer numRec, HttpSession session) throws IOException, IllegalArgumentException, IllegalAccessException {
		
		JsonResponse response = new JsonResponse();
		response.setStatus(JsonResponse.ResponseStatus.FAIL);
		
		/*System.out.println("method: /getUserProfiles/srcViews");
		System.out.println("offset: "+offset);
		System.out.println("numRec: "+numRec);
		System.out.println("srcViewCount: "+session.getAttribute(SessionAttr.USER_SRC_VIEWS_COUNT));*/
		//SessionAttr.USER_DST_VIEWS_COUNT
		final Integer numViews = (Integer) session.getAttribute(SessionAttr.USER_SRC_VIEWS_COUNT);
		if(offset >= numViews) {
			response.setResult("No more records");
			return response;
		}
		try {
		List<MemberDisplay> memDisplay = UserProfileHelper.getProfileViewsInfo(EDirection.incoming, nativeQuery, userRepository, 
				iUserProfileRepository,  numRec, offset);
		session.setAttribute(SessionAttr.PAGE_USER_VIEWS_COUNT, numRec);
		response.setResult(memDisplay);
		response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
		}catch(Exception e) {
			response.setResult(MessageUtil.message("userProfile.views.get.fail"));
			logger.error(e.getMessage());
		}

		return response;
	}
	@GetMapping("/fetchUserProfiles")	
	public  @ResponseBody JsonResponse  searchUsers(@RequestParam("offset")Integer offset, 
			@RequestParam("numRec")Integer numRec, HttpSession session) throws IllegalArgumentException, IllegalAccessException {
		
		JsonResponse response = new JsonResponse();
		response.setStatus(JsonResponse.ResponseStatus.FAIL);
		
	//	System.out.println("fetch: "+numRec );
		session.setAttribute(SessionAttr.PAGE_USER_SEARCH_COUNT, numRec);
		TargetProfileForm searchProfileForm = (TargetProfileForm)session.getAttribute("searchParams");	
		try {
			List<MemberDisplay> memDisplay = nativeQuery.searchMemberProfiles(searchProfileForm, 
				CurrentUserUtility.getCurrentUser().getUserId(), numRec, offset);
			for(final MemberDisplay memberProfile : memDisplay) {
				memberProfile.setProfileCompleteStatus();
			}
		
			response.setResult(memDisplay);
			response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
		}catch(Exception e) {
			response.setResult(MessageUtil.message("userProfile.views.get.fail"));
			logger.error(e.getMessage());
		}

		return response;
	}
	@GetMapping("/getUserProfiles")
	public @ResponseBody JsonResponse getUserProfiles(@RequestParam("offset")Integer offset, @RequestParam("numRec")Integer numRec)  {
		
		JsonResponse response = new JsonResponse();
		try {
		
			TargetUserBasicInfo targetInfo = nativeQuery.getTargetUsersInfo(CurrentUserUtility.getCurrentUser().getUserId());
			List<MemberDisplay> memDisplay = UserProfileHelper.getMembersInfo(nativeQuery, targetInfo, numRec, offset);
		
		
			response.setResult(memDisplay);
			response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
		}catch(Exception e) {
			response.setResult(MessageUtil.message("userProfile.views.get.fail"));
			response.setStatus(JsonResponse.ResponseStatus.FAIL);
			logger.error(e.getMessage());
		}

		return response;
	}
	@GetMapping("/flagMsg/{msgId}")
	public @ResponseBody JsonResponse toggleMsgFlag(@PathVariable(name="msgId") Integer msgId){
		JsonResponse response = new JsonResponse();
		response.setStatus(JsonResponse.ResponseStatus.FAIL);
		try {
			final UserMessage userMsg = userMessageRepository.findOneById(msgId);
			userMsg.setMsgFlag(!userMsg.getMsgFlag());
			userMessageRepository.save(userMsg);
			response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
			response.setResult(userMsg.getMsgFlag());
			//System.out.println(userMsg.getMsgFlag());
		}catch(Exception e) {
			response.setStatus(JsonResponse.ResponseStatus.FAIL);
			response.setResult(MessageUtil.message("msg.flag.failed"));
			logger.error(e.getMessage());
		}
		return response;
	}
	@PostMapping("/modifyRole/{username}/{role}")
	@PreAuthorize("hasAuthority('admin')")
	public JsonResponse modifyUserRole(@PathVariable String username, @PathVariable Integer role) {
		JsonResponse response = new JsonResponse();
		response.setStatus(JsonResponse.ResponseStatus.FAIL);
		EUserRole userRole = EUserRole.getObject(role);
		try {
			User user = userRepository.findOneByUsername(username);
			if(user != null) {
				user.setUserRole(role);
				userRepository.save(user);		
				response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
				if(userRole == EUserRole.MOD || userRole == EUserRole.ADMIN)
					response.setResult(MessageUtil.message("user.promote.success"));
				else if(userRole == EUserRole.USER)
					response.setResult(MessageUtil.message("user.demote.success"));
				//else
					//response.setResult(MessageUtil.message("user.role.modify.uknown"));
		}
		}catch(Exception e) {
			if(userRole == EUserRole.MOD || userRole == EUserRole.ADMIN)
				response.setResult(MessageUtil.message("user.promote.fail"));
			else if(userRole == EUserRole.USER)
				response.setResult(MessageUtil.message("user.demote.fail"));
			logger.error(e.getMessage());
		}
		return response;
	}
	@PostMapping("/blockUser/{username}")
	@PreAuthorize("hasAuthority('admin')")
	public JsonResponse blockUser(@PathVariable String username) {
		JsonResponse response = new JsonResponse();
		response.setStatus(JsonResponse.ResponseStatus.FAIL);
		try {
		User user = userRepository.findOneByUsername(username);
		if(user != null) {
			user.setActive(false);
			final BlockedUser blkUser = new BlockedUser();
			blkUser.setUserId(user.getId());
			blkUser.setBlockageReason("some reason");
			blkUser.setDateCreated(Timestamp.from(Instant.now()));
			
			userRepository.save(user);
			blockedUserRepository.save(blkUser);
			response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
			response.setResult(MessageUtil.message("user.block.success", user.getUsername()));
		}
		}catch(Exception e) {
			response.setResult(MessageUtil.message("user.block.fail"));
			logger.error(e.getMessage());
		}
		
		return response;
	}
	@PostMapping("/blockUsers")
	@PreAuthorize("hasAuthority('admin')")
	public JsonResponse blockUsers(@RequestParam Integer[] userIds) {
		
		JsonResponse response = new JsonResponse();
		response.setStatus(JsonResponse.ResponseStatus.FAIL);
		
		try {
			if(userIds != null && userIds.length >0) {
		
			final List<User> users = userRepository.findAllByIdIn(userIds);
			final List<BlockedUser> blkUsers = new ArrayList<>(users.size());
			for (final User user : users) {
				
				user.setActive(false);
				final BlockedUser blkUser = new BlockedUser();
				blkUser.setUserId(user.getId());
				blkUser.setBlockageReason("flagged message");
				blkUser.setDateCreated(Timestamp.from(Instant.now()));
				blkUsers.add(blkUser);
			}
			userRepository.saveAll(users);
			blockedUserRepository.saveAll(blkUsers);
			
			//blockedUserRepository.
			response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
			response.setResult(MessageUtil.message("users.block.success"));
			}	
		}catch(Exception e) {
			response.setResult(MessageUtil.message("users.block.fail"));
			logger.error(e.getMessage());
		}
		return response;	
	}
	
	@PostMapping("/updateMsgStatus")
	public JsonResponse updateMsgStatus(@RequestParam ArrayList<Integer> msgIds){
		JsonResponse response = new JsonResponse();
		response.setStatus(JsonResponse.ResponseStatus.FAIL);

		try {
			//System.out.println(msgIds);
			final int updateCount = userMessageRepository.updateMsgViewedStatus(msgIds);
			//System.out.println(updateCount);
			response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
			
			
		}catch(Exception e) {
			
			logger.error(e.getMessage());
		}
		
		return response;
	}
	@PostMapping("/sendMsg/{target}")
	public @ResponseBody JsonResponse sendUserMsg(@PathVariable(name="target") String target, @RequestParam(name="msg")String msg){
		
		JsonResponse response = new JsonResponse();
		response.setStatus(JsonResponse.ResponseStatus.FAIL);

		try{
			final Timestamp now = Timestamp.from(Instant.now());
			UserMessage userMsg = UserMessage.builder()
				.srcUserId(CurrentUserUtility.getCurrentUser().getUserId())
				.dstUserId(userRepository.findOneByUsername(target).getId())
				.dateCreated(now)
				.dateChanged(now)				
				.msgTxt(msg)
				.msgFlag(false)
				.msgViewed(false)
				.build();
		
		//System.out.println(userMsg.getMsgShort());		
		final UserMessage currMsg = userMessageRepository.save(userMsg);
		
		UserMsgLastActivity uMsgLast = userMessageLastRepository.getUserMsgForUserId(currMsg.getSrcUserId(), currMsg.getDstUserId());
		if(uMsgLast != null) {			
			if(uMsgLast.getSrcUserId() != currMsg.getSrcUserId()) {
				userMessageLastRepository.delete(uMsgLast);
				uMsgLast.setSrcUserId(currMsg.getSrcUserId());
				uMsgLast.setDstUserId(currMsg.getDstUserId());
			}
			uMsgLast.setMsgId(currMsg.getId());
			userMessageLastRepository.save(uMsgLast);
			
		}else {			
			UserMsgLastActivity uMsgAct = UserMsgLastActivity.builder()
					.srcUserId(currMsg.getSrcUserId())
					.dstUserId(currMsg.getDstUserId())
					.msgId(currMsg.getId())
					.build();
			userMessageLastRepository.save(uMsgAct);
		}
		
		response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
		response.setResult(MessageUtil.message("member.message.sent"));
		List<Integer> msgList;
		if (  (  msgList = CommonObject.msgSent.get(target)) != null ) {
			msgList.add(currMsg.getId());
			
			sendSseMsg(target, "msg", msgList.size());
		}
		
		}catch(Exception e) {
			response.setStatus(JsonResponse.ResponseStatus.FAIL);
			response.setResult(MessageUtil.message("member.message.sent.fail"));
			//System.out.println(e.getMessage());
			logger.error(e.getMessage());
		}
		
		return response;	
		
	}
	@GetMapping("/targetMessages")
	public @ResponseBody JsonResponse getTargetUserMessages(@RequestParam(name="target", required=true) String target, @RequestParam("offsetMsg")Integer offsetMsg) {
		JsonResponse response = new JsonResponse();
		response.setStatus(ResponseStatus.FAIL);
		User targetUser = userRepository.findOneByUsername(target);
		try {
		List<UserMessage> userMessages	= userMessageRepository.getMessagesForUser(CurrentUserUtility.getCurrentUser().getUserId(), 
				targetUser.getId(), offsetMsg, 3);
		List<UserMessageDisplay> msgDisplay = UserMessageDisplay.from(userMessages);
		
		response.setResult(msgDisplay);
		response.setStatus(ResponseStatus.SUCCESS);
		}catch(Exception e) {
			
			logger.error(e.getMessage());
		}
		return response;
	}
	@GetMapping("/messages")
	public @ResponseBody JsonResponse getUserMessages(@RequestParam("offset")Integer offsetUser, 
			@RequestParam("offsetMsg")Integer offsetMsg, @RequestParam("numRec")Integer numRec, HttpSession session) {
		JsonResponse response = new JsonResponse();
		response.setStatus(ResponseStatus.FAIL);
		final int msgPerUser = 3;//this should be parameterized rather than hardcoded
		try{

			List<Integer> msgUserIds = userMessageLastRepository.getMessageUsers(CurrentUserUtility.getCurrentUser().getUserId(), 
					offsetUser, numRec);
			List<User> users = userRepository.findAllByIdIn(msgUserIds);
			Map<Integer, String> userMap = getMsgUserMap(users);
			List<UserMessageWrapper> msgWrapperList = new ArrayList<>();
			
			for(Integer userId : msgUserIds) {
				
				List<UserMessage> userMessages	= userMessageRepository.getMessagesForUser(CurrentUserUtility.getCurrentUser().getUserId(), 
						userId, offsetMsg, msgPerUser);
				msgWrapperList.add(UserMessageWrapper.from(userMap.get(userId), UserMessageDisplay.from(userMessages)));
			}

			session.setAttribute(SessionAttr.PAGE_MSG_USERS_COUNT, numRec);
			response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
			response.setResult(msgWrapperList);
		}catch(Exception e) {
			
			response.setResult(MessageUtil.message("member.message.retrieve.fail"));
			logger.error(e.getMessage());
		}
		
		return response;
		
	}
	private Map<Integer, String> getMsgUserMap(List<User> users){
		
		Map<Integer, String> userMap = new HashMap<>();
		
		for(User user : users)
			userMap.put(user.getId(), user.getUsername());
		return userMap;
	}
	
	@ModelAttribute("userProfileForm")
	public UserProfileForm userProfileForm() {
		return new UserProfileForm();
	}
	@ModelAttribute("targetProfileForm")
	public TargetProfileForm targetProfileForm() {
		return new TargetProfileForm();
	}
}
