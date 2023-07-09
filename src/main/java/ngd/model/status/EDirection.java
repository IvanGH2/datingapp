package ngd.model.status;

import lombok.Getter;

public enum EDirection {
	
	incoming(1),
	outgoing(2);
	
	@Getter
	private Integer value;
	
	private EDirection(Integer val) {
		value = val;
	}

}
