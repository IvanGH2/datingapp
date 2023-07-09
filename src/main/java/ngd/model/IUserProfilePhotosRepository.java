package ngd.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserProfilePhotosRepository extends JpaRepository<UserProfilePhotos, Integer> {
	
	UserProfilePhotos findOneByUserId(Integer userId);

	UserProfilePhotos findOneById(Integer id);
	
	List<UserProfilePhotos> findAllByUserId(Integer userId);
	
	UserProfilePhotos findOneByUserIdAndLinkToPhoto(Integer userId, String link);

}
