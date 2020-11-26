package stocktales.dataBook.model.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.dataBook.enums.EnumUnit;
import stocktales.dataBook.model.pojo.PJInterval;

@Entity
@Table(name = "FPFinancialSector")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FPFinancialSector extends PJInterval
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long     id;
	private String   scCode;
	@Enumerated(EnumType.STRING)
	private EnumUnit unit;
	private double   totalIncome;
	private double   feeCommIncome;
	private double   feeCommExpenses;
	private double   provisions;
	private double   GNPA;
	private double   NNPA;
	private double   CAR;
	private double   tier1CAR;
	private double   PCR;
	private double   deposits;
	private double   retailDepPercentage;
	private double   corpDepPercentage;
	private double   opexToNII;
	
}
