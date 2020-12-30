package stocktales.controllers.config.site;

import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController
{
	@GetMapping("/")
	public String showHome(
	        @CurrentSecurityContext(expression = "authentication.name") String username
	)
	
	{
		if (username != null)
		{
			//Set it in Session Bean
		}
		return "home/StockTalesIndex";
	}
}
