package ngd.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IBlockedUserRepository extends JpaRepository<BlockedUser, Integer> {
	
	BlockedUser findOneByUserId(Integer id);

}
