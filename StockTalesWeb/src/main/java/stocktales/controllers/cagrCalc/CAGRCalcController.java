package stocktales.controllers.cagrCalc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.basket.allocations.autoAllocation.strategy.repo.IRepoStrategy;
import stocktales.cagrEval.enums.EnumDurationType;
import stocktales.cagrEval.helperPoJo.CAGRFormPoJo;
import stocktales.cagrEval.helperPoJo.CAGRResult;
import stocktales.cagrEval.helperPoJo.RollOverDurationsParam;
import stocktales.cagrEval.intf.ICAGRCalcSrv;

@Controller
@RequestMapping("/cagrcalc")
public class CAGRCalcController
{
	@Autowired
	private ICAGRCalcSrv cagrCalcSrv;
	
	@Autowired
	private IRepoStrategy repoStgy;
	
	@GetMapping("/show/{stgyId}")
	public String showCAGRForm(
	        @PathVariable String stgyId, Model model
	)
	{
		if (stgyId != null)
		{
			
			int stgId = new Integer(stgyId);
			if (stgId > 0)
			{
				CAGRFormPoJo cagrPojo = new CAGRFormPoJo();
				
				cagrPojo.setStgyId(stgId);
				cagrPojo.setBaseYr(2010);
				cagrPojo.setIntervalinYrs(5);
				cagrPojo.setLength(10);
				cagrPojo.setE2EOnly(false);
				model.addAttribute("pojo", cagrPojo);
			}
		}
		return "cagr/stgyCAGRForm";
	}
	
	@PostMapping("/process")
	public String processCAGRCalc(
	        @ModelAttribute("pojo") CAGRFormPoJo pojo, Model model
	)
	{
		if (pojo != null)
		{
			if (pojo.getStgyId() > 0 && cagrCalcSrv != null)
			{
				try
				{
					cagrCalcSrv.Initialize(pojo.getStgyId(), pojo.isE2EOnly());
					
					cagrCalcSrv.calculateCAGR(
					        new RollOverDurationsParam(pojo.getBaseYr(), pojo.getIntervalinYrs(), pojo.getLength()));
					
					List<CAGRResult> cagrResults = cagrCalcSrv.getCagrResults();
					
					CAGRResult e2eresult = cagrResults.stream()
					        .filter(x -> x.getDurationH().getDurationType() == EnumDurationType.EndToEnd).findFirst()
					        .get();
					
					cagrResults.remove(e2eresult);
					
					model.addAttribute("E2E", e2eresult);
					model.addAttribute("ROList", cagrResults);
					model.addAttribute("details", repoStgy.findByStid(pojo.getStgyId()));
					
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return "cagr/cagrResultsStgy";
	}
}
