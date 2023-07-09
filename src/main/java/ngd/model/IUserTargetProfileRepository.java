package ngd.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserTargetProfileRepository extends JpaRepository<TargetProfile, Integer> {
	
	TargetProfile findOneByUserId(Integer id);

	//TargetProfile findOneByBodyType(Integer bodyType);

	//TargetProfile findOneByRelStatus(Integer relStatus);
}
