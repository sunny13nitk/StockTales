package stocktales.controllers.scovw;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.scsnapshot.model.pojo.StockSnapshot;
import stocktales.scsnapshot.srv.intf.IStockSnapshotSrv;

@Controller
@RequestMapping("/scOvw")
public class StockController
{
	@Autowired
	private IStockSnapshotSrv ssSrv;
	
	@GetMapping("/{scCode}")
	public String showCAGRForm(
	        @PathVariable String scCode, Model model
	)
	{
		if (ssSrv != null)
		{
			StockSnapshot ss;
			try
			{
				ss = ssSrv.getStockSnapshot(scCode);
				if (ss != null)
				{
					model.addAttribute("ss", ss);
					
					model.addAttribute("chartData2", ss.getTrends().getRevWCTrends());
					
				}
			} catch (Exception e)
			{
				model.addAttribute("formError", "Invalid Scrip Code ! - " + scCode);
				return "Error";
			}
			
		}
		
		return "scsnapshot/scOvw";
	}
	
}
