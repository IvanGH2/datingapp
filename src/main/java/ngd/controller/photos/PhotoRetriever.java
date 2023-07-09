package ngd.controller.photos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import ngd.controller.MainController;
import ngd.model.IUserProfilePhotosRepository;
import ngd.model.UserProfile;
import ngd.model.UserProfilePhotos;
import ngd.utility.CurrentUserUtility;
import ngd.utility.MessageUtil;


@Service
public class PhotoRetriever {
	
	private static final Logger logger = LoggerFactory.getLogger(PhotoRetriever.class);
	
	@Value("${ngd.ext-resource.location}")
	private String extResource;
	
	@Value("${ngd.photos.max}")
    private Integer numPhotosMax;
    
    private String photoNotSavedReason;
    
    public String getPhotoSaveFailedReason() {
          return photoNotSavedReason;
    }



	@Autowired
	private IUserProfilePhotosRepository userProfileRepo;

		public boolean deleteUserPhoto(Integer userId, String imgName) {
	
		Path imgPath =  getUserPhotoLocationPath(userId);
		
		File img =new File(imgPath.toString(), imgName);
		if(img.exists()) {
			img.delete();
			return true;
		}

		return false;
	}
	public  List<String> getUserPhotos(UserProfile userProfile) {
		
		List<String> userPhotoLinks = new ArrayList<>();
		if(!userProfile.getPhotosAvailable()) 
			return userPhotoLinks;

		Path rootPath =  getUserPhotoLocationPath(userProfile.getUserId());
		try {

			File imgFolder = rootPath.toFile();
			File[] photos = imgFolder.listFiles();
		
			if(photos != null) {
			for(final File photo : photos)
				userPhotoLinks.add(userProfile.getUserId() + "/"  + photo.getName());
			}
		}catch(Exception e) {
			logger.error(e.toString());
		}
		
		List<UserProfilePhotos> externalPhotos = userProfileRepo.findAllByUserId(userProfile.getUserId());
		for(final UserProfilePhotos extPhoto : externalPhotos)
			userPhotoLinks.add(extPhoto.getLinkToPhoto());
		
		return userPhotoLinks;	
	}

	public boolean savePhotos(final MultipartHttpServletRequest request) throws IOException {
	       
	       final List<MultipartFile> sentFiles = request.getFiles("file");
	       
	       if(sentFiles != null && !sentFiles.isEmpty()) {
	             
	             final Path rootPath = getUserPhotoLocationPath(CurrentUserUtility.getCurrentUser().getUserId());
	             final String[] prevImgs =  rootPath.toFile().list();
	             final Integer photosPrevSaved = prevImgs == null ? 0 : rootPath.toFile().list().length;
	             
	             if ( photosPrevSaved >= numPhotosMax ) {
	                    photoNotSavedReason = MessageUtil.message("userProfile.photo.save.fail.maxsaved", numPhotosMax);
	                    
	             }else if (photosPrevSaved + sentFiles.size() >  numPhotosMax) {
	                    photoNotSavedReason = MessageUtil.message("userProfile.photo.save.fail.maxexceed", photosPrevSaved, numPhotosMax - photosPrevSaved );
	                    
	             }else {      
	                           Files.createDirectories(rootPath);
	                           int photosSaved = 0;
	                           boolean failed = false;
	                           for( final MultipartFile file : sentFiles) {  
	                                 try {
	                                        Path path = rootPath.resolve(file.getOriginalFilename());
	                                        Files.copy(file.getInputStream(), path); 
	                                        photosSaved++;
	                                 }catch(Exception e) {
	                                        logger.error(e.toString());
	                                        failed = true;
	                                 }
	                           }
	                           if(failed) {
	                                 if(photosSaved == 0)
	                                        photoNotSavedReason = MessageUtil.message("userProfile.photo.save.fail");
	                                 else
	                                        photoNotSavedReason = MessageUtil.message("userProfile.photo.save.fail.partial", sentFiles.size() - photosSaved);
	                                 
	                           }else        
	                                 return true; 
	             }
	       }
	       return false;
	}  

	private Path getUserPhotoLocationPath(Integer userId) {
		
		final String pathFile = "file:/";
		final String pathJar  = "jar:file:/";
		logger.info("ext resource: "+ extResource);
		final String saveLocation = getClass().getClassLoader().getResource(extResource).toString();
		logger.info("save upload loc: "+ saveLocation);
		
		if(saveLocation.startsWith(pathFile))
			return Paths.get(saveLocation.substring(pathFile.length(), saveLocation.length()) + File.separator + userId);
		else if (saveLocation.startsWith(pathJar))
			return Paths.get(saveLocation.substring(pathJar.length(), saveLocation.length()) +  File.separator + userId);
		else
			return Paths.get(saveLocation + File.separator + userId);
			
	}
}
