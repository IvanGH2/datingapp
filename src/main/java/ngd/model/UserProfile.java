package ngd.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ngd_user_profile")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
	
	@Id
	@Column(name = "user_id", nullable = false)
	private Integer userId;
	
	@Column(name = "rel_status", nullable = false)
	private Integer relStatus;
	
	@Column(name = "edu_level", nullable = true)
	private Integer eduLevel;
	
	@Column(name = "emp_status", nullable = true)
	private Integer empStatus;
	
	@Column(name = "body_type", nullable = true)
	private Integer bodyType;
	
	@Column(name = "profile_visible_to", nullable = false)
	private Integer profileVisibleTo;
	
	@Column(name = "personality", nullable = true)
	private String personalityType;
	
	@Column(name = "country", nullable = false)
	private String country;
	
	@Column(name = "city", nullable = false)
	private String city;
	
	@Column(name = "profession", nullable = true)
	private String profession;
	
	@Column(name = "hobbies", nullable = true)
	private String hobbies;
	
	@Column(name = "user_message", nullable = true)
	private String userMsg;
	
	@Column(name = "date_created", nullable = false)
	private Timestamp dateCreated;
	
	@Column(name = "date_changed", nullable = false)
	private Timestamp dateChanged;
	
	@Column(name = "photos_available", nullable = false)
	private Boolean photosAvailable;
	
	@Override
	public String toString() {
		return "UserProfile[userId="+ userId+" relStatus="+relStatus+" bodyType="+bodyType+" personality="+personalityType
				+" eduLevel="+eduLevel+" country="+country+" hobbies="+hobbies+" profession="+profession
				+" userMsg="+userMsg + " profileVisibleTo="+profileVisibleTo+" dateCreated="+dateCreated+" dateChanged="+dateChanged+"]";
	}
}
