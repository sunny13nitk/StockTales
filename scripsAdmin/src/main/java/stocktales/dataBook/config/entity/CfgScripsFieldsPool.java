package stocktales.dataBook.config.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Scrip Specific Field Pools
 */
@Entity
@Table(name = "scripsFieldsPool")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CfgScripsFieldsPool
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int    fpid;
	@NotNull
	private String sccode;
	@NotNull
	private String fPoolEntityQName; //Qualified Entity Pojo for Field Pool
	
	@NotNull
	private String srvbean;
}
