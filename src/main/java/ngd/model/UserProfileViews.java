package ngd.model;

import java.io.Serializable;
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
@Table(name = "ngd_user_profile_views")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileViews implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1994537728350582120L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;
	
	@Column(name = "src_user_id", nullable = false)
	private Integer srcUserId;
	
	@Column(name = "dst_user_id", nullable = false)
	private Integer dstUserId;
	
	@Column(name = "date_viewed")
	private Timestamp dateViewed;

}
