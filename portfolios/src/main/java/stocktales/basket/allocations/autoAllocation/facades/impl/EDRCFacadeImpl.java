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
import stocktales.basket.allocations.config.pojos.CalibrationItem;
import stocktales.basket.allocations.config.pojos.StrengthWeights;
import stocktales.basket.allocations.config.services.CLRConfig;
import stocktales.repository.SC10YearRepository;
import stocktales.repository.TrendsRepository;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_10YData;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_Trends;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCExistsDB_Srv;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.types.ScripSector;
import stocktales.services.interfaces.ScripService;

@Service
public class EDRCFacadeImpl implements EDRCFacade
{
	@Autowired
	private EDRCScoreCalcSrv edrcSrv;
	
	@Autowired
	private ScripService scSrv;
	
	@Autowired
	private SC10YearRepository sc10Yrepo;
	
	@Autowired
	private ISCExistsDB_Srv scExSrv;
	
	@Autowired
	private StrengthWeights strWts;
	
	@Autowired
	private CLRConfig clrConfig;
	
	@Autowired
	private TrendsRepository repoTrends;
	
	@Autowired
	private SC10YearRepository repo10Yr;
	
	@Value("${trendValPeriod}")
	private String trendValPeriod;
	
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
						double                    cvalR         = summary.getValR();
						Optional<CalibrationItem> currValConfig = this.clrConfig.getValSoothConfigL().stream()
						        .filter(x -> x.getMinm() <= cvalR && x.getMaxm() >= cvalR).findFirst();
						if (currValConfig.isPresent())
						{
							summary.setStrengthScore(
							        Precision.round((summary.getStrengthScore() * currValConfig.get().getSf()), 1));
							
						}
						
					}
					
					/*
					 * Re-calibrate for Un-pledged Promoter Holding, Working Capital Cycle, Financial Strength 
					 * (Interest + Dep.) /PAT - Only for Non financial Scrips
					 */
					
					if (!scSrv.isScripBelongingToFinancialSector(scrip))
					
					{
						
						//UPH SS Calibration
						if (clrConfig.getUPHConfigL() != null)
						{
							try
							{
								double UPH = scExSrv.Get_ScripExisting_DB(scrip).getUPH();
								
								Optional<CalibrationItem> currUPHConfig = this.clrConfig.getUPHConfigL().stream()
								        .filter(x -> x.getMinm() <= UPH && x.getMaxm() >= UPH).findFirst();
								if (currUPHConfig.isPresent())
								{
									summary.setStrengthScore(
									        Precision.round((summary.getStrengthScore() * currUPHConfig.get().getSf()),
									                1));
									
								}
							} catch (EX_General e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
						
						//WC Cycle Calibration
						if (clrConfig.getWCConfigL() != null)
						{
							
							Optional<EN_SC_Trends> trendEntO = repoTrends.findBySCCodeAndPeriod(scrip, trendValPeriod);
							if (trendEntO.isPresent())
							{
								EN_SC_Trends trendEnt = trendEntO.get();
								double       WCCycle  = trendEnt.getWCCAvg();
								
								Optional<CalibrationItem> currWCConfig = this.clrConfig.getWCConfigL().stream()
								        .filter(x -> x.getMinm() <= WCCycle && x.getMaxm() >= WCCycle).findFirst();
								if (currWCConfig.isPresent())
								{
									summary.setStrengthScore(
									        Precision.round((summary.getStrengthScore() * currWCConfig.get().getSf()),
									                1));
									
								}
							}
							
						}
						
						//IDBP Cycle Calibration
						if (clrConfig.getIDBPConfigL() != null)
						{
							
							Optional<EN_SC_Trends> trendEntO = repoTrends.findBySCCodeAndPeriod(scrip, trendValPeriod);
							if (trendEntO.isPresent())
							{
								EN_SC_Trends trendEnt = trendEntO.get();
								double       IDBP     = trendEnt.getFViabAvg();
								
								Optional<CalibrationItem> currIDBPConfig = this.clrConfig.getIDBPConfigL().stream()
								        .filter(x -> x.getMinm() <= IDBP && x.getMaxm() >= IDBP).findFirst();
								if (currIDBPConfig.isPresent())
								{
									summary.setStrengthScore(
									        Precision.round((summary.getStrengthScore() * currIDBPConfig.get().getSf()),
									                1));
									
								}
							}
							
						}
						
						//CFO/PAT Calibration
						if (clrConfig.getCFOPATConfigL() != null)
						{
							
							Optional<EN_SC_10YData> sc10YEntO = repo10Yr.findBySCCode(scrip);
							if (sc10YEntO.isPresent())
							{
								EN_SC_10YData sc10YEnt = sc10YEntO.get();
								
								double CFOPATAdh = sc10YEnt.getCFOPATR();
								
								Optional<CalibrationItem> currCFOPATConfig = this.clrConfig.getCFOPATConfigL().stream()
								        .filter(x -> x.getMinm() <= CFOPATAdh && x.getMaxm() >= CFOPATAdh).findFirst();
								if (currCFOPATConfig.isPresent())
								{
									summary.setStrengthScore(
									        Precision.round(
									                (summary.getStrengthScore() * currCFOPATConfig.get().getSf()), 1));
									
								}
							}
							
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
