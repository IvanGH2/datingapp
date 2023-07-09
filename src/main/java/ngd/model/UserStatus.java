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
@Table(name = "ngd_useractivation_status")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatus {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "userId", nullable = false)
	private String userId;
	
	@Column(name = "user_status", nullable = false)
	private Integer userStatus;
	
	@Column(name = "date_created", nullable = false)
	private Timestamp dateCreted;
	
	@Column(name = "date_changed", nullable = false)
	private Timestamp dateChanged;
}
