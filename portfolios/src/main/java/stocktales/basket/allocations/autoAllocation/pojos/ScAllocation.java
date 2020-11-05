package stocktales.basket.allocations.autoAllocation.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.basket.allocations.autoAllocation.valuations.pojos.ScValuation;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScAllocation extends ScValuation
{
	private double allocation;
}
