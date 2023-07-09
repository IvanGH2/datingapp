package ngd.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IUserMessagesLastRepository extends JpaRepository<UserMsgLastActivity, Integer>{
	
	UserMsgLastActivity findOneBySrcUserId(Integer id);
	
	UserMsgLastActivity findOneBySrcUserIdAndDstUserId(Integer srcId, Integer dstId);
	
	@Query(value =  "select * from ngd_user_msg_last where (src_user_id = ?1 and dst_user_id = ?2) or (dst_user_id = ?1 and src_user_id=?2)", nativeQuery = true) 
	UserMsgLastActivity getUserMsgForUserId(Integer srcUserId, Integer dstUserId);
	
	@Query(value = "select count(*) from ( select distinct msg.userId from ( "
			+ "		select "
			+ "					case "
			+ "						when src_user_id  = ?1 then dst_user_id "
			+ "						when dst_user_id  = ?1 then src_user_id "
			+ "						end as userId "
			+ "						from ngd_user_msg_last order by msg_id desc) msg where userId is not null ) msgFiltered", nativeQuery = true)
	Integer getMessageUsersForUserByUserId(Integer userId);
	
	@Query(value =  " select  msg.userId from ("
			+ "		  select "
			+ "			case "
			+ "			when src_user_id  = ?1 then dst_user_id "
			+ "			when dst_user_id  = ?1 then src_user_id "
			+ "			end as userId"
			+ "			from ngd_user_msg_last order by msg_id desc) msg where userId is not null  limit ?3 offset ?2", nativeQuery = true)
	List<Integer> getMessageUsers(Integer userId, Integer offset, Integer numRec);

}
