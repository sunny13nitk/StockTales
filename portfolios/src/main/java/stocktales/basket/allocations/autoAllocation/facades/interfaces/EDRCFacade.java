package stocktales.basket.allocations.autoAllocation.facades.interfaces;

import java.util.List;
import java.util.function.Predicate;

import stocktales.basket.allocations.autoAllocation.facades.pojos.SC_EDRC_Summary;

public interface EDRCFacade
{
	public List<SC_EDRC_Summary> getEDRCforSCripsList(
	        List<String> scrips
	);
	
	public List<SC_EDRC_Summary> getEDRCforSCripsList(
	        List<String> scrips, Predicate<? extends SC_EDRC_Summary> predicate
	);
	
	public SC_EDRC_Summary getEDRCforSCrip(
	        String scCode
	);
}
