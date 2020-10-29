package stocktales.basket.allocations.autoAllocation.facades.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.basket.allocations.autoAllocation.facades.interfaces.EDRCFacade;
import stocktales.basket.allocations.autoAllocation.facades.pojos.SC_EDRC_Summary;
import stocktales.basket.allocations.autoAllocation.interfaces.EDRCScoreCalcSrv;
import stocktales.basket.allocations.autoAllocation.pojos.ScripEDRCScore;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCExistsDB_Srv;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.types.ScripSector;

@Service
public class EDRCFacadeImpl implements EDRCFacade
{
	@Autowired
	private EDRCScoreCalcSrv edrcSrv;
	
	@Autowired
	private ISCExistsDB_Srv scExSrv;
	
	private List<ScripSector> scripSectors;
	
	@Override
	public List<SC_EDRC_Summary> getEDRCforSCripsList(
	        List<String> scrips
	)
	{
		List<SC_EDRC_Summary> result = null;
		if (scrips != null)
		{
			if (scrips.size() > 0 && edrcSrv != null)
			{
				result = new ArrayList<SC_EDRC_Summary>();
				SC_EDRC_Summary summary = null;
				
				for (String scrip : scrips)
				{
					ScripEDRCScore scEDRC = edrcSrv.getEDRCforScrip(scrip);
					String         sector = null;
					if (scripSectors == null)
					{
						try
						{
							loadScripsandSectors();
						} catch (EX_General e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
					sector = scripSectors.stream().filter(x -> x.getScCode().equals(scrip)).findFirst().get()
					        .getSector();
					
					if (scEDRC != null)
					{
						
						if (scEDRC.getCashflowsScore() != null)
						{
							summary = new SC_EDRC_Summary(scrip, sector,
							        Precision.round(scEDRC.getEarningsDivScore().getResValue(), 1),
							        Precision.round(scEDRC.getReturnRatiosScore().getNettValue(), 1),
							        Precision.round(scEDRC.getCashflowsScore().getNettValue(), 1),
							        Precision.round(scEDRC.getEdrcScore(), 1));
						} else
						{
							summary = new SC_EDRC_Summary(scrip, sector,
							        Precision.round(scEDRC.getEarningsDivScore().getResValue(), 1),
							        Precision.round(scEDRC.getReturnRatiosScore().getNettValue(), 1), 0,
							        Precision.round(scEDRC.getEdrcScore(), 1));
						}
					}
					
					result.add(summary);
				}
			}
		}
		
		return result;
	}
	
	private void loadScripsandSectors(
	) throws EX_General
	{
		
		this.scripSectors = scExSrv.getAllScripSectors();
		
	}
	
}
