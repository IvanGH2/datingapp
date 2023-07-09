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
@Table(name = "ngd_user_activation_link")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationLink implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7400564781721252791L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "userid", nullable = false)
	private Integer userId;
	
	@Column(name = "activation_link", nullable = false)
	private String activationLink;
	
	@Column(name = "link_consumed", nullable = false)
	private Boolean linkConsumed;
	
	@Column(name = "time_created", nullable = false)
	private Timestamp timeCreated;
	
	@Column(name = "time_expires", nullable = false)
	private Timestamp timeExpires;
	
}
