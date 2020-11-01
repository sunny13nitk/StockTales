package stocktales.predicates;

import org.springframework.stereotype.Component;

import stocktales.basket.allocations.autoAllocation.facades.pojos.SC_EDRC_Summary;

@Component("CCPPredicate")
public class ScripsSummaryPredicate extends GenericSCEDRCSummaryPredicate
{
	
	@Override
	public boolean test(
	        SC_EDRC_Summary x
	)
	{
		
		return (x.getAvWtED() > 15 && x.getAvWtRR() >= 18 && (x.getAvWtCF() == 0 || x.getAvWtCF() > 40)
		        && (x.getValR() > 7 && x.getValR() < 45));
		
	}
	
	public String getNotes(
	)
	{
		return "(Earnings & Div. Score > 15) AND (Return Ratios Score > 18) AND (Cash Flow Score (Non Financials) > 40) AND (Value Ratio Between 7  and 45) )";
	}
	
}
