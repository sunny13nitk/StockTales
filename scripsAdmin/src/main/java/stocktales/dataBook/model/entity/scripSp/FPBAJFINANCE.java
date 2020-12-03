package stocktales.dataBook.model.entity.scripSp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.dataBook.model.pojo.PJIntervalSimple;

@Entity
@Table(name = "FPBAJFINANCE")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FPBAJFINANCE extends PJIntervalSimple
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long   id;
	private double bhflaum;
	private double bhflopextonii;
	private double bfslpat;
	private double cftotal;
	private int    totalloc;
	private int    ruralloc;
	private int    posl;         //Point of Sales in Lakhs
	private double emicardm;     //Digital EMI cards  CIF in M
	private double coemicardm;   //Co Branded EMI Cards in M
	private double mobikwikm;    //Mobikkwik users in M
	
	private double cftotalm;     //Total Franchise
	private double xsellm;       // xSell franchise
	private double bestm;        //Non delinquent best customers
	private double perbestxsell; //Percentage Best Cross Sell of Total Franchise
	private double newcustomers; //New Customers Onboarded
	
}
