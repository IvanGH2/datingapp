package ngd.model.status;

import lombok.Getter;

public enum ETargetGender {
	
	FEMALE(1),
	MALE(2),
	BOTH(3);
	
	@Getter
	private Integer value;
	
	private ETargetGender(Integer val) {
		value = val;
	}

}
