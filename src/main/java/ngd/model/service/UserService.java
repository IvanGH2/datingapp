package ngd.model.service;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ngd.model.IUserRepository;
import ngd.model.User;
import ngd.model.status.EUserRole;

@Service
public class UserService {
	
	@Autowired
	private IUserRepository userRepository;
	
	public User addUser(RegisterUserForm nui, String ip) {
		
		User newUser = this.addUser(nui);
		newUser.setIp(ip);
		return newUser;
	}
	
	public User addUser(RegisterUserForm nui) {
		
		final Timestamp now = new Timestamp(System.currentTimeMillis());
		User newUser = User.builder().username(nui.getUsername())
				.password(new BCryptPasswordEncoder().encode(nui.getPassword()))
				.email(nui.getEmail())	
				.userRole(EUserRole.USER.getValue())
				.active(false)
				.age(nui.getUserAge())
				.gender(nui.getUserGender())
				.targetGender(nui.getTargetGender())
				.dateCreated(now)
				.dateChanged(now)
				.lastActivity(now)
				.build();
		
		return userRepository.save(newUser);
	}
	public void deleteUser(Integer userId) {
		
		userRepository.deleteById(userId);
	}
	public Boolean doesExistsUserWithSameUsername(String username) {
		
		User user = userRepository.findOneByUsername(username);
		
		return user != null ? true : false;
		
	}
	public Boolean doesExistsUserWithSameIp(String ip) {
		
		User user = userRepository.findOneByIp(ip);
		
		return user != null ? true : false;
		
	}
	public Boolean doesExistsUserWithSameEmail(String email) {
		
		User user = userRepository.findOneByEmail(email);
		
		return user != null ? true : false;
	}	
}
