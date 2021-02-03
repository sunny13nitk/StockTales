package stocktales.usersPF.model;

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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "userpfconfig")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPFConfig
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long cid;
	
	@NotNull
	private String username; //To be Injected via Session Bean
	
	@Min(value = 0)
	private double cashbalance; //Initial Balance
	
	@Min(value = 0)
	private double avgannualcorpus; //Amount User can annualy deploy
	
	@Min(value = 0)
	private int mincagr; //Min Expected CAGR to trigger deployments
	
	@Min(value = 0)
	private int yrsrollover; //Years avg annual corpus can be rolled over to get # Units 
	
	@Min(value = 0)
	@Max(value = 25)
	private int maxposbreach; //Percentage Breach on Positive side - Over min CAGR for Position sizing
	
	@NotNull
	private String brokercode; //Broker Code 
	
	private boolean confirmb4deploy; //Confirm Amount always before deployment
	
	private String brokerurl;
	
	/*
	 * Any User(s) can subscribe to Many Strategies
	 * One to Many Relation
	 */
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "userpfconfig")
	private List<UserStrategy> userStrategies = new ArrayList<UserStrategy>();
	
	public UserPFConfig subscribeToStrategy(
	        UserStrategy usStgy
	)
	{
		usStgy.setUserpfconfig(this);
		this.userStrategies.add(usStgy);
		return this;
	}
	
}
