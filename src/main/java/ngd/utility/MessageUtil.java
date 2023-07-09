package ngd.utility;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageUtil {

	private static MessageSource message;

	@Autowired
	public MessageUtil(MessageSource message) {
		MessageUtil.message = message;
	}

	public static String message(String code) {
		return message(code, LocaleContextHolder.getLocale());
	}

	public static String message(String code, Locale locale) {
		return message(code, null, locale);
	}

	public static String message(String code, Object... args) {
		return message(code, args, LocaleContextHolder.getLocale());
	}

	public static String message(String code, Object[] args, Locale locale) {
		
		return message.getMessage(code, args, locale);
	}
}