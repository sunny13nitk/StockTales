package stocktales.basket.allocations.autoAllocation.valuations.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.basket.allocations.autoAllocation.interfaces.EDRCScoreCalcSrv;
import stocktales.basket.allocations.autoAllocation.valuations.interfaces.SCValuationSrv;
import stocktales.basket.allocations.autoAllocation.valuations.pojos.ScValuation;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.implementations.SCExistsDB_Srv;

@Service
public class SCValuationSrvImpl implements SCValuationSrv
{
	@Autowired
	private EDRCScoreCalcSrv edrcSrv;
	
	@Autowired
	private SCExistsDB_Srv scDBSrv;
	
	@Override
	public ScValuation getValuationforScrip(
	        String scCode, double CMP, double MoS
	)
	{
		
		ScValuation scValuation = null;
		if (MoS == 0)
		{
			MoS = 1;
		}
		//Get the TTM Eps from SCGenQ
		
		//Get the EDRC Score from edrcSrv
		
		// Get the weighted PE from SCWtPE Srv
		
		//Adjust EDRC Score for MoS
		
		//Calculate 5Yr Henceforth Price
		
		//Calculate 5Yr Price CAGR
		
		return scValuation;
	}
	
}
