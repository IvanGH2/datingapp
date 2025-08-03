package ngd.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import ngd.utility.MessageUtil;

@ControllerAdvice
public class GenericExceptionHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericExceptionHandler.class);
	
	@ExceptionHandler(Throwable.class)
	public ModelAndView defaultErrorHandler(HttpServletRequest request, HttpServletResponse response, Exception e)
			throws Exception {
		LOGGER.error("Failed url(gen): " + request.getRequestURL() + " Exception: ", e.getMessage());
		if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
			throw e;
		}
		
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return fillExcModelAndView(MessageUtil.message("exception.default"));
	}

	 @ExceptionHandler(AccessDeniedException.class)
	public ModelAndView accessDeniedException(HttpServletRequest req, HttpServletResponse resp, AccessDeniedException e)
			throws Exception {
		LOGGER.error("Failed URL(ade): " + req.getRequestURL() + " Exception: ", e.getMessage());

		resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
		return fillExcModelAndView(MessageUtil.message("url.forbidden"));
	}
	private ModelAndView fillExcModelAndView(String msg) {
		ModelAndView mv = new ModelAndView();
		
		mv.addObject("msg", msg);
		mv.addObject("excHandler", true);
		mv.setViewName("error");
		return mv;
	}
}
