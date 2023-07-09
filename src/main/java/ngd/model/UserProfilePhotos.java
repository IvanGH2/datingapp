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
@Table(name = "ngd_user_profile_photos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfilePhotos implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7832151927475392152L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;
	
	@Column(name = "user_id", nullable = false)
	private Integer userId;
	
	@Column(name = "link_to_photo", nullable = false)
	private String linkToPhoto;
	
	@Column(name = "date_created", nullable = false)
	private Timestamp dateCreated;
	
	@Column(name = "date_changed", nullable = false)
	private Timestamp dateChanged;
	
	@Override
	public String toString() {
		return "UserProfilePhotos[id=" + id + " userId="+userId+" linkToPhoto="+linkToPhoto+" dateCreated="+dateCreated
				+" dateChanged="+dateChanged+"]";
	}

}
