package ngd.utility;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


import ngd.config.UserInfo;

@Component("CurrentUserUtility")
public class CurrentUserUtility {
	private static final Logger LOGGER = LoggerFactory.getLogger(CurrentUserUtility.class);

	public static UserInfo getCurrentUser() {
		Authentication tAuthentication = getAuthentication();
		if (tAuthentication != null) {
			try {
				return (UserInfo) getAuthentication().getPrincipal();
			} catch (Exception e) {
				return null;
			}
		} else {
			LOGGER.info("Greška kod dohvata trenutnog korisnika");
			return null;
		}
	}

	public static Set<GrantedAuthority> getUserRights() {
		Authentication tAuthentication = getAuthentication();
		if (tAuthentication != null) {
			return new HashSet<>(tAuthentication.getAuthorities());
		} else {
			LOGGER.info("Greška kod dohvata prava za trenutnog korisnika");
			return new HashSet<>();
		}
	}

	public static boolean hasRight(final String right) {
		return getUserRights().stream().filter(e -> e.getAuthority().equals(right)).findFirst().isPresent();
	}

	private static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

}
