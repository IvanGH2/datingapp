package ngd.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ngd_user_messages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMessage {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;
	
	@Column(name = "src_user_id", nullable = false)
	private Integer srcUserId;
	
	@Column(name = "dst_user_id", nullable = false)
	private Integer dstUserId;
	
	@Column(name = "msg_txt", nullable = false)
	private String msgTxt;
	
	@Column(name = "msg_flag", nullable = false)
	private Boolean msgFlag;
	
	@Column(name = "msg_viewed", nullable = false)
	private Boolean msgViewed;
	
	@Column(name = "date_created", nullable = false)
	private Timestamp dateCreated;
	
	@Column(name = "date_changed", nullable = false)
	private Timestamp dateChanged;
	
	@Column(name = "reserved", nullable = true)
	private String reserved;

}
