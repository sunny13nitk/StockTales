package stocktales.basket.allocations.autoAllocation.facades.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import stocktales.basket.allocations.autoAllocation.facades.interfaces.EDRCFacade;
import stocktales.basket.allocations.autoAllocation.facades.pojos.SC_EDRC_Summary;
import stocktales.basket.allocations.autoAllocation.interfaces.EDRCScoreCalcSrv;
import stocktales.basket.allocations.autoAllocation.pojos.ScripEDRCScore;
import stocktales.basket.allocations.config.pojos.StrengthWeights;
import stocktales.basket.allocations.config.pojos.ValSoothingSS;
import stocktales.basket.allocations.config.services.CLRConfig;
import stocktales.repository.SC10YearRepository;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCExistsDB_Srv;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.types.ScripSector;

@Service
public class EDRCFacadeImpl implements EDRCFacade
{
	@Autowired
	private EDRCScoreCalcSrv edrcSrv;
	
	@Autowired
	private SC10YearRepository sc10Yrepo;
	
	@Autowired
	private ISCExistsDB_Srv scExSrv;
	
	@Autowired
	private StrengthWeights strWts;
	
	@Autowired
	private CLRConfig clrConfig;
	
	private List<ScripSector> scripSectors;
	
	@Value("${valuationSoothingSS}")
	private boolean isValuationSoothingEnabled;
	
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
						double strengthWt = Precision
						        .round((sc10Yrepo.findBySCCode(scrip).get().getValR() * strWts.getValR()
						                + scEDRC.getEdrcScore() * strWts.getEDRC()), 1);
						
						if (scEDRC.getCashflowsScore() != null)
						{
							summary = new SC_EDRC_Summary(scrip, sector,
							        Precision.round(scEDRC.getEarningsDivScore().getResValue(), 1),
							        Precision.round(scEDRC.getReturnRatiosScore().getNettValue(), 1),
							        Precision.round(scEDRC.getCashflowsScore().getNettValue(), 1),
							        Precision.round(scEDRC.getEdrcScore(), 1),
							        Precision.round(sc10Yrepo.findBySCCode(scrip).get().getValR(), 1),
							        Precision.round(sc10Yrepo.findBySCCode(scrip).get().getCFOPATR(), 1), strengthWt);
						} else
						{
							summary = new SC_EDRC_Summary(scrip, sector,
							        Precision.round(scEDRC.getEarningsDivScore().getResValue(), 1),
							        Precision.round(scEDRC.getReturnRatiosScore().getNettValue(), 1), 0,
							        Precision.round(scEDRC.getEdrcScore(), 1),
							        Precision.round(sc10Yrepo.findBySCCode(scrip).get().getValR(), 1),
							        Precision.round(sc10Yrepo.findBySCCode(scrip).get().getCFOPATR(), 1), strengthWt);
						}
					}
					
					/*
					 * If Valuation Soothing of Strength Score is Active - Sooth out the scores
					 */
					
					if (isValuationSoothingEnabled)
					{
						//Scan for Right Valuation Zone
						double                  cvalR         = summary.getValR();
						Optional<ValSoothingSS> currValConfig = this.clrConfig.getValSoothConfigL().stream()
						        .filter(x -> x.getMinm() <= cvalR && x.getMaxm() >= cvalR).findFirst();
						if (currValConfig.isPresent())
						{
							summary.setStrengthScore(
							        Precision.round((summary.getStrengthScore() * currValConfig.get().getSf()), 1));
							
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
	
	@Override
	public List<SC_EDRC_Summary> getEDRCforSCripsList(
	        List<String> scrips, Predicate<? extends SC_EDRC_Summary> predicate
	)
	{
		List<SC_EDRC_Summary> result    = null;
		List<SC_EDRC_Summary> filteredR = null;
		if (predicate != null && scrips != null)
		{
			result = this.getEDRCforSCripsList(scrips);
		}
		if (result != null)
		{
			filteredR = result.stream().filter((Predicate<? super SC_EDRC_Summary>) predicate)
			        .collect(Collectors.toList());
			
			return filteredR;
		}
		// TODO Auto-generated method stub
		return filteredR;
	}
	
}
