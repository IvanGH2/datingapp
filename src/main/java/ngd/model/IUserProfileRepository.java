package ngd.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserProfileRepository extends JpaRepository<UserProfile, Integer> {
		
	UserProfile findOneByUserId(Integer id);
	
//	UserProfile findOneByBodyType(Integer bodyType);
	
//	UserProfile findOneByRelStatus(Integer relStatus);
	
	List<UserProfile> findAllByUserIdIn(List<Integer> userIds);
}
