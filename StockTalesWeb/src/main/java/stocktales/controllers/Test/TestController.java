package stocktales.controllers.Test;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import stocktales.cagrEval.helperPoJo.CAGRResult;
import stocktales.cagrEval.helperPoJo.RollOverDurationsParam;
import stocktales.cagrEval.helperPoJo.XIRRItems;
import stocktales.cagrEval.helperPoJo.YearsFromTo;
import stocktales.cagrEval.helperPoJo.YearsRollOverResults;
import stocktales.cagrEval.intf.ICAGRCalcSrv;
import stocktales.cagrEval.intf.IRollOverYrs;
import stocktales.controllers.Test.entity.MultiTest;
import stocktales.controllers.Test.pojo.TestMulti;
import stocktales.controllers.Test.repo.RepoMultiTest;
import stocktales.dataBook.helperPojo.scjournal.dbproc.NumandLastEntry;
import stocktales.dataBook.helperPojo.scjournal.dbproc.intf.PlaceHolderLong;
import stocktales.dataBook.helperPojo.scjournal.dbproc.intf.ScJSummary;
import stocktales.dataBook.model.repo.scjournal.RepoScJournal;
import stocktales.healthcheck.intf.IHC_Srv;
import stocktales.healthcheck.model.helperpojo.HCComboResult;
import stocktales.helperPOJO.NameValDouble;
import stocktales.helperPOJO.ScValFormPOJO;
import stocktales.predicates.manager.PredicateManager;
import stocktales.repository.SC10YearRepository;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_10YData;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_BalSheet;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scDataContainer.DAO.types.scDataContainer;
import stocktales.scripsEngine.uploadEngine.scDataContainer.services.interfaces.ISCDataContainerSrv;
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
	
	@Autowired
	private RepoMultiTest repoMultiTest;
	
	@Autowired
	private RepoScJournal repoSCJ;
	
	@Autowired
	private ISCDataContainerSrv scContSrv;
	
	@Autowired
	private IRollOverYrs roySrv;
	
	@Autowired
	private ICAGRCalcSrv cagrCalcSrv;
	
	@Autowired
	private IHC_Srv hcSrv;
	
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
	
	@GetMapping("/multi")
	public String showTestMulti(
	        Model model
	)
	{
		model.addAttribute("multipojo", new TestMulti());
		return "test/testMulti";
	}
	
	@PostMapping("/multi")
	public String handlePostMulti(
	        @ModelAttribute("multipojo") TestMulti multiPojo, Model model
	)
	{
		if (multiPojo != null)
		{
			System.out.println(multiPojo.getTag());
			System.out.println(multiPojo.getOthertag());
			System.out.println(multiPojo.getCatg());
			
			//--Persist in DB
			MultiTest multitest = new MultiTest();
			multitest.setCatg(multiPojo.getCatg());
			
			if (multiPojo.getOthertag() != null)
			{
				multitest.setTag(multiPojo.getTag() + ',' + multiPojo.getOthertag());
			} else
			{
				multitest.setTag(multiPojo.getTag());
			}
			repoMultiTest.save(multitest);
		}
		return "success";
	}
	
	@GetMapping("/multi/tag/{tagtext}")
	public String scanTestMulti(
	        @PathVariable("tagtext") String tagtext, Model model
	)
	{
		if (tagtext != null)
		{
			if (tagtext.trim().length() > 0)
			{
				List<MultiTest> list = repoMultiTest.findAllByTagContainingIgnoreCase(tagtext);
				for (MultiTest multiTest : list)
				{
					System.out.println(multiTest.getTag());
					
					System.out.println(multiTest.getCatg());
				}
			}
		}
		
		return "success";
	}
	
	@GetMapping("/dq/{scCode}")
	public String testdqbyScrip(
	        @PathVariable String scCode
	
	)
	{
		
		if (repoSCJ != null)
		{
			List<Object[]> vals = repoSCJ.findByAsArray("BAJFINANCE");
			if (vals != null)
			{
				if (vals.size() > 0)
				{
					
					NumandLastEntry snippet = new NumandLastEntry();
					snippet.setLastEntryDate((Date) vals.get(0)[0]);
					snippet.setNumEntries((Long) vals.get(0)[1]);
					
					System.out.println("Total Entries - " + snippet.getNumEntries() + " & Last Entry On -  "
					        + snippet.getLastEntryDate());
				}
			}
		}
		return "success";
	}
	
	@GetMapping("/dqGCatg/{scCode}")
	public String testdqGbyScrip(
	        @PathVariable String scCode
	
	)
	{
		
		if (repoSCJ != null)
		{
			List<PlaceHolderLong> vals = repoSCJ.countEntriesByCategory("BAJFINANCE");
			if (vals != null)
			{
				if (vals.size() > 0)
				{
					for (PlaceHolderLong placeHolderLong : vals)
					{
						System.out.println(
						        placeHolderLong.getPlaceholder() + " : Entries - " + placeHolderLong.getNumEntries());
						;
					}
				}
			}
		}
		return "success";
	}
	
	@GetMapping("/dqGTag/{scCode}")
	public String testdqGTbyScrip(
	        @PathVariable String scCode
	
	)
	{
		
		if (repoSCJ != null)
		{
			
		}
		return "success";
	}
	
	@GetMapping("/dq_Summary/{scCode}")
	public String testdqSummarybyScrip(
	        @PathVariable String scCode
	
	)
	{
		
		List<ScJSummary> vals = repoSCJ.getSummaryByScCode("BAJFINANCE");
		if (vals != null)
		{
			if (vals.size() > 0)
			{
				for (ScJSummary summ : vals)
				{
					System.out.println("Categories:" + summ.getNumCatgs());
					System.out.println("Sources:" + summ.getNumSources());
					System.out.println("Effects:" + summ.getNumEffects());
					System.out.println("Tags:" + summ.getNumTags());
					
				}
			}
		}
		
		return "success";
		
	}
	
	@GetMapping("/scload/{scCode}")
	public String loadSCripData(
	        @PathVariable String scCode
	
	)
	{
		if (scCode != null)
		{
			try
			{
				scContSrv.load(scCode);
				scDataContainer scDC = scContSrv.getScDC();
				if (scDC != null)
				{
					if (scDC.getBalSheet_L() != null)
					{
						Optional<EN_SC_BalSheet> balMin = scDC.getBalSheet_L().stream()
						        .min(Comparator.comparing(EN_SC_BalSheet::getYear));
						if (balMin.isPresent())
						{
							int ymin = balMin.get().getYear();
							System.out.println("Min Year : " + ymin);
						}
						
					}
					System.out.println("Scrip loaded!");
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return "success";
	}
	
	@GetMapping("/scripsBysector/{sector}")
	public String listScripsBySector(
	        @PathVariable String sector
	
	)
	{
		if (sector != null)
		{
			try
			{
				List<String> scrips = scExSrv.getAllScripNamesforSector(sector);
				
				for (String string : scrips)
				{
					System.out.println(string);
				}
			} catch (EX_General e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return "success";
		
	}
	
	@GetMapping("/ROY")
	public String showRollOverYears(
	
	)
	{
		if (roySrv != null)
		{
			YearsRollOverResults royRes = roySrv.generateRollOverYrs(2010, 3, 10);
			if (royRes != null)
			{
				System.out.println("Intervals");
				for (YearsFromTo yent : royRes.getRollOverYrs())
				{
					System.out.println(yent.getFrom() + " | " + yent.getTo());
					
				}
				
				System.out.println("End to End Points");
				System.out.println(royRes.getE2eYrs().getFrom() + "|" + royRes.getE2eYrs().getTo());
			}
		}
		
		return "success";
	}
	
	@GetMapping("/cagrsim")
	public String showCAGRCalc(
	)
	{
		List<String> scrips = new ArrayList<String>();
		scrips.add("BAJFINANCE");
		/*scrips.add("ALKYLAMINE");
		scrips.add("ABBOTTINDIA");
		scrips.add("BRITANNIA");*/
		/*	scrips.add("LTI");
			scrips.add("LTTS");*/
		
		if (cagrCalcSrv != null)
		{
			try
			{
				cagrCalcSrv.Initialize(scrips, false);
				cagrCalcSrv.calculateCAGR(new RollOverDurationsParam(2010, 3, 10));
				if (cagrCalcSrv.getCagrResults() != null)
				{
					for (CAGRResult cagrResult : cagrCalcSrv.getCagrResults())
					{
						System.out.println("----- Duration Details ------");
						System.out.println(
						        cagrResult.getDurationH().getFrom() + " : " + cagrResult.getDurationH().getTo());
						System.out.println(cagrResult.getDurationH().getDurationType().toString());
						
						System.out.println("---------- Constituent Details ---------");
						for (XIRRItems xirrItem : cagrResult.getItems())
						{
							System.out.println(xirrItem.getScCode() + " | " + xirrItem.getAllocation() + " | "
							        + xirrItem.getCAGR() + " | " + xirrItem.getWtCAGR());
						}
						
						System.out.println("---------- Summary ---------");
						
						System.out
						        .println("Zero CAGR Allocation Sum : " + cagrResult.getSummary().getSumzeroCAGRAlloc());
						System.out.println("Boost Factor : " + cagrResult.getSummary().getBoostFactor());
						System.out.println("NETT. CAGR ------->>   " + cagrResult.getSummary().getNettCAGR());
					}
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return "success";
	}
	
	@GetMapping("/cagrsim/{stgyId}")
	public String showCAGRCalcforStrategy(
	        @PathVariable String stgyId
	)
	{
		
		if (cagrCalcSrv != null)
		{
			try
			{
				cagrCalcSrv.Initialize(new Integer(stgyId), false);
				cagrCalcSrv.calculateCAGR(new RollOverDurationsParam(2010, 5, 10));
				if (cagrCalcSrv.getCagrResults() != null)
				{
					for (CAGRResult cagrResult : cagrCalcSrv.getCagrResults())
					{
						System.out.println("--------------------------------");
						System.out.println("----- Duration Details ------");
						System.out.println("--------------------------------");
						System.out.println(
						        cagrResult.getDurationH().getFrom() + " : " + cagrResult.getDurationH().getTo());
						System.out.println(cagrResult.getDurationH().getDurationType().toString());
						
						System.out.println("---------- Constituent Details ---------");
						for (XIRRItems xirrItem : cagrResult.getItems())
						{
							System.out.println(xirrItem.getScCode() + " | " + xirrItem.getAllocation() + " | "
							        + xirrItem.getCAGR() + " | " + xirrItem.getWtCAGR());
						}
						
						System.out.println("---------- Summary ---------");
						
						System.out
						        .println("Zero CAGR Allocation Sum : " + cagrResult.getSummary().getSumzeroCAGRAlloc());
						System.out.println("Boost Factor : " + cagrResult.getSummary().getBoostFactor());
						System.out.println("NETT. CAGR ------->>   " + cagrResult.getSummary().getNettCAGR());
						System.out.println("----------------------------------------------------------------");
						System.out.println("NIFTY CAGR ------->>   " + cagrResult.getSummary().getNiftyCAGR());
					}
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return "success";
	}
	
	@GetMapping("/topNEDRC/{numScrips}")
	public String showTopNEDRC(
	        @PathVariable String numScrips
	)
	{
		
		if (edrcFacSrv != null)
		{
			List<NameValDouble> topN   = edrcFacSrv.getTopNED(new Integer(numScrips));
			List<String>        Scrips = new ArrayList<String>();
			for (NameValDouble nameVal : topN)
			{
				Scrips.add(nameVal.getName());
				System.out.println(nameVal.getName() + " - ED Score: " + nameVal.getValue());
			}
			
			try
			{
				cagrCalcSrv.Initialize(Scrips, true);
				cagrCalcSrv.calculateCAGR(new RollOverDurationsParam(2015, 3, 5));
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (cagrCalcSrv.getCagrResults() != null)
			{
				for (CAGRResult cagrResult : cagrCalcSrv.getCagrResults())
				{
					System.out.println("----- Duration Details ------");
					System.out.println(
					        cagrResult.getDurationH().getFrom() + " : " + cagrResult.getDurationH().getTo());
					System.out.println(cagrResult.getDurationH().getDurationType().toString());
					
					System.out.println("---------- Constituent Details ---------");
					for (XIRRItems xirrItem : cagrResult.getItems())
					{
						System.out.println(xirrItem.getScCode() + " | " + xirrItem.getAllocation() + " | "
						        + xirrItem.getCAGR() + " | " + xirrItem.getWtCAGR());
					}
					
					System.out.println("---------- Summary ---------");
					
					System.out.println("Zero CAGR Allocation Sum : " + cagrResult.getSummary().getSumzeroCAGRAlloc());
					System.out.println("Boost Factor : " + cagrResult.getSummary().getBoostFactor());
					System.out.println("NETT. CAGR ------->>   " + cagrResult.getSummary().getNettCAGR());
				}
			}
			
		}
		return "success";
	}
	
	@GetMapping("/shc/{scCode}")
	public String performSCHealthCheck(
	        @PathVariable String scCode
	)
	{
		
		if (hcSrv != null)
		{
			try
			{
				hcSrv.Initialize(scCode);
				hcSrv.processScripHealthCheck();
				List<HCComboResult> results = hcSrv.getResults();
				if (results != null)
				{
					
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return "success";
	}
	
}
