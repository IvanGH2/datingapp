package ngd.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ngd.controller.display.MemberDisplay;
import ngd.controller.photos.PhotoRetriever;
import ngd.model.IUserProfileRepository;
import ngd.model.IUserRepository;
import ngd.model.User;
import ngd.model.UserProfile;
import ngd.model.UserProfileViews;
import ngd.model.nativequery.NativeQuery;
import ngd.model.nativequery.model.TargetUserBasicInfo;
import ngd.model.status.EDirection;
import ngd.utility.CurrentUserUtility;
import ngd.utility.DateTimeUtil;

public class UserProfileHelper {
	

	private static PhotoRetriever photoRetriever;
	
	public static void  setPhotoRetriever(PhotoRetriever photoRet) {
		photoRetriever = photoRet;
	}
	
	public static List<MemberDisplay> getMembersInfo(final NativeQuery nativeQuery, TargetUserBasicInfo targetInfo, int numRecords, int startRec) throws IOException, IllegalArgumentException, IllegalAccessException{
		
		//find all users meeting the set criteria
		final Integer userId = CurrentUserUtility.getCurrentUser().getUserId();
		List<User>members = nativeQuery.findMatchingUsers(userId, targetInfo.getGender(), targetInfo.getAgeFrom(), 
				targetInfo.getAgeTo(), numRecords, startRec);
		List<Integer> userIds = new ArrayList<>();
	
		for(final User member : members) {
			userIds.add(member.getId());
		}

		//get user profiles as well
		List<UserProfile> memberProfiles = nativeQuery.findUserProfilesByUserId(userIds);
		//convert users and user profiles to MemberDisplay 
		List<MemberDisplay> memDisplay =  MemberDisplay.from(members, memberProfiles, photoRetriever);
		for(final MemberDisplay memberProfile : memDisplay) {
			memberProfile.setProfileCompleteStatus();
		}
		return memDisplay;
	}
	
public static List<MemberDisplay> getProfileViewsInfo(EDirection eDirection, final NativeQuery nativeQuery, 
		IUserRepository userRepository,
		IUserProfileRepository userProfileRepository,
		int numRecords, int offset) throws IOException, IllegalArgumentException, IllegalAccessException{
		
		//find all users meeting the set criteria
		final Integer userId = CurrentUserUtility.getCurrentUser().getUserId();
		
		List<UserProfileViews> views = eDirection == EDirection.outgoing ? nativeQuery.findTargetProfileViews(userId, numRecords, offset)
																		 : nativeQuery.findSourceProfileViews(userId, numRecords, offset);
		List<Integer> userIds = new ArrayList<>();
		HashMap<Integer, UserProfileViews> mapViews = new HashMap<>();
		for(final UserProfileViews view : views) {
			final Integer currUserId = eDirection == EDirection.outgoing ? view.getDstUserId() : view.getSrcUserId();
			userIds.add(currUserId);
			mapViews.put(currUserId, view);
		}
		
		List<MemberDisplay> memDisplay =  nativeQuery.getMemberProfilesByUserIds(userIds);//.from(members, memberProfiles);

		for(final MemberDisplay memberProfile : memDisplay) {
			memberProfile.setProfileCompleteStatus();
			memberProfile.setProfileVisitedOn(DateTimeUtil.formatDate(mapViews.get(memberProfile.getUserId()).getDateViewed(), "dd-MM-yyyy"));
			
		}
		return memDisplay;
	}
}
