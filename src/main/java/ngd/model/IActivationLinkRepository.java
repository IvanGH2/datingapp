package ngd.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IActivationLinkRepository extends JpaRepository<ActivationLink, Integer> {
	
	ActivationLink findOneByUserId(Integer userId);
	
	ActivationLink findOneById(Integer id);
	
	ActivationLink findOneByActivationLink(String aLink);
}
