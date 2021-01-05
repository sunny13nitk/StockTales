package stocktales.healthcheck.beanSrv.intf;

import java.util.List;

import stocktales.healthcheck.beanSrv.helperPOJO.NameVal;
import stocktales.healthcheck.intf.IHC_GetEvaluationResults;

/*
 * Carve Out service for Avg. Sales Growth
 */
public interface IAvgSalesGrowth extends IHC_GetEvaluationResults
{
	public List<NameVal> getSalesTrends(
	);
	
	public List<NameVal> getPositiveYrs(
	);
	
	public List<NameVal> getNegativeYrs(
	);
}
