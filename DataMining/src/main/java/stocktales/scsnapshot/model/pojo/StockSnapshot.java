package stocktales.scsnapshot.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.basket.allocations.autoAllocation.facades.pojos.SC_EDRC_Summary;
import stocktales.basket.allocations.autoAllocation.valuations.pojos.ScValuation;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockSnapshot
{
	private StockQuoteBasic quoteBasic;
	
	private ScValuation targetPrice;
	
	private SC_EDRC_Summary summary;
	
	private StockMCapLast4QData last4QData;
	
	private StockFundamentals fundamentals;
	
	private StockTrends trends;
}
