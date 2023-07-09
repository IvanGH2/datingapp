package ngd.model.nativequery.model;

import java.io.Serializable;
import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlaggedMessageInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5324205637760945112L;

	private Integer senderId;
	
	private String senderUsername;
	
	private Integer msgId;
	
	private String msg;
	
	private Date msgDate;
	
		

}
