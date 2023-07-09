package ngd.controller.rest.model;


import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ngd.model.status.EUserProfile;

@Getter
@Setter
public class UserProfileForm implements Serializable{	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5111939205634895185L;

	private EUserProfile.ERelationshipStatus eRelStatus;
	
	private String country;
	
	private String residence;
	
	private EUserProfile.EEducationLevel eEduLevel;
	
	private String profession;
	
	private List<EUserProfile.EPersonality> personalities;
	
	private EUserProfile.EEmploymentStatus eEmploymentStatus;
	
	private EUserProfile.EBodyType eBodyType;
	
	private String hobbies;
	
	private String message;
	
	private List<String> photoLinks;
	

}
