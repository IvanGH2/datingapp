package ngd.config;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

public class UserInfo implements UserDetails {
	private static final long serialVersionUID = -8757795539217855822L;

	@Getter
	private final Integer userId;

	@Getter
	private final String email;

	//@Getter
//	private final String fullName;

	private  String password;
	
	private final String username;
	
	private final Set<GrantedAuthority> authorities;
	
	private final boolean accountNonExpired;
	
	private final boolean accountNonLocked;
	
	private final boolean credentialsNonExpired;
	
	private final boolean enabled;

	public UserInfo(Integer userId, String username, String password, String email, 
			Set<GrantedAuthority> authorities) {
		this(userId, username, password, email,  authorities, 
				true, true, true, true);
	}
	

	public UserInfo(Integer userId, String username, String password, String email, 
			Set<GrantedAuthority> authorities, boolean accountNonExpired, boolean accountNonLocked,
			boolean credentialsNonExpired, boolean enabled) {
		super();
		this.password = password;
		this.username = username;
		this.email = email;
	
		this.authorities = authorities;
		this.accountNonExpired = accountNonExpired;
		this.accountNonLocked = accountNonLocked;
		this.credentialsNonExpired = credentialsNonExpired;
		this.enabled = enabled;
		this.userId = userId;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String psw) {
		password = psw;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}