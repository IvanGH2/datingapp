package ngd.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import ngd.controller.response.JsonResponse.ResponseStatus;
import ngd.model.ActivationLink;
import ngd.model.IActivationLinkRepository;
import ngd.model.IUserRepository;
import ngd.model.User;
import ngd.utility.MessageUtil;

@Controller
public class UserActivate {
	
	@Autowired
	private IActivationLinkRepository activeLinkRepository;
	
	@Autowired
	private IUserRepository userRepository;
		
	@GetMapping("/register/userActivate/{aLink}")
	public  ModelAndView activateUser(@PathVariable(name = "aLink")String aLink, ModelAndView mv) {
		
		mv.addObject("status", ResponseStatus.FAIL);
		mv.setViewName("regResponse");
		
		try {
			ActivationLink activationLinkObj = activeLinkRepository.findOneByActivationLink(aLink);
			
			if(activationLinkObj != null) {				
					final long currentTime = System.currentTimeMillis();
					final long linkExpiresAt = activationLinkObj.getTimeExpires().getTime();
					if( currentTime > linkExpiresAt) {
						mv.addObject("msg", MessageUtil.message("register.activation.timeexpiry"));// 
						mv.addObject("currLink", activationLinkObj.getId());
						return mv;
					}
					activationLinkObj.setLinkConsumed(true);
					activeLinkRepository.save(activationLinkObj);
					
					User currUser = userRepository.findOneById(activationLinkObj.getUserId());
					
					currUser.setActive(true);
					
					userRepository.save(currUser);
					
					mv.addObject("msg", MessageUtil.message("register.activation.success"));	
					mv.addObject("status", ResponseStatus.SUCCESS);
					
			}else {
					mv.addObject("msg", MessageUtil.message("register.activation.linknotfound"));
					mv.addObject("currLink", -1);
					
			}
		}catch(Exception e) {
			
			mv.setViewName("error");
		}
		
		return mv;
	}

}
