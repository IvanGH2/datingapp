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
@Table(name = "ngd_user_target_profile")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetProfile {
	@Id
	@Column(name = "user_id", nullable = false)
	private Integer userId;
	
	@Column(name = "age_from", nullable = true)
	private Integer ageFrom;
	
	@Column(name = "age_to", nullable = true)
	private Integer ageTo;
	
	@Column(name = "gender")
	private String gender;
	
	@Column(name = "rel_status", nullable = true)
	private String relStatus;
	
	@Column(name = "edu_level", nullable = true)
	private String eduLevel;
	
	@Column(name = "body_type", nullable = true)
	private String bodyType;
	
	@Column(name = "personality", nullable = true)
	private String personalityType;
	
	@Column(name = "children_status", nullable = true)
	private String childrenStatus;
	
	@Column(name = "children_status_include", nullable = true)
	private Boolean childrenStatusInclude;
	
	@Column(name = "countries", nullable = true)
	private String countries;
	
	@Column(name = "employment_status", nullable = true)
	private String employmentStatus;
	
	@Column(name = "employment_status_include", nullable = true)
	private Boolean employmentStatusInclude;
	
	@Column(name = "rel_status_include", nullable =true)
	private Boolean relStatusInclude;
	
	@Column(name = "edu_level_include", nullable = true)
	private Boolean eduLevelInclude;
	
	@Column(name = "body_type_include", nullable = true)
	private Boolean bodyTypeInclude;
	
	@Column(name = "country_include", nullable = true)
	private Boolean countryInclude;
	
	@Column(name = "personality_include", nullable = true)
	private Boolean personalityTypeInclude;
	
	@Column(name = "date_created", nullable = false)
	private Timestamp dateCreated;
	
	@Column(name = "date_changed", nullable = false)
	private Timestamp dateChanged;
	
	@Override
	public String toString() {
		return "TargetProfile[userId="+userId+" relStatus="+relStatus+ " gender= " + gender + " bodyType="+bodyType+" personality="+personalityType
				+" eduLevel="+eduLevel+" countries="+countries+" relStatusInclude="+relStatusInclude+" eduLevelInclude="+eduLevelInclude
				+" bodyTypeInclude="+bodyTypeInclude+" personalityTypeInclude="+personalityTypeInclude + " dateCreated="+dateCreated+" dateChanged="+dateChanged+"]";
	}
}
