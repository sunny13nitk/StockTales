package stocktales.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.basket.allocations.autoAllocation.facades.interfaces.EDRCFacade;
import stocktales.basket.allocations.autoAllocation.facades.pojos.SC_EDRC_Summary;
import stocktales.basket.allocations.autoAllocation.interfaces.EDRCScoreCalcSrv;
import stocktales.basket.allocations.autoAllocation.interfaces.ISrv_FCFSCore;
import stocktales.basket.allocations.autoAllocation.pojos.FCFScore;
import stocktales.basket.allocations.autoAllocation.pojos.ScripEDRCScore;
import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.interfaces.IStgyRebalanceSrv;
import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.pojos.StgyRebalance;
import stocktales.basket.allocations.autoAllocation.valuations.interfaces.SCValuationSrv;
import stocktales.basket.allocations.autoAllocation.valuations.interfaces.SCWtPESrv;
import stocktales.basket.allocations.autoAllocation.valuations.pojos.scWtPE;
import stocktales.helperPOJO.ScValFormPOJO;
import stocktales.predicates.manager.PredicateManager;
import stocktales.repository.SC10YearRepository;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_10YData;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCExistsDB_Srv;

@Controller
@RequestMapping("/test")
public class TestController
{
	@Autowired
	private EDRCScoreCalcSrv edrcSrv;
	
	@Autowired
	private ISCExistsDB_Srv scExSrv;
	
	@Autowired
	private EDRCFacade edrcFacSrv;
	
	@Autowired
	private SC10YearRepository sc10Yrepo;
	
	@Autowired
	private SCWtPESrv scWtPESrv;
	
	@Autowired
	private PredicateManager predMgr;
	
	@Autowired
	private SCValuationSrv scValSrv;
	
	@Autowired
	private ISrv_FCFSCore cfScoreSrv;
	
	@Autowired
	private IStgyRebalanceSrv stgyRblSrv;
	
	@GetMapping("/edrcSrv/{scCode}")
	public String testEDRCSrv(
	        @PathVariable String scCode
	
	)
	{
		ScripEDRCScore edrcScore = edrcSrv.getEDRCforScrip(scCode);
		if (edrcScore != null)
		{
			System.out.println(edrcScore.getScCode());
			System.out.println("Earning Scrore : " + edrcScore.getEarningsDivScore().getResValue());
			System.out.println("ROCE Scrore : " + edrcScore.getReturnRatiosScore().getNettValue());
			if (edrcScore.getCashflowsScore() != null)
			{
				System.out.println("Cash Flow Score : " + edrcScore.getCashflowsScore().getNettValue());
			}
			System.out.println("Nett Score : " + edrcScore.getEdrcScore());
		}
		
		return "success";
	}
	
	@GetMapping("/sectors")
	public String testSectorsList(
	)
	{
		try
		{
			List<String> sectors = scExSrv.getAllSectors();
			for (String sector : sectors)
			{
				System.out.println(sector);
			}
		} catch (EX_General e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "success";
	}
	
	@GetMapping("/edrcFacade")
	public String testEDRCFacade(
	)
	{
		try
		{
			List<SC_EDRC_Summary> edrcList = edrcFacSrv.getEDRCforSCripsList(scExSrv.getAllScripNames());
			for (SC_EDRC_Summary edrc_item : edrcList)
			{
				System.out.println(edrc_item.getScCode() + "|" + edrc_item.getAvWtED() + "|" + edrc_item.getAvWtRR()
				        + "|" + edrc_item.getAvWtCF() + "|" + edrc_item.getEDRC());
			}
		} catch (EX_General e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "success";
	}
	
	@GetMapping("/edrcFacade/{scCode}")
	public String testEDRCFacadeforScrip(
	        @PathVariable String scCode
	)
	{
		
		SC_EDRC_Summary edrc_item = edrcFacSrv.getEDRCforSCrip(scCode);
		if (edrc_item != null)
		{
			System.out.println(edrc_item.getScCode() + "|" + edrc_item.getAvWtED() + "|" + edrc_item.getAvWtRR() + "|"
			        + edrc_item.getAvWtCF() + "|" + edrc_item.getEDRC());
		}
		
		return "success";
	}
	
	@GetMapping("/repo_10Yr/{scCode}")
	public String test10YRepo(
	        @PathVariable String scCode
	)
	{
		if (scCode != null)
		{
			Optional<EN_SC_10YData> ent10Y = sc10Yrepo.findBySCCode(scCode);
			if (ent10Y.isPresent())
			{
				System.out.println(ent10Y.get().getSCCode() + " : " + ent10Y.get().getValR());
			}
		}
		return "success";
	}
	
	@GetMapping("/wtPE/{scCode}")
	public String testwtPE(
	        @PathVariable String scCode
	)
	{
		if (scCode != null)
		{
			scWtPE scripWtPE = scWtPESrv.getWeightedPEforScrip(scCode);
			System.out.println(scripWtPE.getScCode() + " : " + scripWtPE.getWtPE());
		}
		return "success";
	}
	
	@GetMapping("/scVal/{scCode}")
	public String testscValuation(
	        @PathVariable String scCode, Model model
	)
	{
		if (scCode != null)
		{
			ScValFormPOJO scvalPOJO = new ScValFormPOJO(scCode, .7, 0);
			model.addAttribute("scValPOJO", scvalPOJO);
		}
		return "test/forms/scVal";
	}
	
	@GetMapping("/predicates")
	public String testpredicates(
	
	)
	{
		if (predMgr != null)
		{
			List<String> beanNames = predMgr.getPredicateBeanNames();
			for (String string : beanNames)
			{
				System.out.println(string);
			}
		}
		
		return "success";
	}
	
	@GetMapping("/predicates/{predName}")
	public String testpredicatesbyName(
	        @PathVariable String predName
	
	)
	{
		if (predMgr != null)
		{
			
			System.out.println("Bean Name : " + predMgr.getActivePredicateBeanName(predName));
			
		}
		
		return "success";
	}
	
	@GetMapping("/cfyields/{scCode}")
	public String testcfYieldsbyScrip(
	        @PathVariable String scCode
	
	)
	{
		FCFScore fcfScore = cfScoreSrv.getFCFScorebyScrip(scCode);
		if (fcfScore != null)
		{
			System.out.println("Scrip Code:  " + fcfScore.getScCode() + " | FCF Yield | " + fcfScore.getFcfYield()
			        + " | CFO Yield | " + fcfScore.getCfoYield());
			
		}
		return "success";
	}
	
	@GetMapping("/cfyields")
	public String testcfYields(
	
	)
	{
		
		List<String> scrips;
		try
		{
			scrips = scExSrv.getAllScripNames();
			
			for (String scCode : scrips)
			{
				FCFScore fcfScore = cfScoreSrv.getFCFScorebyScrip(scCode);
				if (fcfScore != null)
				{
					System.out.println("Scrip Code:  " + fcfScore.getScCode() + " | FCF Yield | "
					        + fcfScore.getFcfYield() + " | CFO Yield | " + fcfScore.getCfoYield());
					
				}
			}
		} catch (EX_General e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "success";
	}
	
	@GetMapping("/stgyRebal/{stgId}")
	public String testStgyRebalancing(
	        @PathVariable("stgId") String stgId, Model model
	)
	{
		
		int stgyId = new Integer(stgId);
		if (stgyId > 0)
		{
			StgyRebalance rblPoJo = stgyRblSrv.triggerReBalancingforStgy(stgyId);
			if (rblPoJo != null)
			{
				model.addAttribute("rblPOJO", rblPoJo);
			}
		}
		return "success";
	}
	
}
