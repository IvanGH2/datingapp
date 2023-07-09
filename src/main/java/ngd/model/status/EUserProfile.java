package ngd.model.status;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import ngd.utility.MessageUtil;


public class EUserProfile {
	
	public enum EProfileVisibility{
		
		registeredUsers(1), 
		registeredProfileVerified(2);//not currently implemented or checked 
		
		@Getter
		private Integer value;
		
		private EProfileVisibility(Integer val) {
				value = val;
			}
		private static Map<Integer, EProfileVisibility> valuesMap = new HashMap<>();
		 
		 public  static EProfileVisibility getObject(Integer val) {
			 if(valuesMap.isEmpty()) {
					for(EProfileVisibility e : EProfileVisibility.values()){
						valuesMap.put(e.value, e);
					}
				}
				return valuesMap.get(val);
		 }
			
			public  static String getLocalString(final Integer val) {
				
				final String[] localNames = {  "userProfile.visibility.registered_users", 
						"userProfile.visibility.registered_users_profile_verified" };

				return val == null ? MessageUtil.message("data.notavailable") : MessageUtil.message(localNames[val-1]);
			 }
		
	}
	public enum EChildrenStatus{
		
		irrelevant(1),
		relevant(2),
		no_children(3),
		no_small_children(4),
		small_children(5);
		
		@Getter
		private Integer value;
		
		private EChildrenStatus(Integer val) {
			value = val;
		}
		private static Map<Integer, EChildrenStatus> valuesMap = new HashMap<>();
			
		public  static String getLocalString(final Integer val) {
			
			final String[] localNames = { "userProfile.target.children.options.notimportant", "userProfile.target.children.options.important",
					"userProfile.target.children.options.nochildren", "userProfile.target.children.options.importantnotsmall", 
					"userProfile.target.children.options.importantsmall" };

					return val == null ? MessageUtil.message("data.notavailable") : MessageUtil.message(localNames[val-1]);
			}
		 
		 public  static EChildrenStatus getObject(Integer val) {
			 if(valuesMap.isEmpty()) {
					for(EChildrenStatus e : EChildrenStatus.values()){
						valuesMap.put(e.value, e);
					}
				}
				return valuesMap.get(val);
		 }
	}
	public enum EEmploymentStatus{
		
		unemployed(1),
		employee(2),
		employer(3),
		parttime(4);
		
		@Getter
		private Integer value;
		
		private EEmploymentStatus(Integer val) {
				value = val;
			}
			
			public  static String getLocalString(final Integer val) {
				
				final String[] localNames = { "userProfile.employment.status.options.unemployed", "userProfile.employment.status.options.employee",
						"userProfile.employment.status.options.employer", "userProfile.employment.status.options.parttime" };
				 
					return val == null ? MessageUtil.message("data.notavailable") : MessageUtil.message(localNames[val-1]);
			 }
		private static Map<Integer, EEmploymentStatus> valuesMap = new HashMap<>();
		 
		 public  static EEmploymentStatus getObject(Integer val) {
			 if(valuesMap.isEmpty()) {
					for(EEmploymentStatus e : EEmploymentStatus.values()){
						valuesMap.put(e.value, e);
					}
				}
				return valuesMap.get(val);
		 }
	}
	
	public enum EPersonality{
		
		reserved(1),
		outgoing(2),
		serious(3),
		responsible(4),
		funny(5),
		moody(6),
		cheerful(7),
		nostalgic(8),
		carefree(9),
		careless(10);
		
		@Getter
		private Integer value;
		
		 private EPersonality(Integer val) {
			value = val;
		}
		 private static Map<Integer, EPersonality> valuesMap = new HashMap<>();
		 
		 public  static EPersonality getObject(Integer val) {
			 if(valuesMap.isEmpty()) {
					for(EPersonality e : EPersonality.values()){
						valuesMap.put(e.value, e);
					}
				}
				return valuesMap.get(val);
		 }
		 
		public  static String getLocalString(final Integer val) {
			
			final String[] localNames = { "userProfile.personality.options.reserved", "userProfile.personality.options.outgoing",
					"userProfile.personality.options.serious", 	"userProfile.personality.options.responsible",
					"userProfile.personality.options.funny", 	"userProfile.personality.options.funny", 
					"userProfile.personality.options.cheerful", "userProfile.personality.options.nostalgic",
					"userProfile.personality.options.carefree", "userProfile.personality.options.careless" };
			
				return val == null ? MessageUtil.message("data.notavailable") : MessageUtil.message(localNames[val-1]);
			 }
	}	
	public enum EEducationLevel{
		
		primary(1),
		secondary(2),
		college(3),
		university(4),
		postgraduate(5);
		
		@Getter
		private Integer value;
				
		 private EEducationLevel(Integer val) {
			value = val;
		}
			
		public  static String getLocalString(final Integer val) {
				 
			final String[] localNames = { "userProfile.eduLevel.options.primary", "userProfile.eduLevel.options.secondary",
					"userProfile.eduLevel.options.college", "userProfile.eduLevel.options.university", "userProfile.eduLevel.options.university+" };
		
					return val == null ? MessageUtil.message("data.notavailable") : MessageUtil.message(localNames[val-1]);
			}
		 private static Map<Integer, EEducationLevel> valuesMap = new HashMap<>();
		 
		 public  static EEducationLevel getObject(Integer val) {
			
			 if(valuesMap.isEmpty()) {
					for(EEducationLevel e : EEducationLevel.values()){
						valuesMap.put(e.value, e);
					}
				}
				return valuesMap.get(val);
		 }
	}
		
public enum EBodyType{
		
		slim(1),
		normal(2),
		slightly_overweight(3),
		overweight(4),
		athletic(5);
		
		@Getter
		private Integer value;
		
		private static Map<Integer, EBodyType> valuesMap = new HashMap<>();
		
	
		
		public  static String getLocalString(final Integer val) {
			
			final String[] localNames = { "userProfile.bodytype.options.slim", "userProfile.bodytype.options.normal", "userProfile.bodytype.options.slightly_overweight", 
					"userProfile.bodytype.options.overweight", "userProfile.bodytype.options.athletic" };
	
				return val == null ? MessageUtil.message("data.notavailable") : MessageUtil.message(localNames[val-1]);
		 }
		
		 private EBodyType(Integer val) {
			value = val;
			
		}
		 public  static EBodyType getObject(Integer val) {
			 if(valuesMap.isEmpty()) {
					for(EBodyType bt : EBodyType.values()){
						valuesMap.put(bt.value, bt);
					}
				}
				return valuesMap.get(val);
		 }
		
	}
public enum ERelationshipStatus{
	
	single(1),
	in_relationship(2),
	married(3);
	
	@Getter
	private Integer value;
	
	private ERelationshipStatus(Integer val) {
		value = val;
	}
	
	private static Map<Integer, ERelationshipStatus> valuesMap = new HashMap<>();
	 
	 public  static ERelationshipStatus getObject(Integer val) {
		 if(valuesMap.isEmpty()) {
				for(ERelationshipStatus e : ERelationshipStatus.values()){
					valuesMap.put(e.value, e);					
				}
			}
			return valuesMap.get(val);
	 }
	 
	 public  static String getLocalString(final Integer val) {
		 
		 final String[] localNames = { "userProfile.relStatus.options.single", "userProfile.relStatus.options.inrelationship", 
				 "userProfile.relStatus.options.married" };
			return val == null ? MessageUtil.message("data.notavailable") : MessageUtil.message(localNames[val-1]);
	 }
	}
}
