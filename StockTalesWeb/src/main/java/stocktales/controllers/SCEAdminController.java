package stocktales.controllers;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.IXLS_Scrip_Upload_Srv;

@Controller
@Slf4j
@RequestMapping("/admin/sc")
public class SCEAdminController
{
	@Autowired
	private IXLS_Scrip_Upload_Srv scUploadSrv;
	
	private final String uploadScripForm = "scrips/upload";
	
	@GetMapping("/upload")
	public String uploadScrip(
	
	)
	{
		
		return uploadScripForm;
	}
	
	@PostMapping("/upload")
	public String handleImagePost(
	        
	        @RequestParam("file") MultipartFile file, Model model
	)
	{
		if (file != null)
		{
			try
			{
				XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
				if (wb != null)
				{
					if (scUploadSrv.Upload_Scrip_from_XLS_WBCtxt(wb))
					{
						log.debug("Scrip Successfully Uploaded");
						//to be replaced with properties messages
						model.addAttribute("formSucc", "Scrip Successfully Uploaded!");
					}
					
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				//to be replaced with properties messages
				model.addAttribute("formError", e.getMessage());
			}
		}
		
		return uploadScripForm;
	}
}
