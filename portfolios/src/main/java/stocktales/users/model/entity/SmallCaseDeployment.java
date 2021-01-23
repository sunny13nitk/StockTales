package stocktales.users.model.entity;

import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "scdeployments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class SmallCaseDeployment
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long dpid;
	
	private Date   date;   //Date of Deployment
	private double amount; //Amount Deployed
	
	@ManyToOne(cascade =
	{ CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "flid")
	private FundLine fundline;
}
