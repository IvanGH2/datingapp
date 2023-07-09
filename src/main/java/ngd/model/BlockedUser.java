package ngd.model;

import java.io.Serializable;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ngd_blocked_users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockedUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1319899712223477828L;
	
	@Id
	@Column(name="user_id", nullable=false)
	private Integer userId;
	
	@Column(name="blockage_reason", nullable=false)
	private String blockageReason;
	
	@Column(name="date_created", nullable=false)
	private Timestamp dateCreated;
	
	@Override
	public String toString() {
		
		return "User[id=" + this.userId  + ", reason="+ this.blockageReason + ", dateCreated=" + this.dateCreated +"]";
	}

}
