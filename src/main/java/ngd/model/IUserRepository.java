package ngd.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IUserRepository extends JpaRepository<User, Integer> {
	
	public User findOneByUsername(String username);
	
	public User findOneById(Integer id);
	
	public User findOneByIp(String ip);

	
	public User findOneByEmail(String email);
	
	public List<User> findAllByActiveAndTargetGender(Boolean active, String gender);
	
	public List<User> findAllByIdIn(List<Integer> ids);
	
	public List<User> findAllByIdIn(Integer[] ids);
	
	 @Query("select username from User where id in ( ?1 )")
	public List<String> getUsernames(List<Integer> userIds);
}
