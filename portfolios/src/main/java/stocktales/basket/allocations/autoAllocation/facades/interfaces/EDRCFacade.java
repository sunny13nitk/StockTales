package stocktales.basket.allocations.autoAllocation.facades.interfaces;

import java.util.List;

import stocktales.basket.allocations.autoAllocation.facades.pojos.SC_EDRC_Summary;

public interface EDRCFacade
{
	public List<SC_EDRC_Summary> getEDRCforSCripsList(
	        List<String> scrips
	);
}
