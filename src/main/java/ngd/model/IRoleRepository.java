package ngd.model;

import org.springframework.data.jpa.repository.JpaRepository;



public interface IRoleRepository extends JpaRepository<Role, Integer> {
	Role findOneByRoleName(String rolename);
	Role findOneById(Integer id);
}
