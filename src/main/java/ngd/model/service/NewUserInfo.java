package ngd.model.service;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewUserInfo implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -874914006100622933L;

	private String userName;
	
	private String fullName;
	
	private String email;
	
	private String password;
	
	private String gender;
	
	private String targetGender;
	
	private Integer age;
	
	private Integer userRole;

	@Override
	public String toString() {
		return "NewUserInfo [fullname=" + fullName + ", username=" + userName + ", email=" + email + ", password="
				+ password + "]";
	}
}