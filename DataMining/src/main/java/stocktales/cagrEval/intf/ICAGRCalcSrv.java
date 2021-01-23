package stocktales.cagrEval.intf;

import java.util.List;

import stocktales.cagrEval.helperPoJo.CAGRResult;
import stocktales.cagrEval.helperPoJo.RollOverDurationsParam;
import stocktales.healthcheck.beanSrv.helperPOJO.NameVal;

public interface ICAGRCalcSrv
{
	public void Initialize(
	        List<String> scrips, boolean calcEndToEndOnly
	) throws Exception;
	
	public void Initialize(
	        int strategyId, boolean calcEndToEndOnly
	) throws Exception;
	
	public void InitializeAdHoc(
	        List<NameVal> scripAllocationsList, boolean calcEndToEndOnly
	);
	
	public void calculateCAGR(
	        RollOverDurationsParam durationsParam
	) throws Exception;
	
	public List<CAGRResult> getCagrResults(
	);
	
}
