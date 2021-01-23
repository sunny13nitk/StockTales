package stocktales.users.model.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "userspf")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class UserPF
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long   uid;
	private String uname;
	private double walletbalance;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
	private List<FundLine> fundlines = new ArrayList<FundLine>();
	
}
