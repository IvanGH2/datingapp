package ngd.model;


import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IUserProfileViewsRepository extends JpaRepository<UserProfileViews, Integer>{

	UserProfileViews findOneBySrcUserIdAndDstUserId(Integer srcUserId, Integer dstUserId);
	
	List<UserProfileViews> findAllBySrcUserId(Integer userId);
	
	List<UserProfileViews> findAllByDstUserId(Integer userId);
	
	@Query(value = "select count(*) from ngd_user_profile_views where dst_user_id = ?1 and date_viewed > ?2", nativeQuery=true)
	Integer getNewProfileViewsCountByUserIdAndDate(Integer userId, Timestamp date);
	
	@Query(value = "select count(*) from ngd_user_profile_views upv, ngd_user u where upv.src_user_id = ?1 and u.id = upv.dst_user_id and u.active = true",  nativeQuery = true)
	Integer getProfileViewsForTargetUser(Integer userId);
	
	@Query(value = "select count(*) from ngd_user_profile_views upv, ngd_user u where upv.dst_user_id = ?1 and u.id = upv.src_user_id and u.active = true",  nativeQuery = true)
	Integer getProfileViewsForSourceUser(Integer userId);
}
