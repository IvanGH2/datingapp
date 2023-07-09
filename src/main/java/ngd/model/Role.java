package ngd.model;

import java.io.Serializable;

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

@Getter
@Setter
@Entity
@Table(name = "ngd_role")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role implements Serializable{

	private static final long serialVersionUID = 3168315679935716017L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;
	

	@Column(name = "role_name", nullable = false)
	String roleName;
	
	@Override
	public String toString() {
		return "User role [id=" + this.id + ", role name="+ this.roleName;
	}
}
