package ngd.model.nativequery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ngd.controller.display.MemberDisplay;
import ngd.controller.photos.PhotoRetriever;
import ngd.controller.rest.model.TargetProfileForm;
import ngd.model.User;
import ngd.model.UserMessage;
import ngd.model.UserProfile;
import ngd.model.UserProfileViews;
import ngd.model.nativequery.model.FlaggedMessageInfo;
import ngd.model.nativequery.model.TargetUserBasicInfo;
import ngd.model.status.EUserProfile.EBodyType;
import ngd.model.status.EUserProfile.EEducationLevel;
import ngd.model.status.EUserProfile.EEmploymentStatus;
import ngd.model.status.EUserProfile.ERelationshipStatus;

@Repository
public class NativeQuery {
	
	private static final Logger logger = LoggerFactory.getLogger(NativeQuery.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private PhotoRetriever photoRetriever;
		
public List<FlaggedMessageInfo>	 getFlaggedMessages(Integer numRecords, Integer offset){
	
	final NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
	final MapSqlParameterSource params = new MapSqlParameterSource();
	params.addValue("limit", numRecords, Types.INTEGER);
	params.addValue("offset", offset, 	 Types.INTEGER);
	
	final String query = "select u.id as senderId, u.username as senderUsername, um.id msgId, um.msg_txt as msg, um.date_created as msgDate from ngd_user_messages um,"
			+ " ngd_user u where u.id = um.src_user_id and u.active = true and um.msg_flag = true "
			+ " order by um.id desc limit :limit offset :offset";
	
	return namedJdbcTemplate.query(query, params, BeanPropertyRowMapper.newInstance(FlaggedMessageInfo.class));
}
public TargetUserBasicInfo getTargetUsersInfo(Integer userId) {
	
	final NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
	final MapSqlParameterSource params = new MapSqlParameterSource();
	
	params.addValue("currUserId", userId, Types.INTEGER);
	
	final String query = "select target_gender as gender, age_from, age_to from ngd_user u, ngd_user_target_profile utp "
			+ "where id = :currUserId and u.id = utp.user_id and active=true";
	try {
		return namedJdbcTemplate.queryForObject(query,  params,  BeanPropertyRowMapper.newInstance(TargetUserBasicInfo.class));
	}catch(DataAccessException e) {
		return null;
	}	 
	
}
public Integer findMatchingUsersCount( TargetUserBasicInfo targetInfo){
		
		final NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		final MapSqlParameterSource params = new MapSqlParameterSource();
		
		params.addValue("targetGender", targetInfo.getGender(), Types.VARCHAR);
		params.addValue("startAge", targetInfo.getAgeFrom(), Types.INTEGER);
		params.addValue("endAge", targetInfo.getAgeTo(), Types.INTEGER);
		
		final String query = "select count(*) from ngd_user where active = true and "
				+ "gender = :targetGender and age between :startAge and "
				+ ":endAge";
		
		 return namedJdbcTemplate.queryForObject(query,  params,  Integer.class);
		
	}
public Integer searchMemberProfilesCount(TargetProfileForm searchProfileForm, Integer currUserId) {
	

	final NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
	final MapSqlParameterSource params = new MapSqlParameterSource();

	final String query =createSearchQueryAndSetParams(searchProfileForm, params, true, currUserId);

	System.out.println(query);
	 return namedJdbcTemplate.queryForObject(query, params, Integer.class);
	
}
private String createSearchQueryAndSetParams(final TargetProfileForm searchProfileForm, 
		final MapSqlParameterSource params, boolean retCount, Integer currUserId) {
	
	String query = null;
	
	final String gender 		= 	searchProfileForm.getTargetGender();
	final Integer ageFrom 		= 	searchProfileForm.getAgeFrom();
	final Integer ageTo			= 	searchProfileForm.getAgeTo();
	//process countries
	final List<String> countries = 	searchProfileForm.getCountries();
	final Boolean countriesInclusion = searchProfileForm.getCountriesInclude();	
	final String countriesTerm = countries != null ? countriesInclusion == true ? " country in (:countries) " 
			: " country not in (:countries) " : "1=1";
	//process education
	final List<Integer> eduConditions = new ArrayList<>();
	final List<EEducationLevel> eduLevels = searchProfileForm.getEduLevels();
	if(eduLevels != null) eduLevels.forEach(v -> eduConditions.add(v.getValue()));
	final Boolean eduInclusion = searchProfileForm.getEduLevelsInclude();
	final String eduTerm	= eduLevels != null ? eduInclusion == true ? " edu_level in (:eduLevel) "
			: " edu_level not in (:eduLevel) " : "1=1";
	//process relationship
	final List<Integer> relConditions = new ArrayList<>();
	final List<ERelationshipStatus> relStatus  = searchProfileForm.getRelStatuses();
	if(relStatus != null) relStatus.forEach(v -> relConditions.add(v.getValue()));
	System.out.println(relConditions);
	System.out.println(relStatus);
	final Boolean relInclusion	= searchProfileForm.getRelStatusInclude();
	final String relTerm = relStatus != null ? relInclusion == true ? " rel_status in (:relStatus) "
			: " rel_status not in (:relStatus) " : "1=1";
	
	final List<EEmploymentStatus> empStatus = searchProfileForm.getEmploymentStatuses();
	final List<Integer> empConditions = new ArrayList<>();
	if(empStatus != null) empStatus.forEach(v -> empConditions.add(v.getValue()));
	final Boolean empInclusion = searchProfileForm.getEmploymentStatusInclude();
	final String empTerm = empStatus != null ? empInclusion == true ? " emp_status in (:empStatus) "
			: " emp_status not in (:empStatus) " : "1=1";
	
	final List<Integer> bodyConditions = new ArrayList<>();
	final List<EBodyType> bodyTypes = searchProfileForm.getBodyTypes();
	if(bodyTypes != null) bodyTypes.forEach(v -> bodyConditions.add(v.getValue()));
	final Boolean bodyInclusion = searchProfileForm.getBodyTypesInclude();
	final String bodyTerm = bodyTypes != null ? bodyInclusion == true ? " body_type in (:bodyType) "
			: " body_type not in (:bodyType) " : "1=1";
	//if(countries != null)
		//countriesPredicate =  countriesInclusion == true ? " in (:countries) " : " not in (:countries) ";
	
	params.addValue("currUserId", 	currUserId, Types.INTEGER);
	params.addValue("ageFrom", 		ageFrom, Types.INTEGER);
	params.addValue("ageTo", 		ageTo, Types.INTEGER);
	params.addValue("gender", 		gender, Types.VARCHAR);
	params.addValue("countries", 	countries, Types.VARCHAR);
	params.addValue("relStatus", 	relConditions, Types.INTEGER);
	params.addValue("eduLevel", 	eduConditions, Types.INTEGER);
	params.addValue("bodyType", 	bodyConditions, Types.INTEGER);
	params.addValue("empStatus", 	empConditions, Types.INTEGER);
	
	if(retCount) {
		query = "select  count(*) from ngd_user u, ngd_user_profile "
				+ " up where  u.id = up.user_id and u.id != :currUserId and u.gender = :gender and u.age between "
				+ " :ageFrom and :ageTo and " + countriesTerm  + " and " + relTerm + " and " 
				+ bodyTerm + " and " + eduTerm + " and "+empTerm;
		
	}else {
		query = "select  u.age, u.date_created " //u.id, u.gender, u.target_gender,
		+ ", up.rel_status, up.edu_level, up.body_type"
		+ ", up.country, up.city, up.profession, up.hobbies, up.user_message, up.personality"
		+ ", up.photos_available, u.id, u.last_activity, u.username from ngd_user u, ngd_user_profile "
		+ " up where  u.id = up.user_id and u.id != :currUserId and u.gender = :gender and u.age between "
		+ " :ageFrom and :ageTo and " + countriesTerm + " and " + relTerm + " and " + bodyTerm 
		+ " and " + eduTerm + " and "+ empTerm +" order by id limit :limit offset :offset ";
		}
	return query;
	
}
public List<MemberDisplay> searchMemberProfiles(TargetProfileForm searchProfileForm, Integer currUserId, 
		Integer numRecords, Integer offset) {
	

	final NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
	final MapSqlParameterSource params = new MapSqlParameterSource();
	
	params.addValue("limit", numRecords, Types.INTEGER);
	params.addValue("offset", offset, Types.INTEGER);
	
	final String query = createSearchQueryAndSetParams(searchProfileForm, params, false, currUserId);
	System.out.println(query);
	System.out.println("searchProfilesByUserIds");
	 return namedJdbcTemplate.query(query, params, new MemberDisplayRowMapper());
	
}
	public List<User> findMatchingUsers(Integer currUserId, String targetGender, Integer startAge, Integer endAge, 
			Integer numRecords, Integer offset){
		
		final NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		final MapSqlParameterSource params = new MapSqlParameterSource();
		
		params.addValue("cUserId", currUserId, Types.INTEGER);
		params.addValue("targetGender", targetGender, Types.VARCHAR);
		params.addValue("startAge", startAge, Types.INTEGER);
		params.addValue("endAge", endAge, Types.INTEGER);
		params.addValue("limit", numRecords, Types.INTEGER);
		params.addValue("offset", offset, Types.INTEGER);
		System.out.println("limit " + offset.toString() + numRecords.toString());
		final String query = "select * from ngd_user where active = true and id != :cUserId and "
				+ " gender = :targetGender and age between :startAge and "
				+ ":endAge order by id limit :limit offset :offset";
		
		 return namedJdbcTemplate.query(query, params, BeanPropertyRowMapper.newInstance(User.class));
		
	}
	
public List<UserProfileViews> findTargetProfileViews(Integer userId, Integer numRecords, Integer offset){
		
		final NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		final MapSqlParameterSource params = new MapSqlParameterSource();
		
		params.addValue("srcUserId", userId, 	Types.INTEGER);
		params.addValue("limit", 	numRecords, Types.INTEGER);
		params.addValue("offset", 	offset, 	Types.INTEGER);
		
		final String query = "select * from ngd_user_profile_views upv, ngd_user u where upv.src_user_id = :srcUserId  and u.id = upv.dst_user_id"
				+ " and u.active=true order by upv.date_viewed desc limit :limit offset :offset";
		
		System.out.println("find dst views " + query);
		 return namedJdbcTemplate.query(query, params, BeanPropertyRowMapper.newInstance(UserProfileViews.class));
		
	}
//this return incoming views
public List<UserProfileViews> findSourceProfileViews(Integer userId, Integer numRecords, Integer offset){
	
	final NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
	final MapSqlParameterSource params = new MapSqlParameterSource();
	
	params.addValue("dstUserId", userId, 	Types.INTEGER);
	params.addValue("limit", 	numRecords, Types.INTEGER);
	params.addValue("offset", 	offset, 	Types.INTEGER);
	
	final String query = "select * from ngd_user_profile_views upv, ngd_user u where upv.dst_user_id = :dstUserId and u.id=upv.src_user_id "
			+ "and u.active=true order by upv.date_viewed desc limit :limit offset :offset";
	 System.out.println("find src views " + query);
	 return namedJdbcTemplate.query(query, params, BeanPropertyRowMapper.newInstance(UserProfileViews.class));
	
}

public List<UserProfile> findUserProfilesByUserId(List<Integer> userIds){
		
		
	if(userIds == null || userIds.size()==0)
			return null;
	final NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
	final MapSqlParameterSource params = new MapSqlParameterSource();
	params.addValue("userIds", userIds, Types.INTEGER);
		
	final String query = "select * from ngd_user_profile where user_id IN (:userIds)";
		
	return namedJdbcTemplate.query(query, params, BeanPropertyRowMapper.newInstance(UserProfile.class));
		
	}

public List<UserMessage> getUserMessagesDesc(Integer userId, Integer numRecords, Integer offset){
	
	final NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
	final MapSqlParameterSource params = new MapSqlParameterSource();
	
	params.addValue("userId", userId, Types.INTEGER);
	params.addValue("limit", numRecords, Types.INTEGER);
	params.addValue("offset", offset, Types.INTEGER);
	
	final String query = "select * from ngd_user_messages where  "
			+ "src_user_id = :userId or dst_user_id = :userId  "
			+ " order by id desc limit :limit offset :offset ";
	
	 return namedJdbcTemplate.query(query, params, BeanPropertyRowMapper.newInstance(UserMessage.class));
}
public List<MemberDisplay> getMemberProfilesByUserIds(List<Integer> userIds) {
	
	Objects.requireNonNull(userIds);
	final NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
	final MapSqlParameterSource params = new MapSqlParameterSource();
	params.addValue("userIds", userIds, Types.INTEGER);
	final String query = "select  u.age, u.date_created " //u.id, u.gender, u.target_gender,
			+ ", up.rel_status, up.edu_level, up.body_type"
			+ ", up.country, up.city, up.profession, up.hobbies, up.user_message, up.personality"
			+ ", up.photos_available, u.id, u.last_activity, u.username from ngd_user u, ngd_user_profile "
			+ " up where u.id in ( :userIds ) and u.id = up.user_id";
	System.out.println("getMemberProfilesByUserIds");
	 return namedJdbcTemplate.query(query, params, new MemberDisplayRowMapper());
	
}
public MemberDisplay getUserProfileByUsername(String username) {
	
	Objects.requireNonNull(username);
	
	final NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
	final MapSqlParameterSource params = new MapSqlParameterSource();
	params.addValue("username", username, Types.CHAR);
	final String query = "select  u.age, u.date_created " //u.id, u.gender, u.target_gender,
			+ ", up.rel_status, up.edu_level, up.body_type"
			+ ", up.country, up.city, up.profession, up.hobbies, up.user_message, up.personality"
			+ ", up.photos_available, u.id , u.last_activity, u.username from ngd_user u, "
			+ " ngd_user_profile up where username = :username and u.id = up.user_id";

	 return namedJdbcTemplate.queryForObject(query, params, new MemberDisplayRowMapper());
	
}
  private class MemberDisplayRowMapper implements RowMapper<MemberDisplay> {

	@Override
	public MemberDisplay mapRow(final ResultSet resultSet, final int pRowNumber) throws SQLException {
		
		User user = new User();
		
		user.setAge(resultSet.getInt(1) > 0 ? resultSet.getInt(1) : null);
		user.setDateCreated(resultSet.getTimestamp(2));		
		user.setLastActivity(resultSet.getTimestamp(14));
		user.setId(resultSet.getInt(13));
		user.setUsername(resultSet.getString(15));
		UserProfile userProfile = new UserProfile();
		userProfile.setRelStatus(resultSet.getInt(3) > 0 ? resultSet.getInt(3) : null);
		userProfile.setEduLevel(resultSet.getInt(4)  > 0 ? resultSet.getInt(4) : null);
		userProfile.setBodyType(resultSet.getInt(5)  > 0 ? resultSet.getInt(5) : null);
		userProfile.setCountry(resultSet.getString(6));
		userProfile.setCity(resultSet.getString(7));
		userProfile.setProfession(resultSet.getString(8));
		userProfile.setHobbies(resultSet.getString(9));
		userProfile.setUserMsg(resultSet.getString(10));
		userProfile.setPersonalityType(resultSet.getString(11));
		userProfile.setPhotosAvailable(resultSet.getBoolean(12));
		userProfile.setUserId(resultSet.getInt(13));
				
		MemberDisplay memDisplay = null;
		
		try {

			memDisplay = MemberDisplay.from(user, userProfile, photoRetriever, false);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return memDisplay ;
	}

}

}
