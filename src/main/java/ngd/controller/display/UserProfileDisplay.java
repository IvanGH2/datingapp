package ngd.controller.display;


import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ngd.model.UserProfile;
import ngd.model.status.EUserProfile.EBodyType;
import ngd.model.status.EUserProfile.EEducationLevel;
import ngd.model.status.EUserProfile.EEmploymentStatus;
import ngd.model.status.EUserProfile.EPersonality;
import ngd.model.status.EUserProfile.EProfileVisibility;
import ngd.model.status.EUserProfile.ERelationshipStatus;
import ngd.utility.MessageUtil;
import ngd.utility.StringProcessor;


@Getter
@Setter
@Builder
public class UserProfileDisplay {
	

	private String relStatus;
	

	private String eduLevel;
	
	private String empStatus;
	

	private String bodyType;
	

	private String personalityType;
	
	
	private String profileVisibleTo;

	private String country;
	

	private String residence;
	

	private String profession;
	

	private String hobbies;
	

	private String userMsg;
	

	private String photosAvailable;
	
	private List<String> photos;
	
	//public UserProfileDisplay() {}
	
	public static UserProfileDisplay from(final UserProfile userProfile) {
		
		UserProfileDisplay userProfileDisplay = UserProfileDisplay.buildUserProfile(userProfile);
		 
		 return userProfileDisplay;
		
	}
	
	public static UserProfileDisplay from(final UserProfile userProfile, final List<String> photoLinks) {
		
		 UserProfileDisplay userProfileDisplay = UserProfileDisplay.buildUserProfile(userProfile);
		 userProfileDisplay.photos = photoLinks;
		 
		 return userProfileDisplay;
		
	}
	
	private static UserProfileDisplay buildUserProfile(final UserProfile userProfile) {
		
		final String notAvailable = MessageUtil.message("data.notavailable");
		UserProfileDisplay userProfileDisplay = UserProfileDisplay.builder()
				 .country(userProfile.getCountry())
		 		 .residence(userProfile.getCity())
		 		 .profileVisibleTo(EProfileVisibility.getLocalString(userProfile.getProfileVisibleTo()))
		 		 .bodyType(EBodyType.getLocalString(userProfile.getBodyType()))
		 		 .eduLevel(EEducationLevel.getLocalString(userProfile.getEduLevel()))
		 		 .empStatus(EEmploymentStatus.getLocalString(userProfile.getEmpStatus()))
		 		 .personalityType(StringProcessor.convertToLocalPersonalityNames(userProfile.getPersonalityType()))
		 		 .relStatus(ERelationshipStatus.getLocalString(userProfile.getRelStatus()))
		 		 .photosAvailable(userProfile.getPhotosAvailable().booleanValue() ? 
		 				MessageUtil.message("userProfile.photos") : MessageUtil.message("userProfile.photos_na"))
		 		 .profession(userProfile.getProfession() != null ?  userProfile.getProfession() : notAvailable)
		 		 .build();
		 
		 return userProfileDisplay;
	}
}
