package ngd.controller;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ngd.controller.response.JsonResponse;
import ngd.mail.MailSenderService;
import ngd.model.ActivationLink;
import ngd.model.IActivationLinkRepository;
import ngd.model.IUserProfileRepository;
import ngd.model.TargetProfile;
import ngd.model.User;
import ngd.model.UserProfile;
import ngd.model.service.RegisterUserForm;
import ngd.model.service.UserService;
import ngd.utility.MessageUtil;

@Controller
public class Register {
	
	
	@Autowired
	private UserService userService;
	@Autowired
	private IUserProfileRepository userProfileRepository;
	
	@Autowired
	private IActivationLinkRepository activeLinkRepository;
	
	@Autowired 
	private MailSenderService mailSender;
	
	@ModelAttribute("regUserForm")
	public RegisterUserForm regUserForm() {
		return new RegisterUserForm();
	}
	
@PostMapping("/processRegisterInfo")
@Transactional(rollbackOn=Exception.class)
public @ResponseBody JsonResponse processRegisterInfo(@ModelAttribute("regUserForm") RegisterUserForm registerUserForm, HttpServletRequest request) throws Exception {		
	System.out.println("register entered");
	JsonResponse response = new JsonResponse();
	response.setStatus(JsonResponse.ResponseStatus.FAIL);
	
	try {
		if(userService.doesExistsUserWithSameUsername(registerUserForm.getUsername())) {
		
			throw new Exception(MessageUtil.message("register.newuser.username_already_exists"));
			
		}
		if(userService.doesExistsUserWithSameEmail(registerUserForm.getEmail())) {
	
			throw new Exception(MessageUtil.message("register.newuser.email_already_exists"));
		}
		/*if(userService.doesExistsUserWithSameIp(request.getRemoteAddr())) {
			
			throw new Exception(MessageUtil.message("register.newuser.ip_already_exists"));
		}*/
		
		final User user = userService.addUser(registerUserForm, request.getRemoteAddr());
		System.out.println("user profile about to be created");
		System.out.println(registerUserForm);
		final UserProfile userProfile = UserProfile.builder()
		.userId(user.getId())
		.city(registerUserForm.getResidence())
		.country(registerUserForm.getCountry())
		.relStatus(registerUserForm.getRelStatus().getValue())
		.profileVisibleTo(registerUserForm.getProfileVisibleTo().getValue())
		.photosAvailable(false)
		.dateCreated(user.getDateCreated())
		.dateChanged(user.getDateCreated())	
		.build();
		if(userProfile != null ) {
			System.out.println("user profile about to be saved");
			userProfileRepository.save(userProfile);
			System.out.println("user profile created");
		}else
			System.out.println("user profile not created");
		
		final TargetProfile targetProfile = TargetProfile.builder()
				.gender(registerUserForm.getTargetGender())
				.dateCreated(user.getDateCreated())
				.dateChanged(user.getDateCreated())
				.build();
				
		ActivationLink aLink = activeLinkRepository.save(ActivationLink.builder()
				.userId(user.getId())
				.timeCreated(Timestamp.from(Instant.now()))
				.timeExpires(new Timestamp(System.currentTimeMillis()+(1000*3600)))
				.linkConsumed(false)
				.activationLink(UUID.randomUUID().toString())
				.build());
		
		System.out.println("UUID has been created " + aLink.getActivationLink());
		response.setStatus(JsonResponse.ResponseStatus.SUCCESS);
		response.setResult(MessageUtil.message("register.success.response.msg", user.getEmail()));
		
		final String msg = MessageUtil.message("register.mail.activation", "http://localhost:8080/register/userActivate/" + aLink.getActivationLink());
		mailSender.sendMail(msg, user.getEmail(), "activation link");

	} catch (final Exception e) {
		response.setResult(e.getMessage());
		System.out.println(e);
	}
	
	return response;
	}
}
