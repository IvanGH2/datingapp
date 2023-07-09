package ngd.model.status;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public enum EUserRole {
	
	
	USER(1),
	MOD(2),
	ADMIN(3);
	
	@Getter
	private Integer value;
	
	private EUserRole(Integer val) {
		value = val;
	}
	private static Map<Integer, EUserRole> valuesMap = new HashMap<>();
	
	 public  static EUserRole getObject(Integer val) {
		 if(valuesMap.isEmpty()) {
				for(EUserRole e : EUserRole.values()){
					valuesMap.put(e.value, e);
				}
			}
			return valuesMap.get(val);
	 }
}
