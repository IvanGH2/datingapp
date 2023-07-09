package ngd.model;



import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface IUserMessagesRepository extends JpaRepository<UserMessage, Integer> {
	
	UserMessage findOneById(Integer id);

	UserMessage findOneBySrcUserId(Integer id);
	
	UserMessage findOneByDstUserId(Integer id);
	
	@Modifying
	//@Transactional
	@Query(value = "update ngd_user_messages  set msg_viewed = true where id in ( :ids )", nativeQuery=true)
	int updateMsgViewedStatus(@Param("ids") List<Integer> ids);
	
	@Query(value = "select count(*) from ngd_user_messages  where dst_user_id = ?1 and date_created > ?2", nativeQuery=true)
	Integer getNewMessagesCountByUserIdAndDate(Integer userId, Timestamp date);
	
	@Query(value = "select count(*) from ("
			+ " 	select distinct"
			+ "		case "
			+ "		when src_user_id  = ?1 then dst_user_id"
			+ "		when dst_user_id  = ?1 then src_user_id"
			+ "		end"
			+ "		from ngd_user_messages) msg", nativeQuery = true)
	Integer getMessageUsersForUserByUserId(Integer userId);
	
	@Query(value = "select * from ngd_user_messages where (src_user_id = ?1 and dst_user_id = ?2) or "
			+ "(src_user_id = ?2 and dst_user_id = ?1) order by id desc limit ?4 offset ?3", nativeQuery = true)		
	List<UserMessage> getMessagesForUser(Integer srcUserId, Integer dstUserId, Integer offset, Integer numRec);
	
   
}
