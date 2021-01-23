package stocktales.users.model.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.users.enums.EnumFLStatus;
import stocktales.users.enums.EnumFLType;

@Entity
@Table(name = "fundlines")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class FundLine
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long         flid;
	@Enumerated(EnumType.STRING)
	private EnumFLStatus status;
	private double       casbalance;
	private double       depbalance;
	@Enumerated(EnumType.STRING)
	private EnumFLType   fltype;
	private String       sclink;    //Small Case Link
	private int          stid;      //Strategy Id
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "fundline")
	private List<SmallCaseDeployment> scdeployments = new ArrayList<SmallCaseDeployment>();
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "fundline")
	private List<Holdings> holdings = new ArrayList<Holdings>();
	
	@ManyToOne(cascade =
	{ CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "uid")
	private UserPF user;
	
}
