package ngd.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserStatusRepository extends JpaRepository<UserStatus, Integer> {
	UserStatus findOneByUserId(Integer userId);
	UserStatus findOneById(Integer id);
}
