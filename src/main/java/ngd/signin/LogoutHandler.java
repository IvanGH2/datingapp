package ngd.signin;

import java.sql.Timestamp;
import java.time.Instant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import ngd.controller.CommonObject;
import ngd.model.IUserRepository;
import ngd.model.User;
import ngd.utility.CurrentUserUtility;

public class LogoutHandler implements org.springframework.security.web.authentication.logout.LogoutHandler{
	
	@Autowired
	private IUserRepository userRepository;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		// TODO Auto-generated method stub
		final String username = CurrentUserUtility.getCurrentUser().getUsername();
		//System.out.println(CommonObject.sseUsers.size());
		CommonObject.sseUsers.remove(username);
		CommonObject.msgSent.remove(username);
		CommonObject.seenViews.remove(username);
		
		User cUser = userRepository.findOneByUsername(username);
		cUser.setLastActivity(Timestamp.from(Instant.now()));
		userRepository.save(cUser);
			
	}

}
