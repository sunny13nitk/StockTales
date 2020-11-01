package stocktales.basket.allocations.autoAllocation.valuations.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScValuation
{
	private String scCode;
	private double edrcScore;
	private double MoS;
	private double currPE;
	private double weightedPE;
	private double price5Yr;
	private double ret5YrCAGR;
	
}
