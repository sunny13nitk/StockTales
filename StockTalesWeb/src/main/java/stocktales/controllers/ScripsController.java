package stocktales.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.basket.allocations.autoAllocation.facades.interfaces.EDRCFacade;
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
	
}
