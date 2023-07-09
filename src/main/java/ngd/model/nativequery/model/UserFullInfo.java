package ngd.model.nativequery.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 
 * @author ibalen
 *this class represent information from both User and UserProfile classes (tables) 
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFullInfo {
	
	private Integer id;
	
	private String gender;
	
	private String targetGender;
	
	private Integer age;
	
	private Timestamp dateCreated;
	
	private Integer relStatus;
	
	private Integer eduLevel;
	
	private Integer bodyType;
	
	private Integer profileVisibleTo;
	
	private String personalityType;
	
	private String country;	

	private String city;
	
	private String profession;
	
	private String hobbies;
	
	private String userMsg;
	
	private Boolean photosAvailable;

}
