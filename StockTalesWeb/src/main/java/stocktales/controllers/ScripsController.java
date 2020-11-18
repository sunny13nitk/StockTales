package stocktales.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.basket.allocations.autoAllocation.facades.interfaces.EDRCFacade;
import stocktales.basket.allocations.autoAllocation.valuations.interfaces.SCValuationSrv;
import stocktales.helperPOJO.ScValFormPOJO;
import stocktales.predicates.GenericSCEDRCSummaryPredicate;
import stocktales.predicates.manager.PredicateManagerImpl;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCExistsDB_Srv;

@Controller
@RequestMapping("/scrips")
public class ScripsController
{
	@Autowired
	private EDRCFacade edrcFacSrv;
	
	@Autowired
	private ISCExistsDB_Srv scExSrv;
	
	@Autowired
	private PredicateManagerImpl predManager;
	
	@Autowired
	private SCValuationSrv scValSrv;
	
	@GetMapping("/edrc")
	public String showScripsEDRC(
	        Model model
	)
	{
		try
		{
			model.addAttribute("EDRCSummary", edrcFacSrv.getEDRCforSCripsList(scExSrv.getAllScripNames()));
		} catch (EX_General e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "reports/EDRCSummary";
	}
	
	@GetMapping("/edrc/myFilter")
	public String showScripsEDRCmyFilter(
	        Model model
	)
	{
		try
		{
			GenericSCEDRCSummaryPredicate predBean = predManager.getActivePredicateBean();
			if (predBean != null)
			{
				model.addAttribute("criteria", predBean.getNotes());
				
				model.addAttribute("EDRCSummary",
				        edrcFacSrv.getEDRCforSCripsList(scExSrv.getAllScripNames(), predBean));
			}
		} catch (EX_General e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "reports/EDRCSummary";
	}
	
	@GetMapping("/edrc/{scCode}")
	public String showScripsEDRCmyFilter(
	        @PathVariable String scCode, Model model
	)
	{
		
		if (scCode != null && this.edrcFacSrv != null)
		{
			model.addAttribute("summary", edrcFacSrv.getEDRCforSCrip(scCode));
		}
		return "reports/ScripStrengthScoreDetails";
	}
	
	@GetMapping("/val/{scCode}")
	public String testscValuation(
	        @PathVariable String scCode, Model model
	)
	{
		if (scCode != null)
		{
			ScValFormPOJO scvalPOJO = new ScValFormPOJO(scCode, .7, 0);
			model.addAttribute("scValPOJO", scvalPOJO);
		}
		return "valuations/forms/scVal";
	}
	
	@PostMapping("/val/{scCode}")
	public String testscVal(
	        @ModelAttribute("scValPOJO") ScValFormPOJO scValPOJO, Model model
	)
	{
		if (scValPOJO != null)
		{
			model.addAttribute("scVal",
			        scValSrv.getValuationforScrip(scValPOJO.getScCode(), scValPOJO.getCMP(), scValPOJO.getMoS()));
		}
		return "valuations/op/scVal";
	}
	
	@PostMapping("/scVal/{scCode}")
	public String testscValSubmit(
	        @ModelAttribute("scValPOJO") ScValFormPOJO scValPOJO, Model model
	)
	{
		if (scValPOJO != null)
		{
			model.addAttribute("scVal",
			        scValSrv.getValuationforScrip(scValPOJO.getScCode(), scValPOJO.getCMP(), scValPOJO.getMoS()));
		}
		return "valuations/op/scVal";
	}
	
}
