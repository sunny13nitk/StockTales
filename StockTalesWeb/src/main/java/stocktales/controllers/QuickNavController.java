package stocktales.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.siteconfig.model.entity.SitePaths;
import stocktales.siteconfig.repo.RepoSitePaths;
import stocktales.uiHelperPOJOs.SearchText;

@Controller
@RequestMapping("/qnav")
public class QuickNavController
{
	@Autowired
	private RepoSitePaths repoSitePaths;
	
	@GetMapping("/site")
	public String showqNavSite(
	        Model model
	)
	{
		
		//Add all Title Urls to Paths
		if (repoSitePaths != null)
		{
			
			List<SitePaths> sitePaths = repoSitePaths.findAll();
			
			model.addAttribute("paths", sitePaths);
		}
		
		//Add a Blank Search Text POJO
		model.addAttribute("searchtext", new SearchText());
		
		return "quickNav/qnavSite";
	}
	
	@PostMapping("/site")
	public String navigatetoSite(
	        @ModelAttribute("searchtext") SearchText stext
	)
	{
		String url = null;
		if (stext != null)
		{
			if (stext.getStext() != null)
			{
				url = stext.getStext();
			}
		}
		
		return "redirect:" + url;
		
	}
	
}
