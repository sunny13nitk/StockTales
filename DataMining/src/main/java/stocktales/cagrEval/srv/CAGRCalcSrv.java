package stocktales.cagrEval.srv;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.StAllocItem;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.Strategy;
import stocktales.basket.allocations.autoAllocation.strategy.repo.IRepoStrategy;
import stocktales.cagrEval.enums.EnumAggFxn;
import stocktales.cagrEval.enums.EnumDurationType;
import stocktales.cagrEval.enums.EnumStgyMode;
import stocktales.cagrEval.helperPoJo.BalSheetSrvParam;
import stocktales.cagrEval.helperPoJo.CAGRResult;
import stocktales.cagrEval.helperPoJo.DurationHeader;
import stocktales.cagrEval.helperPoJo.NiftyCAGRResult;
import stocktales.cagrEval.helperPoJo.RollOverDurationsParam;
import stocktales.cagrEval.helperPoJo.XIRRItems;
import stocktales.cagrEval.helperPoJo.XIRRSummary;
import stocktales.cagrEval.helperPoJo.YearsFromTo;
import stocktales.cagrEval.helperPoJo.YearsRollOverResults;
import stocktales.cagrEval.intf.IBalSheetUtilSrv;
import stocktales.cagrEval.intf.ICAGRCalcSrv;
import stocktales.cagrEval.intf.INiftyCAGRSrv;
import stocktales.cagrEval.intf.IRollOverYrs;
import stocktales.factsheet.interfaces.IFactSheetBufferSrv;
import stocktales.healthcheck.beanSrv.helperPOJO.NameVal;
import stocktales.scripsEngine.pojos.helperObjs.SheetNames;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CAGRCalcSrv implements ICAGRCalcSrv
{
	@Autowired
	private IFactSheetBufferSrv fsBuffSrv;
	
	@Autowired
	private IRepoStrategy repoStgy;
	
	@Autowired
	private IRollOverYrs rollOverYrsSrv;
	
	@Autowired
	private IBalSheetUtilSrv bSheetUtilSrv;
	
	@Autowired
	private INiftyCAGRSrv niftyCAGRSrv;
	
	private EnumStgyMode stgyMode;
	
	private boolean e2eOnly;
	
	private YearsRollOverResults durations;
	
	private List<CAGRResult> cagrResults = new ArrayList<CAGRResult>();
	
	private List<String> scrips = new ArrayList<String>();
	
	private List<NameVal> stgyAllocations = new ArrayList<NameVal>();
	
	@Override
	public void Initialize(
	        List<String> scrips, boolean calcEndToEndOnly
	) throws Exception
	{
		clear();
		if (scrips != null)
		{
			if (scrips.size() > 0)
			{
				fsBuffSrv.Initialize(scrips, SheetNames.Analysis, false);
				stgyMode     = EnumStgyMode.None;
				this.e2eOnly = calcEndToEndOnly;
				this.scrips  = scrips;
			}
		}
		
	}
	
	@Override
	public void InitializeAdHoc(
	        List<NameVal> scripAllocationsList, boolean calcEndToEndOnly
	)
	{
		clear();
		if (scripAllocationsList != null)
		{
			if (scripAllocationsList.size() > 0)
			{
				//Create a Method to Validate allocations if Not Directly coming from a Saved Strategy
				// - Method TBD
				/*
				 * - Must Include
				 *  -- Duplicate Scrips Detection
				 *  -- Total Allocations Total to be 100%
				 *  -- All Scrips Must Exist in DB
				 */
				
				List<String> scrips = new ArrayList<String>();
				stgyMode     = EnumStgyMode.Adhoc;
				this.e2eOnly = calcEndToEndOnly;
				
				scrips      = scripAllocationsList.stream().map(x -> x.getName()).distinct()
				        .collect(Collectors.toList());
				this.scrips = scrips;
				
			}
		}
		
	}
	
	@Override
	public void Initialize(
	        int strategyId, boolean calcEndToEndOnly
	) throws Exception
	{
		clear();
		//Get Allocation Items from Strategy
		
		if (strategyId > 0)
		{
			if (repoStgy != null)
			{
				Optional<Strategy> stgyO = repoStgy.findById(strategyId);
				if (stgyO.isPresent())
				{
					Strategy stgy = stgyO.get();
					if (stgy.getAllocItems() != null)
					{
						if (stgy.getAllocItems().size() > 0)
						{
							
							for (StAllocItem allocI : stgy.getAllocItems())
							{
								this.scrips.add(allocI.getSccode());
								
								this.stgyAllocations.add(new NameVal(allocI.getSccode(), allocI.getAlloc()));
							}
							
							//Set Strategy Mode ON - If Stgy Valid
							stgyMode     = EnumStgyMode.DB;
							this.e2eOnly = calcEndToEndOnly;
							
							//Load Fact Sheet Buffer for allocation Scrips
							fsBuffSrv.Initialize(scrips, SheetNames.Analysis, false);
						}
					}
					
				}
			}
			
		}
		
	}
	
	@Override
	public void calculateCAGR(
	        RollOverDurationsParam durationsParam
	) throws Exception
	{
		// Load the Roll Over Durations as per Calculations Needed
		
		this.durations = rollOverYrsSrv.generateRollOverYrs(durationsParam.getBaseYr(),
		        durationsParam.getIntervalinYrs(), durationsParam.getLength());
		
		//Loop over each of the Roll Over Durations and E2E Durations to perform stage 1 Calculations
		if (!e2eOnly)
		{
			for (YearsFromTo rolloverDuration : this.durations.getRollOverYrs())
			{
				CAGRResult cagrRes = new CAGRResult();
				cagrRes.setDurationH(new DurationHeader(rolloverDuration.getFrom(), rolloverDuration.getTo(),
				        EnumDurationType.RollOver));
				
				for (String scrip : this.getScrips())
				{
					if (bSheetUtilSrv != null)
					{
						//Trigger CAGR Calculation for Each Scrip for current loop pass calculation period
						BalSheetSrvParam bSheetParam = new BalSheetSrvParam();
						//Load the Param
						bSheetParam.setYrsFromToFilter(rolloverDuration);
						bSheetParam.setScCode(scrip);
						bSheetParam.setAttrName("MCap");
						bSheetParam.setFxntoTrigger(EnumAggFxn.CAGR);
						
						//Call CAGR Calculation
						double cagrVal = Precision.round(bSheetUtilSrv.getFromBalSheetByParam(bSheetParam), 1);
						
						//Populate CAGR Items - Stage 1
						cagrRes.getItems().add(new XIRRItems(scrip, 0, cagrVal, 0));
					}
				}
				
				this.cagrResults.add(cagrRes); //Add to Srv
			}
		}
		
		//Do it for End to End Duration Too
		CAGRResult cagrRes = new CAGRResult();
		cagrRes.setDurationH(new DurationHeader(durations.getE2eYrs().getFrom(), durations.getE2eYrs().getTo(),
		        EnumDurationType.EndToEnd));
		for (String scrip : this.getScrips())
		{
			if (bSheetUtilSrv != null)
			{
				//Trigger CAGR Calculation for Each Scrip for current loop pass calculation period
				BalSheetSrvParam bSheetParam = new BalSheetSrvParam();
				//Load the Param
				bSheetParam.setYrsFromToFilter(
				        new YearsFromTo(durations.getE2eYrs().getFrom(), durations.getE2eYrs().getTo()));
				bSheetParam.setScCode(scrip);
				bSheetParam.setAttrName("MCap");
				bSheetParam.setFxntoTrigger(EnumAggFxn.CAGR);
				
				//Call CAGR Calculation
				double cagrVal = Precision.round(bSheetUtilSrv.getFromBalSheetByParam(bSheetParam), 1);
				
				//Populate CAGR Items - Stage 1
				cagrRes.getItems().add(new XIRRItems(scrip, 0, cagrVal, 0));
			}
		}
		this.cagrResults.add(cagrRes); //Add to Srv
		
		//Perform Stage 2 -  Allocation weighted CAGR and Summarize findings for Each Rollover and E2E
		if (this.cagrResults.size() > 0)
		{
			switch (this.stgyMode)
			{
				case None:
					calcWtCAGRForNoneMode();
					break;
				
				case DB:
					calcWtCAGRforStrategy();
					
				default:
					break;
			}
		}
		
		//Perform Stage 3 - Adjust for No Data Found & Summarize
		adjustNoData_Summarize();
		
	}
	
	/*
	 * ---------------------------------- PRIVATE SECTION ---------------------------
	 */
	
	private void calcWtCAGRForNoneMode(
	)
	{
		int numScrips = this.scrips.size();
		
		double perscAlloc = (100 / numScrips); //In Percentage terms
		
		//Loop through CAGR REsults to Maintain Allocations and WtCAGR for Each XIRR ITEM within each CAGR Result
		
		for (CAGRResult cagrResult : cagrResults)
		{
			for (XIRRItems xirrItem : cagrResult.getItems())
			{
				xirrItem.setAllocation(perscAlloc);
				xirrItem.setWtCAGR(Precision.round((perscAlloc * xirrItem.getCAGR()) / 100, 1));
			}
		}
		
	}
	
	private void calcWtCAGRforStrategy(
	)
	{
		//Loop through CAGR REsults to Maintain Allocations and WtCAGR for Each XIRR ITEM within each CAGR Result
		
		for (CAGRResult cagrResult : cagrResults)
		{
			for (XIRRItems xirrItem : cagrResult.getItems())
			{
				Optional<NameVal> scAllocO = this.stgyAllocations.stream()
				        .filter(x -> x.getName().equals(xirrItem.getScCode())).findFirst();
				if (scAllocO.isPresent())
				{
					NameVal scAlloc = scAllocO.get();
					xirrItem.setAllocation(scAlloc.getValue());
					xirrItem.setWtCAGR(Precision.round((scAlloc.getValue() * xirrItem.getCAGR()) / 100, 1));
				}
				
			}
		}
	}
	
	private void adjustNoData_Summarize(
	)
	{
		for (CAGRResult cagrResult : cagrResults)
		{
			double          sumzeroCAGRAlloc = 0;
			double          nettCAGR         = 0;
			NiftyCAGRResult nfRes            = null;
			
			for (XIRRItems xirrItem : cagrResult.getItems())
			{
				nettCAGR += xirrItem.getWtCAGR();
				if (xirrItem.getCAGR() == 0)
				{
					sumzeroCAGRAlloc += xirrItem.getAllocation();
				}
			}
			
			double deltatobase100 = 100 - sumzeroCAGRAlloc;
			
			double boostFactor = 1;
			
			if (deltatobase100 > 0)
			{
				boostFactor = 100 / deltatobase100;
			}
			
			XIRRSummary summary = new XIRRSummary();
			summary.setSumzeroCAGRAlloc(sumzeroCAGRAlloc);
			if (sumzeroCAGRAlloc > 0)
			{
				summary.setAdjusted(true);
			}
			summary.setBoostFactor(Precision.round(boostFactor, 1));
			summary.setNettCAGR(Precision.round(nettCAGR * boostFactor, 1));
			
			/*
			 * ---NIFTY CAGR
			 */
			if (cagrResult.getDurationH().getDurationType() == EnumDurationType.RollOver)
			{
				nfRes = niftyCAGRSrv.calculateNiftyCAGR(cagrResult.getDurationH().getFrom(),
				        cagrResult.getDurationH().getTo());
			} else
			{
				nfRes = niftyCAGRSrv.calculateNiftyCAGRToDate(cagrResult.getDurationH().getFrom());
			}
			
			if (nfRes != null)
			{
				summary.setNiftyCAGR(nfRes.getCAGR());
			}
			
			cagrResult.setSummary(summary);
		}
	}
	
	private void clear(
	)
	{
		this.cagrResults     = new ArrayList<CAGRResult>();
		this.scrips          = new ArrayList<String>();
		this.stgyAllocations = new ArrayList<NameVal>();
	}
}
