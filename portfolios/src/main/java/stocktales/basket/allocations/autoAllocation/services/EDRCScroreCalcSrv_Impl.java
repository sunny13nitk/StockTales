package stocktales.basket.allocations.autoAllocation.services;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import stocktales.basket.allocations.autoAllocation.interfaces.EDRCScoreCalcSrv;
import stocktales.basket.allocations.autoAllocation.pojos.DurVWv;
import stocktales.basket.allocations.autoAllocation.pojos.DurVWvNett;
import stocktales.basket.allocations.autoAllocation.pojos.DurVWvNett_ED;
import stocktales.basket.allocations.autoAllocation.pojos.ScripEDRCScore;
import stocktales.basket.allocations.config.pojos.AllocationWeights;
import stocktales.basket.allocations.config.pojos.DurationWeights;
import stocktales.repository.TrendsRepository;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_GeneralQ;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_Trends;
import stocktales.services.interfaces.ScripService;

@Service
public class EDRCScroreCalcSrv_Impl implements EDRCScoreCalcSrv
{
	@Autowired
	private TrendsRepository repoSCTrends;
	
	@Autowired
	private DurationWeights cfDurWts;
	
	@Autowired
	private AllocationWeights cfAllocWts;
	
	@Autowired
	private ScripService scripSrv;
	
	@Override
	public ScripEDRCScore getEDRCforScrip(
	        String scCode
	)
	{
		ScripEDRCScore scEDRCscore = new ScripEDRCScore();
		scEDRCscore.setScCode(scCode);
		scEDRCscore.setEarningsDivScore(new DurVWvNett_ED());
		scEDRCscore.setReturnRatiosScore(new DurVWvNett());
		scEDRCscore.setCashflowsScore(new DurVWvNett());
		
		try
		{
			
			//Get the trends
			List<EN_SC_Trends> trends = repoSCTrends.findAllBySCCodeIgnoreCase(scCode);
			if (trends != null)
			{
				
				if (trends.size() > 1)
				{
					//Get the weights
					if (cfAllocWts != null && cfDurWts != null)
					{
						
						double         acc_eps          = 0;
						double         acc_rr           = 0;
						double         acc_cfo          = 0;
						boolean        isFinancialScrip = scripSrv.isScripBelongingToFinancialSector(scCode);
						EN_SC_GeneralQ scGen            = scripSrv.getScripHeader(scCode);
						scEDRCscore.getEarningsDivScore().setDivYield(scGen.getDivYield());
						for (EN_SC_Trends trend : trends)
						{
							//Get the Weights by Year in curr trend loop pass
							String cfDurWtsMName  = "getWt" + trend.getPeriod();
							Method methodcfDurWts = ReflectionUtils.findMethod(DurationWeights.class, cfDurWtsMName);
							if (methodcfDurWts != null)
							{
								double wtvalue = (double) ReflectionUtils.invokeMethod(methodcfDurWts, cfDurWts);
								if (wtvalue > 0)
								{
									//add to weight counters - adjust for missing years if any for a scrip
									acc_eps += wtvalue;
									acc_rr  += wtvalue;
									
									scEDRCscore.getEarningsDivScore().getDurItems().add(
									        new DurVWv(trend.getPeriod(), trend.getPATGR(),
									                trend.getPATGR() * wtvalue));
									
									if (!isFinancialScrip) //ROCE
									{
										scEDRCscore.getReturnRatiosScore().getDurItems().add(
										        new DurVWv(trend.getPeriod(), trend.getROCEAvg(),
										                trend.getROCEAvg() * wtvalue));
										
										scEDRCscore.getCashflowsScore().getDurItems().add(
										        new DurVWv(trend.getPeriod(), trend.getFCF_CFO_Avg(),
										                trend.getFCF_CFO_Avg() * wtvalue));
										
										acc_cfo += wtvalue;
									}
									
									else //ROE
									{
										scEDRCscore.getReturnRatiosScore().getDurItems().add(
										        new DurVWv(trend.getPeriod(), trend.getROEAvg(),
										                trend.getROEAvg() * wtvalue));
									}
									
								}
							}
							
						} //Loop closes for Trends read scan
						
						// Consolidate
						
						//----------------  EPS DIV -------------------------------------------------
						double residualwt_eps = 1 - acc_eps;
						double wtFacEPS       = 1;
						if (residualwt_eps > 0)
						{
							wtFacEPS = 1 / acc_eps;
						}
						
						scEDRCscore.getEarningsDivScore().setNettValue(scEDRCscore.getEarningsDivScore().getDurItems()
						        .stream().mapToDouble(DurVWv::getWv).sum() * wtFacEPS);
						scEDRCscore.getEarningsDivScore().setResValue(scEDRCscore.getEarningsDivScore().getNettValue()
						        + scEDRCscore.getEarningsDivScore().getDivYield());
						
						//----------------  ROCE/ROE  -------------------------------------------------
						double residualwt_rr = 1 - acc_rr;
						double wtFacRR       = 1;
						if (residualwt_rr > 0)
						{
							wtFacRR = 1 / acc_eps;
						}
						
						scEDRCscore.getReturnRatiosScore().setNettValue(scEDRCscore.getReturnRatiosScore().getDurItems()
						        .stream().mapToDouble(DurVWv::getWv).sum() * wtFacRR);
						
						//----------------  CFO -------------------------------------------------
						double nullCFERDC = 1;
						if (isFinancialScrip)
						{
							scEDRCscore.setCashflowsScore(null);
							nullCFERDC = 1 / (cfAllocWts.getWtED() + cfAllocWts.getWtRR());
							
						} else
						{
							double residualwt_cfo = 1 - acc_cfo;
							double wtFacCFO       = 1;
							if (residualwt_cfo > 0)
							{
								wtFacCFO = 1 / acc_cfo;
							}
							
							scEDRCscore.getCashflowsScore().setNettValue(scEDRCscore.getCashflowsScore().getDurItems()
							        .stream().mapToDouble(DurVWv::getWv).sum() * wtFacCFO);
						}
						
						//Calculate Final EDRC Scores
						if (nullCFERDC > 1) //Financial Scrip
						{
							scEDRCscore.setEdrcScore(
							        (scEDRCscore.getEarningsDivScore().getResValue() * cfAllocWts.getWtED()
							                + scEDRCscore.getReturnRatiosScore().getNettValue() * cfAllocWts.getWtRR())
							                * nullCFERDC);
						} else //Non Financial Scrip
						{
							scEDRCscore
							        .setEdrcScore(scEDRCscore.getEarningsDivScore().getResValue() * cfAllocWts.getWtED()
							                + scEDRCscore.getReturnRatiosScore().getNettValue() * cfAllocWts.getWtRR()
							                + scEDRCscore.getCashflowsScore().getNettValue() * cfAllocWts.getWtCF());
						}
					}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// return
		return scEDRCscore;
		
	}
	
}
