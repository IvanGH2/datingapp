package ngd.model.service;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import ngd.model.status.EUserProfile.EProfileVisibility;
import ngd.model.status.EUserProfile.ERelationshipStatus;

@Getter
@Setter
public class RegisterUserForm  implements Serializable {
	
	private static final long serialVersionUID = 4777599284800241545L;

	private String username;
	
	private String password;
	
	private String passwordConfirmed;
	
	private String email;
	
	private Integer userAge;
	
	private String userGender;
	
	private String targetGender;
	
	private String country;
	
	private String residence;
	
	private  ERelationshipStatus relStatus;
	
	private EProfileVisibility profileVisibleTo;
	
	public String toString() {
		
		return "username: "+username+" psw: "+password+" psw repeat: "+this.passwordConfirmed+" user age: "+userAge.toString()
		+"user gender: "+userGender+" target gender: "+targetGender + " country: "+country+" residence: "+residence
		+ " relStatus: "+relStatus+" profileVisibleTo: "+profileVisibleTo;
	}
}
