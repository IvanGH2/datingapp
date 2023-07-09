package ngd.config;


import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ngd.model.IRoleRepository;
import ngd.model.IUserRepository;
import ngd.model.Role;
import ngd.model.User;

@Service
public class UserInfoService implements org.springframework.security.core.userdetails.UserDetailsService {
	
	@Autowired
	private IUserRepository userRepository;
	
	@Autowired
	private IRoleRepository roleRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		final User user = userRepository.findOneByUsername(username);
		
		if(user == null || (user != null && !user.getActive())) {
			//if the user is not active, let's treat them as if they were non-existent
				throw new UsernameNotFoundException(username);
		}
		System.out.println(user);
		final UserInfo ui = new UserInfo(user.getId(),user.getUsername(), user.getPassword(), user.getEmail(),  getUserRights(user));

		return ui;
	}
	private Set<GrantedAuthority> getUserRights(User user) {
		Set<GrantedAuthority> rights = new HashSet<>();
		Role role = roleRepository.findOneById(user.getUserRole());
		rights.add(new SimpleGrantedAuthority(role.getRoleName()));

		return rights;
	}
}
