package stocktales.users.model.entity;

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
@Table(name = "holdings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Holdings
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long hid;
	
	private String sccode;
	private double allocamnt;
	private double depamnt;
	private int    numunits;
	private double ppu;
	
	@ManyToOne(cascade =
	{ CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "flid")
	private FundLine fundline;
}
