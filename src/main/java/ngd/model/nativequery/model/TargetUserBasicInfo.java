package ngd.model.nativequery.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetUserBasicInfo {
	
	
	private String gender;
	
	private Integer ageFrom;
	
	private Integer ageTo;

}
