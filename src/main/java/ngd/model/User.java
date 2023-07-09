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
@Table(name = "ngd_user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

	private static final long serialVersionUID = 7733971846950254915L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "psw", nullable = false)
	private String password;

	@Column(name = "email", nullable = false)
	private String email;
	
	@Column(name = "user_role", nullable = false)
	private Integer userRole;

	@Column(name = "active", nullable = false)
	private Boolean active;
	
	@Column(name = "gender", nullable = false)
	private String gender;
	
	@Column(name = "target_gender", nullable = false)
	private String targetGender;
	
	@Column(name = "age", nullable = false)
	private Integer age;
	
	@Column(name = "date_created", nullable = false)
	private Timestamp dateCreated;
	
	@Column(name = "date_changed", nullable = false)
	private Timestamp dateChanged;
	
	@Column(name = "last_activity", nullable = true)
	private Timestamp lastActivity;
	
	@Column(name = "ip", nullable = true)
	private String ip;

	@Override
	public String toString() {
		
		return "User[id=" + this.id  + ", username="+ this.username + ", password=" + this.password + 
				", email=" + this.email + ", user role=" + this.userRole + ", active=" + this.active + " ip="+this.ip+"]";
	}

}
