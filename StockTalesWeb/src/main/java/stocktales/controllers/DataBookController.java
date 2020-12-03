package stocktales.controllers;

import java.util.Optional;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.dataBook.config.entity.CfgScripsFieldsPool;
import stocktales.dataBook.config.entity.CfgSectorsFieldsPool;
import stocktales.dataBook.enums.EnumInterval;
import stocktales.dataBook.fpSrv.interfaces.IScripSpFieldPoolSrv;
import stocktales.dataBook.fpSrv.interfaces.ISecSpFieldPoolSrv;
import stocktales.dataBook.helperPojo.IdNotes;
import stocktales.dataBook.model.entity.FPFinancialSector;
import stocktales.dataBook.model.repo.RepoCfgScripFP;
import stocktales.dataBook.model.repo.RepoCfgSecFP;
import stocktales.dataBook.model.repo.RepoFPFinancialSector;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_GeneralQ;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCExistsDB_Srv;
import stocktales.utilities.DurationUtilities;

/*
 * -----------------  DATA BOOK CONTROLLER FOR SCRIP DATA MAINTAINENCE  --------------
 */
@Controller
@RequestMapping("/databook/scrip")
public class DataBookController
{
	@Autowired
	private ISCExistsDB_Srv       scExSrv;
	@Autowired
	private RepoCfgSecFP          repocfgsecFP;
	@Autowired
	private RepoCfgScripFP        repocfgscFp;
	@Autowired
	private RepoFPFinancialSector repoFPFinancials;
	
	@Autowired
	private ApplicationContext appCtxt;
	
	@GetMapping("/secsp/{scCode}")
	public String triggerNav_SecSp_DataMaintain_Base(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		String desurl = null;
		
		String sector = null;
		//Get Sector from Scrip Code
		if (scExSrv != null)
		{
			try
			{
				EN_SC_GeneralQ entSc = scExSrv.Get_ScripExisting_DB(scCode);
				if (entSc != null)
				{
					sector = entSc.getSector();
				}
			} catch (EX_General e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//Get the BeanSrv for Scrip Sector
		if (sector != null)
		{
			Optional<CfgSectorsFieldsPool> cfgsecFPO = repocfgsecFP.findBySector(sector);
			if (cfgsecFPO.isPresent())
			{
				CfgSectorsFieldsPool cfgsecFp = cfgsecFPO.get();
				//Get the bean from AppCtxt
				@SuppressWarnings("unchecked")
				ISecSpFieldPoolSrv<T> fpSrv = (ISecSpFieldPoolSrv<T>) appCtxt.getBean(cfgsecFp.getSrvbean());
				if (fpSrv != null)
				{
					
					//Load the Model for Scrip Sector Sp. Data - in case bean found
					/*
					 * Scrip Code, List<Annual & Quarterly>
					 */
					
					model.addAttribute("scCode", scCode);
					model.addAttribute("sector", sector);
					
					model.addAttribute("AnnualList", fpSrv.findAllByInterval(EnumInterval.Annual));
					model.addAttribute("QtrylList", fpSrv.findAllByInterval(EnumInterval.Quarterly));
					
					desurl = "scrips/dataBook/secSpFPoolDataList";
				}
				
			} else
			{
				//No Config Found webpage in case of bean not found
				desurl = "scrips/dataBook/NoConfigFound";
			}
			
		}
		//REdirect to Sector Specific List Page 
		return desurl;
	}
	
	@GetMapping("/scsp/{scCode}")
	public String triggerNav_ScSp_DataMaintain_Base(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		String desurl = null;
		
		//Get the BeanSrv for Scrip Sector
		if (scCode != null)
		{
			Optional<CfgScripsFieldsPool> cfgscFPO = repocfgscFp.findBySccode(scCode);
			if (cfgscFPO.isPresent())
			{
				CfgScripsFieldsPool cfgscFp = cfgscFPO.get();
				//Get the bean from AppCtxt
				@SuppressWarnings("unchecked")
				IScripSpFieldPoolSrv<T> fpSrv = (IScripSpFieldPoolSrv<T>) appCtxt.getBean(cfgscFp.getSrvbean());
				if (fpSrv != null)
				{
					
					//Load the Model for Scrip Sector Sp. Data - in case bean found
					/*
					 * Scrip Code, List<Annual & Quarterly>
					 */
					
					model.addAttribute("scCode", scCode);
					
					model.addAttribute("AnnualList", fpSrv.findAllByInterval(EnumInterval.Annual));
					model.addAttribute("QtrylList", fpSrv.findAllByInterval(EnumInterval.Quarterly));
					
					desurl = "scrips/dataBook/scSpFPoolDataList";
				}
				
			} else
			{
				//No Config Found webpage in case of bean not found
				desurl = "scrips/dataBook/NoConfigFound";
			}
			
		}
		//REdirect to Sector Specific List Page 
		return desurl;
	}
	
	/*
	 * Never Navigate Directly always via  "databook/scrip/secsp/{scCode}"
	 */
	@GetMapping("/secSpData/{scCode}/list")
	public String showSectorSpDataforScrip(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		
		return "scrips/dataBook/secSpFPoolDataList";
	}
	
	@GetMapping("/fpdata/Notes/{fpid}")
	public String showNotes(
	        @PathVariable("fpid") String fpid, Model model
	)
	{
		if (fpid != null)
		{
			Optional<FPFinancialSector> fpDataO = repoFPFinancials.findById(new Long(fpid));
			if (fpDataO.isPresent())
			{
				model.addAttribute("scCode", fpDataO.get().getSccode());
				
				if (fpDataO.get().getVald() > 0)
				{
					model.addAttribute("interval", fpDataO.get().getValm() + "-Q" + fpDataO.get().getVald());
				} else
				{
					model.addAttribute("interval", fpDataO.get().getValm());
				}
				
				IdNotes idNotes = new IdNotes(fpDataO.get().getId(), fpDataO.get().getNotes());
				model.addAttribute("idNotes", idNotes);
			}
			
			///Populate IdNotes POJO and navigate
		}
		
		return "scrips/dataBook/NotesPanel_SS_FP";
	}
	
	/*
	 * -------------------- POST MAPPINGS -------------------------------------------------
	 * 
	 */
	
	@PostMapping("/sspfp/Notes")
	public String savesecSpFpNotes(
	        @ModelAttribute("idNotes") IdNotes idNotes
	)
	{
		FPFinancialSector fpData = null;
		if (idNotes != null)
		{
			Optional<FPFinancialSector> fpDataO = repoFPFinancials.findById(new Long(idNotes.getId()));
			if (fpDataO.isPresent())
			{
				fpData = fpDataO.get();
				//REset Notes
				fpData.setNotes(idNotes.getNotes());
				//Save the Entity
				repoFPFinancials.save(fpData);
				
			}
		}
		
		//REdirect to List for Specific Scrip 
		return "redirect:/databook/scrip/secsp/" + fpData.getSccode();
	}
	
	/*
	 * -----------------------------------------------------------------------------
	 *   -------------        FINANCIAL SECTOR FIELD POOL     ----------------------
	 * -----------------------------------------------------------------------------
	 */
	@GetMapping("/secsp/new/Financials/{scCode}")
	public String createFinSecFPool(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		if (scCode != null)
		{
			FPFinancialSector newfPool = new FPFinancialSector();
			newfPool.setSccode(scCode);
			model.addAttribute("fpdata", newfPool);
			model.addAttribute("years", DurationUtilities.getYearsList(-5));
		}
		
		return "scrips/dataBook/sectors/Financials";
	}
	
	@GetMapping("/fpdata/Financials/{fpid}")
	public String showFinancialsfpoolData(
	        @PathVariable("fpid") String fpid, Model model
	)
	{
		model.addAttribute("fpdata", repoFPFinancials.findById(new Long(fpid)));
		model.addAttribute("years", DurationUtilities.getYearsList(-5));
		return "scrips/dataBook/sectors/Financials";
	}
	
	/**
	 * ________________________________________________________________________
	 *                       POST MAPPINGS
	 * ________________________________________________________________________
	 */
	
	@PostMapping("/sspfp/financials")
	public String savesecSpFpData(
	        @ModelAttribute("fpdata") FPFinancialSector fpData
	)
	{
		if (fpData != null)
		{
			if (fpData.getSccode() != null)
			{
				repoFPFinancials.save(fpData);
			}
		}
		//REdirect to List for Specific Scrip - /secSpData/{scCode}/list
		return "redirect:/databook/scrip/secsp/" + fpData.getSccode();
		
	}
	
}
