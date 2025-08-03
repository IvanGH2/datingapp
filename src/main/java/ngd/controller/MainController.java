package ngd.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ngd.controller.display.MemberDisplay;
import ngd.controller.display.UserPartial;
import ngd.controller.display.UserProfileDisplay;
import ngd.controller.photos.PhotoRetriever;
import ngd.controller.rest.model.TargetProfileForm;
import ngd.model.IBlockedUserRepository;
import ngd.model.IUserMessagesLastRepository;
import ngd.model.IUserMessagesRepository;
import ngd.model.IUserProfileRepository;
import ngd.model.IUserProfileViewsRepository;
import ngd.model.IUserRepository;
import ngd.model.IUserTargetProfileRepository;
import ngd.model.TargetProfile;
import ngd.model.User;
import ngd.model.UserProfile;
import ngd.model.nativequery.NativeQuery;
import ngd.model.nativequery.model.FlaggedMessageInfo;
import ngd.model.nativequery.model.TargetUserBasicInfo;
import ngd.model.status.EUserRole;
import ngd.utility.CurrentUserUtility;
import ngd.utility.DateTimeUtil;
import ngd.utility.MessageUtil;

@Controller
public class MainController {
	
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	//@Value("${ngd.msg.user.count}")
	//private Integer numMsgUserCount;
	
	@Value("${ngd.user.views.count}")
	private Integer numUserViewsCount;
	
	@Value("${ngd.user.search.count}")
	private Integer numUserSearchCount;
	
	
	@Autowired
	private GlobalPropertiesObject globPropObj;
	
	@Autowired
	private IUserProfileRepository iUserProfileRepository;
	
	@Autowired
	private IUserTargetProfileRepository iUserTargetProfileRepository;
	
	@Autowired
	private IUserRepository iUserRepository;
	
	@Autowired
	private IBlockedUserRepository iBlockedUserRepository;
	
	@Autowired
	private IUserMessagesRepository userMessageRepository;
	
	@Autowired
	private NativeQuery nativeQuery;
	
	@Autowired
	private PhotoRetriever photoRetriever;
	
	
	@Autowired
	private IUserMessagesLastRepository userMessagesLastRepository;
	
	@Autowired
	private IUserProfileViewsRepository userProfileViewsRepository;
	
	{
		
		UserProfileHelper.setPhotoRetriever(photoRetriever);
	}
	
	@ModelAttribute("searchProfileForm")
	public TargetProfileForm targetProfileForm() {
		return new TargetProfileForm();
	}
	@GetMapping("/")
	public String index() {
		return "redirect:/letsclick";
	}
	@ModelAttribute("version")
	public String getVersion() {
		return globPropObj.getVersion();
	}
	@GetMapping("/register")
	public ModelAndView register(ModelAndView mv) {
		mv.setViewName("register");
		return mv;
	}
	
	@GetMapping("/letsclick")
	public ModelAndView letsclick(ModelAndView mv, 
			HttpServletRequest request, HttpSession session) {
	
		final String queryStr = request.getQueryString();	
		mv.setViewName("pages/letsclick");
		if(CurrentUserUtility.getCurrentUser() != null /*queryStr != null*/) {
				
					final Integer userId = CurrentUserUtility.getCurrentUser().getUserId();
					final User user = iUserRepository.findOneById(userId);
					final Integer newMsgCount  = userMessageRepository.getNewMessagesCountByUserIdAndDate(userId, user.getLastActivity());
					final Integer newViewCount = userProfileViewsRepository.getNewProfileViewsCountByUserIdAndDate(userId, user.getLastActivity());
					mv.addObject("viewnewcount", MessageUtil.message("login.user.view.count", newViewCount));
					mv.addObject("msgnewcount",  MessageUtil.message("login.user.msg.count", newMsgCount));
					mv.addObject("page", "WELCOME");
					mv.addObject("signedIn", true);
					globPropObj.getVersion();
					session.setAttribute("userSignedIn", true);
					session.setAttribute("currUser", CurrentUserUtility.getCurrentUser().getUsername());
					
					mv.setViewName("pages/main");		
		}else if(queryStr != null && "error".equals(queryStr)) {
					mv.addObject("signedIn", false);
					mv.addObject("errorMsg", MessageUtil.message("login.badcredentials"));
					
					
		
		}else {
			mv.addObject("signedIn", false);	

		}
		return mv;
	}
	@PostMapping("/searchUserProfile")
	public ModelAndView searchUsers(ModelAndView mv, @ModelAttribute("searchProfileForm") TargetProfileForm searchProfileForm,
			@RequestParam("offset")Integer offset, @RequestParam("numRec")Integer numRec, HttpSession session) throws IllegalArgumentException, IllegalAccessException {
		
		Integer userCount ;
		if(MemberDisplay.numUserSearch == 0) {
			MemberDisplay.numUserSearch = this.numUserSearchCount;
			
		}else {
			MemberDisplay.numUserSearch = numRec;
		}
		//System.out.println(this.numUserSearchCount);
		try {
		userCount  = nativeQuery.searchMemberProfilesCount(searchProfileForm, CurrentUserUtility.getCurrentUser().getUserId());
		}catch(Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
		
		List<MemberDisplay> memDisplay;
		try {
			memDisplay = nativeQuery.searchMemberProfiles(searchProfileForm, CurrentUserUtility.getCurrentUser().getUserId(),
				numRec, offset);
		}catch(Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
		for(final MemberDisplay memberProfile : memDisplay) {
			memberProfile.setProfileCompleteStatus();
		}
		Integer numSearchResults = globPropObj.getNumUserSearchCount();
		
		session.setAttribute(SessionAttr.PAGE_USER_SEARCH_COUNT, numSearchResults );
		session.setAttribute("numMatchingUsers", userCount );
		session.setAttribute("searchParams", searchProfileForm);
		mv.addObject("numMatchingUsers", userCount);
		mv.addObject("numUserSearchPage", numSearchResults );
		mv.addObject("page", "USER_MEMBERS");	
		mv.addObject("members", memDisplay);
		mv.addObject("src", "search");
		mv.setViewName("pages/main");
		
		return mv;
	}
	@GetMapping("/recommendMembers")
	public ModelAndView recommendMembers(ModelAndView mv, 
			@RequestParam("offset")Integer offset, @RequestParam("numRec")Integer numRec, 
			HttpSession session) throws IllegalArgumentException, IllegalAccessException, IOException {
		try {
		TargetUserBasicInfo targetInfo = nativeQuery.getTargetUsersInfo(CurrentUserUtility.getCurrentUser().getUserId());
		
			if(targetInfo != null) {
				Integer userCount  = nativeQuery.findMatchingUsersCount(targetInfo);
				session.setAttribute("numMatchingUsers", userCount );			
				List<MemberDisplay> memDisplay = UserProfileHelper.getMembersInfo(nativeQuery, targetInfo, numRec, offset);
			
				mv.addObject("members", memDisplay);
			}
		}catch(Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
		mv.addObject("page", "USER_MEMBERS");
		mv.addObject("src", "members");
		mv.setViewName("pages/main");
		return mv;
	}
	
	@GetMapping("/userProfile")
	public ModelAndView getUserProfile(ModelAndView mv) {
		
	try {
		UserProfile userProfile = iUserProfileRepository.findOneByUserId(CurrentUserUtility.getCurrentUser().getUserId());		
		
		mv.addObject("userProfile", UserProfileDisplay.from(userProfile, 
				photoRetriever.getUserPhotos(userProfile)));
		mv.addObject("page", "USER_PROFILE");
		mv.setViewName("pages/main");
	}catch(Exception e) {
		e.printStackTrace();
		logger.error("Error retrieving user profile :"+ e.toString());
		mv.setViewName("error");
	}
		return mv;
	}
	@GetMapping("/userSearch")
	public ModelAndView getUserSearch(ModelAndView mv, HttpSession session) {
		
		Integer numSearchResults = globPropObj.getNumUserSearchCount();
		
		mv.addObject("numUserSearchPage", numSearchResults );
	try {
		TargetProfile targetProfile = iUserTargetProfileRepository.findOneByUserId(CurrentUserUtility.getCurrentUser().getUserId());
		mv.addObject("page", "USER_SEARCH");									
		mv.addObject("targetProfile", targetProfile);
		mv.setViewName("pages/main");
	}catch(Exception e) {
		logger.error(e.getMessage());
		mv.setViewName("error");
		
	}
		return mv;
	}
	@GetMapping("/profileViewsSrc")
	public ModelAndView getProfileViewsSrc(ModelAndView mv, HttpSession session) {
		Integer viewsNumSrc =0, viewsPerPage=0;
		if(session.getAttribute(SessionAttr.PAGE_USER_VIEWS_COUNT) == null) {
			viewsPerPage = globPropObj.getNumUserViewsCount();
			//System.out.println(viewsPerPage);
		}else {
			viewsPerPage = (Integer) session.getAttribute(SessionAttr.PAGE_USER_VIEWS_COUNT);
		}
		
		try {
			viewsNumSrc = userProfileViewsRepository.getProfileViewsForSourceUser(CurrentUserUtility.getCurrentUser().getUserId());
			session.setAttribute(SessionAttr.USER_SRC_VIEWS_COUNT, viewsNumSrc);
		}catch(Exception e) {
			logger.error(e.getMessage());
			mv.setViewName("error");
			return mv;
		}
		mv.addObject(SessionAttr.USER_CTX_VIEWS_COUNT, viewsNumSrc);
		mv.addObject(SessionAttr.PAGE_USER_VIEWS_COUNT, viewsPerPage);
		mv.addObject("page", "PROFILE_VIEWS_SRC");
		mv.setViewName("pages/main");
		return mv;
	}
	@GetMapping("/userMessages")
	public ModelAndView getUserMessages(ModelAndView mv, HttpSession session) {
		Integer numUsers = null, numUsersPerPage;
		if(session.getAttribute(SessionAttr.PAGE_MSG_USERS_COUNT) == null) {
			numUsersPerPage = globPropObj.getNumMsgUsers();
			//System.out.println(numUsersPerPage);
		}else {
			numUsersPerPage = (Integer) session.getAttribute(SessionAttr.PAGE_MSG_USERS_COUNT);
		}
		
		try {
			numUsers = userMessagesLastRepository.getMessageUsersForUserByUserId(CurrentUserUtility.getCurrentUser().getUserId());
			session.setAttribute(SessionAttr.ALL_USER_MSG_COUNT, numUsers);
		}catch(Exception e) {
			logger.error(e.getMessage());
		
			mv.setViewName("error");
			return mv;
		}
		
		mv.addObject("page", "USER_MESSAGES");
		mv.addObject(SessionAttr.ALL_USER_MSG_COUNT, numUsers);
		mv.addObject(SessionAttr.PAGE_MSG_USERS_COUNT, numUsersPerPage);
		mv.setViewName("pages/main");
		return mv;
	}
	@GetMapping("/profileViewsDst")
	public ModelAndView getProfileViewsDst(ModelAndView mv, HttpSession session) {
		Integer viewsNumDst =0, viewsPerPage = 0;
		if(session.getAttribute(SessionAttr.PAGE_USER_VIEWS_COUNT) == null) {
			viewsPerPage = globPropObj.getNumUserViewsCount();
			//System.out.println(viewsPerPage);
		}else {
			viewsPerPage = (Integer) session.getAttribute(SessionAttr.PAGE_USER_VIEWS_COUNT);
		}
		
		try {
			viewsNumDst = userProfileViewsRepository.getProfileViewsForTargetUser(CurrentUserUtility.getCurrentUser().getUserId());
			session.setAttribute(SessionAttr.USER_DST_VIEWS_COUNT, viewsNumDst);
		}catch(Exception e) {
				logger.error(e.getMessage());
				mv.setViewName("error");
				return mv;
			}
		mv.addObject(SessionAttr.USER_CTX_VIEWS_COUNT, viewsNumDst);
		mv.addObject(SessionAttr.PAGE_USER_VIEWS_COUNT, viewsPerPage);
		mv.addObject("page", "PROFILE_VIEWS_DST");
		mv.setViewName("pages/main");
		return mv;
	}
	@SuppressWarnings("unused")
	@GetMapping("/flaggedMessages")
	@PreAuthorize("hasAnyAuthority('admin','mod')")
	public ModelAndView getFlaggedMessages(ModelAndView mv, HttpServletRequest request, @RequestParam("offset")Integer offset, 
			@RequestParam("numRec")Integer numRec) {
		try {
		final List<FlaggedMessageInfo> fMsgInfo = nativeQuery.getFlaggedMessages(numRec, offset);
		mv.addObject("flaggedMessages", fMsgInfo);
		mv.addObject("page", "FLAGGED_MESSAGES");
		mv.setViewName("pages/main");
		}catch(Exception e) {
			logger.error(e.getMessage());
			mv.setViewName("error");
		}
		return mv;
		
	}
	@GetMapping("/manageUsers")
	@PreAuthorize("hasAuthority('admin')")
	public ModelAndView manageUsers(ModelAndView mv) {
		
		mv.addObject("userRetrieved", false);
		mv.addObject("page", "MANAGE_USER");
		mv.setViewName("pages/main");
		
		return mv;
	}
	@GetMapping("/manageUsers/{username}")
	@PreAuthorize("hasAuthority('admin')")
	public ModelAndView manageUser(ModelAndView mv, @PathVariable String username) {
		
		mv.addObject("userRetrieved", false);

		if(username != null) {
			mv.addObject("userProvided", true);
			final User user = iUserRepository.findOneByUsername(username);
			if(user != null) {
				UserPartial userPartial = new UserPartial();
				userPartial.username = user.getUsername();
				userPartial.since = DateTimeUtil.formatDate(user.getDateCreated(), "dd-MM-yyyy");
				userPartial.lastActivity = DateTimeUtil.formatDate(user.getLastActivity(), "dd-MM-yyyy");
				userPartial.role = getRoleDesc(EUserRole.getObject(user.getUserRole()));
				userPartial.active = user.getActive() ? "active" : "not active";	
				userPartial.blocked = iBlockedUserRepository.findOneByUserId(user.getId()) != null ? true : false;
				mv.addObject("user", userPartial);
				mv.addObject("userRetrieved", true);
				
			}else {
				mv.addObject("msg", MessageUtil.message("user.notexist", username));
			}
		}
		mv.addObject("page", "MANAGE_USER");
		mv.setViewName("pages/main");
		
		
		return mv;
	}
	private String getRoleDesc(EUserRole role) {
			
		switch(role) {
			case USER:
				return "User";
			case MOD:
				return "Mod";
			case ADMIN:
				return "Admin";
			default:
				return "Unknown";

		}
	}
	@GetMapping("/retrieveMessages")
	public ModelAndView getMessages(ModelAndView mv, HttpServletRequest request, HttpSession session) {
		
		Integer numUsers, numUsersPerPage;
		
		if(session.getAttribute(SessionAttr.PAGE_MSG_USERS_COUNT) == null) {
			numUsersPerPage = globPropObj.getNumMsgUsers();
			System.out.println(numUsersPerPage);
		}else
			numUsersPerPage = (Integer) session.getAttribute(SessionAttr.PAGE_MSG_USERS_COUNT);
		
		if(session.getAttribute(SessionAttr.ALL_USER_MSG_COUNT) != null) {
			numUsers = (Integer)session.getAttribute(SessionAttr.ALL_USER_MSG_COUNT);
		}else{
			try {
			numUsers = userMessagesLastRepository.getMessageUsersForUserByUserId(CurrentUserUtility.getCurrentUser().getUserId());
			session.setAttribute(SessionAttr.ALL_USER_MSG_COUNT, numUsers);
			}catch(Exception e) {
				logger.error(e.getMessage());
				mv.setViewName("error");
				return mv;
			}
		}
		
		mv.addObject("page", "USER_MESSAGES");
		mv.addObject(SessionAttr.ALL_USER_MSG_COUNT, numUsers);
		mv.addObject(SessionAttr.PAGE_MSG_USERS_COUNT, numUsersPerPage);
		mv.setViewName("pages/main");
		return mv;
		
	}
	@GetMapping("/messagesSse")
	public String getMessages(ModelAndView mv) {
		
		CommonObject.msgSent.get(CurrentUserUtility.getCurrentUser().getUsername()).clear();
		
		return "redirect:retrieveMessages";
		
	}
}
