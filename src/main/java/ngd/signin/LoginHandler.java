package ngd.signin;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ngd.controller.CommonObject;
import ngd.utility.CurrentUserUtility;

@Component
public class LoginHandler extends SimpleUrlAuthenticationSuccessHandler {
	
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
    	
    	System.out.println("login success");
    	System.out.println(CommonObject.sseUsers.size());
    	
    	final String username = CurrentUserUtility.getCurrentUser().getUsername();
    	CommonObject.sseUsers.put(username, 	new SseEmitter());
    	CommonObject.msgSent.put(username, 		new ArrayList<>());
    	CommonObject.seenViews.put(username, 	new ArrayList<>());
    	//System.out.println(username);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
