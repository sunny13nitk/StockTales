package stocktales.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.basket.allocations.autoAllocation.facades.pojos.SC_EDRC_Summary_List_Repo;
import stocktales.basket.allocations.autoAllocation.interfaces.IScAllocationListRepo;
import stocktales.basket.allocations.autoAllocation.pojos.ScAllocation;
import stocktales.basket.allocations.autoAllocation.pojos.ScAllocationList;
import stocktales.basket.allocations.autoAllocation.strategy.interfaces.IStrategySrv;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.Strategy;
import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.interfaces.IStgyRebalanceSrv;
import stocktales.basket.allocations.autoAllocation.strategy.repo.IRepoStrategy;
import stocktales.basket.allocations.autoAllocation.valuations.interfaces.SCValuationSrv;
import stocktales.basket.allocations.autoAllocation.valuations.pojos.ScValuation;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.services.interfaces.ScripService;

@Controller
@RequestMapping("/stratergy")
public class StratergyController
{
	@Autowired
	private SC_EDRC_Summary_List_Repo edrcFilteredRepo;
	
	@Autowired
	private IScAllocationListRepo allocationsRepo;
	
	@Autowired
	private SCValuationSrv scValSrv;
	
	@Autowired
	private ScripService scSrv;
	
	@Autowired
	private IStrategySrv stgySrv;
	
	@Autowired
	private IRepoStrategy stgRepo;
	
	@Autowired
	private IStgyRebalanceSrv srv_Stgy_Rebal;
	
	private List<String> scCodes;
	
	@GetMapping("/myFilter")
	public String showStratergyStaging(
	        Model model
	)
	{
		
		if (allocationsRepo != null)
		{
			
			try
			{
				//Populate Scrip Codes if Null
				getSCCodes();
				
				//Add a Local Variable to Filter Scrips coming from Filter Predicate
				List<String> scCodesList = new ArrayList<String>();
				scCodesList = this.scCodes;
				
				//Get List of Scrips from Autowired Filtered List
				if (this.edrcFilteredRepo != null)
				{
					ScAllocationList scAllocList = new ScAllocationList();
					if (this.edrcFilteredRepo.getEdrcFilteredList() != null)
					{
						if (this.edrcFilteredRepo.getEdrcFilteredList().size() > 0)
						{
							List<String> scrips = new ArrayList<String>();
							
							edrcFilteredRepo.getEdrcFilteredList().stream().filter(x -> scrips.add(x.getScCode()))
							        .distinct().collect(Collectors.toList());
							
							//Prepare the SCrip List
							if (scrips.size() > 0)
							{
								scAllocList.setScAllocations(allocationsRepo.stageAllocationsforScrips(scrips));
								
								model.addAttribute("scStagingList", scAllocList);
							}
							
						}
						
					}
					
					for (ScAllocation scAllocation : scAllocList.getScAllocations())
					{
						Optional<String> scCodeFound = scCodesList.stream()
						        .filter(x -> x.toString().equals(scAllocation.getScCode())).findFirst();
						
						if (scCodeFound.isPresent())
						{
							scCodesList.remove(scAllocation.getScCode());
							
						}
						
					}
				}
				
				//Add Blank Scrip Code to Model
				model.addAttribute("scCode", new String());
				//Add Scrip Codes
				model.addAttribute("scCodes", scCodesList);
				
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
		return "strategy/Staging_1";
	}
	
	@PostMapping("/myFilter/addScrip")
	public String addScripToStrategy(
	        @ModelAttribute("scCode") String scCode
	)
	{
		
		if (scCode != null)
		{
			this.allocationsRepo.addNew(scCode);
		}
		
		return "redirect:/stratergy/staging_1";
		
	}
	
	@GetMapping("/staging_1")
	public String showStaging_1(
	        Model model
	)
	{
		if (allocationsRepo != null)
		{
			if (allocationsRepo.getScAllocList() != null)
			{
				if (allocationsRepo.getScAllocList().size() > 0)
				{
					
					ScAllocationList scAllocList = new ScAllocationList();
					scAllocList.setScAllocations(allocationsRepo.getScAllocList());
					//Add Allocations List
					model.addAttribute("scStagingList", scAllocList);
					
					//Prepare List of Scrips as per Allocations List
					List<String> scrips = new ArrayList<String>();
					allocationsRepo.getScAllocList().stream().filter(x -> scrips.add(x.getScCode())).distinct()
					        .collect(Collectors.toList());
					
					//Get all Scrips Codes
					//Add a Local Variable to Filter Scrips coming from Current Allocations Collection
					List<String> scCodesList = new ArrayList<String>();
					scCodesList = getSCCodes(); //Al SC Codes
					
					for (ScAllocation scAllocation : scAllocList.getScAllocations())
					{
						Optional<String> scCodeFound = scCodesList.stream()
						        .filter(x -> x.toString().equals(scAllocation.getScCode())).findFirst();
						
						if (scCodeFound.isPresent())
						{
							scCodesList.remove(scAllocation.getScCode());
							
						}
						
					}
					
					//Add Blank Scrip Code to Model
					model.addAttribute("scCode", new String());
					//Add Scrip Codes
					model.addAttribute("scCodes", scCodesList);
					
				}
			}
		}
		
		return "strategy/Staging_1";
	}
	
	@GetMapping("/staging/delete/{scCode}")
	public String showStaging_1afterScDelete(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		if (scCode != null)
		{
			this.allocationsRepo.delete(scCode);
		}
		return "redirect:/stratergy/staging_1";
	}
	
	@GetMapping("/staging_2")
	public String showStaging_2(
	        Model model
	)
	{
		if (stgySrv != null)
		{
			model.addAttribute("qStats", stgySrv.getQuickStats());
			model.addAttribute("strategy", new Strategy());
		}
		
		return "strategy/Staging_2";
	}
	
	@GetMapping("/list")
	public String listAll(
	        Model model
	)
	{
		if (stgySrv != null)
		{
			model.addAttribute("stgList", stgySrv.getStrategiesList());
		}
		return "strategy/list";
	}
	
	@GetMapping("/{stgId}")
	public String showStrategyDetails(
	        @PathVariable("stgId") int stgId, Model model
	)
	{
		if (stgId > 0)
		{
			
			//Get the Strategy and its allocItems
			if (stgySrv != null)
			{
				this.allocationsRepo.clear_replace_allocations(stgySrv.getValuationsSimulationforStrategy(stgId));
				//Add Allocations List
				ScAllocationList scAllocList = new ScAllocationList();
				scAllocList.setScAllocations(allocationsRepo.getScAllocList());
				//Add Strategy ID
				scAllocList.setStgyId(stgId);
				model.addAttribute("scStagingList", scAllocList);
				
			}
		}
		
		return "strategy/simulation";
	}
	
	@GetMapping("/rebal/{stgId}")
	public String showRebalancing(
	        @PathVariable("stgId") String stgId, Model model
	)
	{
		model.addAttribute("rblPOJO", srv_Stgy_Rebal.triggerReBalancingforStgy(new Integer(stgId)));
		model.addAttribute("scCodes", getSCCodes());
		return "strategy/reBalance";
	}
	
	/*
	 * ____________________________________________________________________________________
	 * 
	 *                     POST MAPPINGS
	 * _____________________________________________________________________________________
	 */
	
	@PostMapping(value = "/staging_1", params = "action=validProc")
	public String refreshStaging_1(
	        @ModelAttribute("scStagingList") ScAllocationList scAllocations, Model model
	
	)
	{
		
		if (scAllocations != null)
		{
			List<ScAllocation> allocList = new ArrayList<ScAllocation>();
			allocList = scAllocations.getScAllocations();
			if (allocList.size() > 0)
			{
				//Totals of Allocation rounded to zero places should be 100
				double totalAllocPer = scAllocations.getScAllocations().stream()
				        .mapToDouble(ScAllocation::getAllocation).sum();
				totalAllocPer = Precision.round(totalAllocPer, 0);
				if (totalAllocPer == 100)
				{
					// Initialize Session Bean for Stratergy
					stgySrv.Initialize();
					
					// Load on Allocations
					stgySrv.loadAllocationItems(allocList);
				}
				
			}
		}
		return "redirect:/stratergy/staging_2";
	}
	
	@PostMapping(value = "/staging_1", params = "action=refresh")
	public String vaildateProcStaging_1(
	        @ModelAttribute("scStagingList") ScAllocationList scAllocations, Model model
	
	)
	{
		
		if (scAllocations != null)
		{
			refreshAllocations(scAllocations);
		}
		//redirect to stratergy/staging_1
		return "redirect:/stratergy/staging_1";
	}
	
	@PostMapping("/save")
	public String saveStrategy(
	        @ModelAttribute("strategy") Strategy strategy
	
	)
	{
		if (strategy != null && stgySrv != null)
		{
			stgySrv.save(strategy);
		}
		
		return "strategy/list";
	}
	
	@PostMapping("/simulation")
	public String simulateStrategy(
	        @ModelAttribute("scStagingList") ScAllocationList scAllocations, Model model
	)
	{
		
		refreshAllocations(scAllocations);
		//Add Allocations List
		ScAllocationList scAllocList = new ScAllocationList();
		scAllocList.setScAllocations(this.allocationsRepo.getScAllocList());
		//Add Strategy ID
		scAllocList.setStgyId(scAllocations.getStgyId());
		model.addAttribute("scStagingList", scAllocList);
		
		return "strategy/simulation";
	}
	
	/*************************************************************************
	 * ------------------PRIVATE METHODS -----------------------------------
	 *************************************************************************/
	
	private void refreshAllocations(
	        ScAllocationList scAllocations
	)
	{
		List<ScAllocation> allocList = new ArrayList<ScAllocation>();
		allocList = scAllocations.getScAllocations();
		if (allocList.size() > 0)
		{
			//for Each Allocation Item
			for (ScAllocation scAllocation : allocList)
			{
				//Get the Valuation = Retrigger Valuation Calculation using current CMP MoS
				ScValuation scValRecalc = scValSrv.getValuationforScrip(scAllocation.getScCode(), scAllocation.getCMP(),
				        scAllocation.getMoS());
				if (scValRecalc != null)
				{
					//Create SCAllocation POJO using this valuation calc above
					ScAllocation newAlloc = new ScAllocation(scValRecalc);
					//Set allocation Percentage as per POJO in loop pass to Create SC Allocation
					newAlloc.setAllocation(scAllocation.getAllocation());
					//Call Repo Method Refresh passing the Updated SCAllocation POJO
					this.allocationsRepo.refreshAllocation(newAlloc);
				}
				
			}
			
		}
	}
	
	/* _______________________________________________________________________________________
	 * 
	 *                                  PRIVATE METHODS
	 * _______________________________________________________________________________________
	 */
	
	public List<String> getSCCodes(
	)
	{
		
		//Populate Scrip Codes if Null
		if (this.scCodes == null)
		{
			if (scSrv != null)
			{
				try
				{
					this.scCodes = scSrv.getAllScripNames();
				} catch (EX_General e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return this.scCodes;
		
	}
	
}
