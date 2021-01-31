package stocktales.controllers.pf;

import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.usersPF.intf.ISessionUserManager;
import stocktales.usersPF.model.UserPFConfig;
import stocktales.usersPF.repo.RepoBrokers;
import stocktales.usersPF.repo.RepoUserPFConfig;

@Controller
@RequestMapping("/pf")
public class PFController
{
	
	@Autowired
	private ISessionUserManager usrMgrSrv;
	
	@Autowired
	private RepoBrokers repoBrokers;
	
	@Autowired
	private RepoUserPFConfig repoPFConfig;
	
	@Autowired
	private MessageSource msgSrc;
	
	@GetMapping("/ovw")
	public String showUserPFOvw(
	        Model model
	)
	{
		String vwName = "pf/pfOvw";
		if (usrMgrSrv.getUserName() != null)
		{
			usrMgrSrv.loadPFDetails();
			UserPFConfig pfConfig = usrMgrSrv.getUserPFDetails();
			if (pfConfig != null)
			{
				//Load the model accordingly and Traverse
				model.addAttribute("pfConfig", pfConfig);
				
			} else
			{
				//Initialize and Traverse
				vwName   = "pf/pfCusForm";
				pfConfig = new UserPFConfig();
				pfConfig.setUsername(usrMgrSrv.getUserName());
				model.addAttribute("pfConfig", pfConfig);
				model.addAttribute("message", msgSrc.getMessage("NO_PFCUS", null, Locale.ENGLISH));
				model.addAttribute("brokers", repoBrokers.getBrokerNames());
			}
		}
		return vwName;
	}
	
	@GetMapping("/cus")
	public String showCustomizing(
	        Model model
	)
	{
		if (usrMgrSrv.getUserName() != null)
		{
			usrMgrSrv.loadPFDetails();
			UserPFConfig pfConfig = usrMgrSrv.getUserPFDetails();
			if (pfConfig != null)
			{
				if (pfConfig.getCid() > 0)
				{
					model.addAttribute("pfConfig", pfConfig);
					model.addAttribute("message", msgSrc.getMessage("EDIT_PFCUS", null, Locale.ENGLISH));
					model.addAttribute("brokers", repoBrokers.getBrokerNames());
				}
			}
		}
		
		return "pf/pfCusForm";
	}
	
	@PostMapping("/saveConfig")
	public String saveConfig(
	        @Valid @ModelAttribute("pfConfig") UserPFConfig pfConfig, BindingResult bres, Model model
	)
	{
		String vwName = "pf/pfOvw";
		if (pfConfig != null)
		{
			if (bres.hasErrors())
			{
				model.addAttribute("formError", bres.getAllErrors().get(0));
				model.addAttribute("brokers", repoBrokers.getBrokerNames());
				vwName = "pf/pfCusForm";
			} else
			{
				repoPFConfig.save(pfConfig);
			}
			
		}
		
		return vwName;
	}
}
