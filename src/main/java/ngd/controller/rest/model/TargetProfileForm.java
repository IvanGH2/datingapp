package ngd.controller.rest.model;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ngd.model.status.EUserProfile;


@Getter
@Setter
public class TargetProfileForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1152159110065426558L;
	
	private List<EUserProfile.ERelationshipStatus> relStatuses;
	
	private List<String> countries;
	
	//private String residence;
	
	private String targetGender;
	
	private Integer ageFrom;
	
	private Integer ageTo;
	
	private List<EUserProfile.EEducationLevel> eduLevels;
	
	private List<EUserProfile.EPersonality> personalities;
	
	private List<EUserProfile.EBodyType> bodyTypes;
	
	private List<EUserProfile.EEmploymentStatus> employmentStatuses;
	
	private List<EUserProfile.EChildrenStatus> childrenStatuses;
	
	private Boolean relStatusInclude;
	
	private Boolean eduLevelsInclude;
	
	private Boolean personalitiesInclude;
	
	private Boolean bodyTypesInclude;
	
	private Boolean countriesInclude;
	
	private Boolean employmentStatusInclude;
	
	private Boolean childrenStatusInclude;
	
	
	/*public String getCountries() {
		
		String countries = "";
		for(final String c : this.countries)
			countries = countries + c + ",";
		return countries;
	}*/

}
