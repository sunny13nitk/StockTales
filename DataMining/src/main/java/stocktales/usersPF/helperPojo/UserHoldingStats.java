package stocktales.usersPF.helperPojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.usersPF.model.Holding;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserHoldingStats extends Holding
{
	private double totalAlloc;
	private String totalAllocS;
	private double allocPer;
	private String allocPerS;
}
