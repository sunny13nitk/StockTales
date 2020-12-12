package stocktales.controllers.databook.scJournal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import stocktales.dataBook.model.pojo.ScripJournalPoJo;
import stocktales.dataBook.model.repo.scjournal.RepoScJournal;
import stocktales.dataBook.scjournal.intf.IScJournalSrv;
import stocktales.utilities.DurationUtilities;

@Controller
@RequestMapping("/scJournal")
public class ScJournalController
{
	
	@Autowired
	private IScJournalSrv scJSrv;
	
	@Autowired
	private RepoScJournal repoScJ;
	
	@GetMapping("/new/{scCode}")
	public String shownewJournal(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		if (scCode != null)
		{
			model.addAttribute("years", DurationUtilities.getYearsList(-5));
			model.addAttribute("scJP", scJSrv.createNewJournalforScrip(scCode));
			
			model.addAttribute("numLast", scJSrv.getQStats_New_ByScrip(scCode));
		}
		return "scrips/dataBook/scJournal/newScJournal";
	}
	
	@GetMapping("/load")
	public String showloadJournal(
	        Model model
	)
	{
		
		{
			model.addAttribute("years", DurationUtilities.getYearsList(-5));
			model.addAttribute("scJP", scJSrv.getScJournal());
			
			model.addAttribute("numLast", null);
		}
		return "scrips/dataBook/scJournal/newScJournal";
	}
	
	@GetMapping("/image")
	public void renderImageFromDB(
	        HttpServletResponse response
	) throws IOException
	{
		
		ScripJournalPoJo scJPojo = scJSrv.getScJournal();
		
		if (scJPojo.getImage() != null)
		{
			byte[] byteArray = new byte[scJPojo.getImage().length];
			int    i         = 0;
			
			for (Byte wrappedByte : scJPojo.getImage())
			{
				byteArray[i++] = wrappedByte; //auto unboxing
			}
			
			//Images
			response.setContentType("image/jpeg");
			
			InputStream is = new ByteArrayInputStream(byteArray);
			IOUtils.copy(is, response.getOutputStream());
			
		}
	}
	
	@GetMapping("/upload")
	public String uploadScrip(
	
	)
	{
		return "scrips/dataBook/scJournal/Imageupload";
	}
	
	@GetMapping("/ovw/{scCode}")
	public String showOvwJournal(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		if (scCode != null)
		{
			model.addAttribute("scCode", scCode);
			model.addAttribute("snippet", scJSrv.getQStats_New_ByScrip(scCode));
			model.addAttribute("summary", repoScJ.getSummaryByScCode(scCode).get(0));
			model.addAttribute("catgClass", repoScJ.countEntriesByCategory(scCode));
			model.addAttribute("srcClass", repoScJ.countEntriesBySource(scCode));
			model.addAttribute("tagDetails", scJSrv.getTagDetailsOvw(scCode));
			model.addAttribute("effectClass", repoScJ.countEntriesByEffect(scCode));
		}
		
		return "scrips/dataBook/scJournal/scJOvw";
	}
	
	/*
	 * ----------------------------------------------------------------
	 *                  POST MAPPING
	 * ----------------------------------------------------------------                 
	 */
	
	@PostMapping("/upload")
	public String handleImagePost(
	        
	        @RequestParam("file") MultipartFile file, Model model
	)
	{
		if (file != null)
		{
			
			scJSrv.uploadImage(file);
			
		}
		return "redirect:/scJournal/load";
		
	}
	
	@PostMapping("/save")
	public String saveJournal(
	        @ModelAttribute("scJP") ScripJournalPoJo scJP, Model model
	)
	{
		
		if (scJP != null)
		{
			scJSrv.refreshScJournal(scJP); //REfresh Buffer From View Model
			
			scJSrv.saveJournal(); //SAve the Journal
		}
		
		scJSrv.clearBuffer(); //Clear the buffer
		//Navigate to Details for Scrip Journal with an Option to Switch to Overview
		return "success";
	}
	
}
