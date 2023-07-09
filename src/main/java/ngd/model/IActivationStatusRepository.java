package ngd.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IActivationStatusRepository extends JpaRepository<ActivationStatus, Integer> {
	ActivationStatus findOneById(Integer id);
}
