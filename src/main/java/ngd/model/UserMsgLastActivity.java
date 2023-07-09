package ngd.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ngd_user_msg_last")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(LastMsgCompositeKey.class)
public class UserMsgLastActivity implements Serializable{	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6143892127208860351L;
	
	
	@Column(name = "msg_id", nullable = false)
	private Integer msgId;
	
	@Id
	@Column(name = "src_user_id", nullable = false)
	private Integer srcUserId;
	
	@Id
	@Column(name = "dst_user_id", nullable = false)
	private Integer dstUserId;

}
