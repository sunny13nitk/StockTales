package stocktales.dataBook.config.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "secFieldsPool")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CfgSectorsFieldsPool
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int    fpid;
	private String sector;
	private String fPoolEntityQName;
	private String getUrl;
}
