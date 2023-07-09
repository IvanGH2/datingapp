package ngd.controller.display;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ngd.annotation.ProfileCompletionAttr;
import ngd.controller.CommonObject;
import ngd.controller.photos.PhotoRetriever;
import ngd.model.User;
import ngd.model.UserProfile;
import ngd.model.status.EUserProfile;
import ngd.utility.DateTimeUtil;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDisplay {
	
	private String username;
	
	private Integer userId;
	
	private String since; 
	
	@ProfileCompletionAttr
	private String relStatus;
	
	private String lastActivity;	
	
	private String profileVisitedOn;
	
	private Boolean online;
	
	@ProfileCompletionAttr
	private String age;
	
	@ProfileCompletionAttr
	private String from;
	
	@ProfileCompletionAttr
	private String message;
	
	@ProfileCompletionAttr
	private String profession;
	
	@ProfileCompletionAttr
	private String education;
	
	@ProfileCompletionAttr
	private String personalities;
	
	@ProfileCompletionAttr
	private String bodyType;
	
	@ProfileCompletionAttr
	private String hobbies;
	
	@ProfileCompletionAttr
	private Boolean photoAvailable;
	
	private List<String> photos;	
	
	//private List<byte[]> photoBytes;
	
	private boolean photosAsBytes;
	
	private boolean profileCompleted;
	
	private String profileCompletion;
	
	public static PhotoRetriever photoRetriever;
	
	public static Integer numUserViews = 0;
	
	public static Integer numUserSearch = 0;
		
	public static MemberDisplay from(final User user, final UserProfile userProfile, final PhotoRetriever phRet, boolean photosAsBytes) throws IOException {
		
		MemberDisplay memDisplay = new MemberDisplay();
		photoRetriever = phRet;
		memDisplay.photosAsBytes = photosAsBytes;
		return from(memDisplay, user, userProfile, true);
		
	}
	public void setProfileCompleteStatus() throws IllegalArgumentException, IllegalAccessException {
		
		int itemsCompleted = 0, itemsTotal = 0;
		Class<MemberDisplay> clz = MemberDisplay.class;
		Field[] fields = clz.getDeclaredFields();
		
		for(Field field : fields) {
			
			if(field.isAnnotationPresent(ProfileCompletionAttr.class)) {
				itemsTotal++;
				if( field.get(this) != null )
					itemsCompleted++;
			}
		}
		profileCompleted = (float)itemsCompleted / itemsTotal > 0.7f ? true : false;
		
		profileCompletion =  String.valueOf((100 * itemsCompleted / itemsTotal) ) + "%";
	}
	private static MemberDisplay from(final MemberDisplay memDisplay, final User user, 
			final UserProfile userProfile, boolean fetchPhotos) throws IOException {
		
		if(memDisplay == null || user == null || userProfile == null) return null;
			
		memDisplay.setAge(user.getAge().toString());
		memDisplay.setSince(DateTimeUtil.formatDate(user.getDateCreated(), "dd-MM-yyyy"));
		memDisplay.setUsername(user.getUsername());
		memDisplay.setLastActivity(DateTimeUtil.formatDate(user.getLastActivity(), "dd-MM-yyyy"));
		memDisplay.setUserId(user.getId());
		memDisplay.setRelStatus(EUserProfile.ERelationshipStatus.getLocalString(userProfile.getRelStatus()));		
		
		if(userProfile.getCountry() != null && userProfile.getCity() != null)
			memDisplay.setFrom(userProfile.getCity() +", " + userProfile.getCountry());
		else if(userProfile.getCity() != null)
			memDisplay.setFrom(userProfile.getCity());
		else if(userProfile.getCountry() != null)
			memDisplay.setFrom(userProfile.getCountry());
		
		if(userProfile.getBodyType() != null)
				memDisplay.setBodyType(EUserProfile.EBodyType.getLocalString(userProfile.getBodyType()));
		
		if(userProfile.getEduLevel() != null)
			memDisplay.setEducation(EUserProfile.EEducationLevel.getLocalString(userProfile.getEduLevel()));
		
		if(userProfile.getPersonalityType() != null)
			memDisplay.setPersonalities(userProfile.getPersonalityType().toString());
		if(userProfile.getProfession() != null)
			memDisplay.setProfession(userProfile.getProfession());
		
		if(userProfile.getUserMsg() != null)
			memDisplay.setMessage(userProfile.getUserMsg());
		memDisplay.setHobbies(userProfile.getHobbies());
		memDisplay.setPhotoAvailable(userProfile.getPhotosAvailable());	
		memDisplay.setOnline(CommonObject.sseUsers.containsKey(user.getUsername()));
		
		if(fetchPhotos && userProfile.getPhotosAvailable()) {		
			memDisplay.setPhotos(photoRetriever.getUserPhotos(userProfile));
		}
		return memDisplay;

	}
	
public static List<MemberDisplay> from(final List<User> users, final List<UserProfile> userProfiles, final PhotoRetriever phRet) throws IOException {
		
		List<MemberDisplay> members = new ArrayList<>();
		photoRetriever = phRet;
		for(int i=0;i<users.size();++i) {
			MemberDisplay memDisplay = new MemberDisplay();
			members.add(from(memDisplay, users.get(i), userProfiles.get(i), false));
		}
		return members;
	}

}
