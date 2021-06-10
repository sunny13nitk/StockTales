package stocktales.historicalPrices.srv.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.basket.allocations.autoAllocation.strategy.interfaces.IStgyAllocShort;
import stocktales.basket.allocations.autoAllocation.strategy.repo.RepoStgyAllocations;
import stocktales.historicalPrices.enums.EnumInterval;
import stocktales.historicalPrices.pojo.DateStgySummary;
import stocktales.historicalPrices.pojo.HistoricalQuote;
import stocktales.historicalPrices.pojo.StgyRelValuation;
import stocktales.historicalPrices.pojo.StockHistory;
import stocktales.historicalPrices.pojo.StockPriceAllocVal;
import stocktales.historicalPrices.srv.intf.ITimeSeriesStgyValuationSrv;
import stocktales.historicalPrices.utility.StockPricesUtility;
import stocktales.maths.UtilPercentages;
import yahoofinance.histquotes.Interval;

@Service
public class TimeSeriesStgyValuationsSrv implements ITimeSeriesStgyValuationSrv
{
	
	@Autowired
	private RepoStgyAllocations repoStgyAlloc;
	
	private List<IStgyAllocShort> stgyAllocs;
	
	private String[] scrips;
	
	private int amount;
	private int intervalCal;
	
	private Calendar from;
	private Calendar to;
	
	private SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
	
	private String fromDate;
	
	private List<StockHistory> stocksHistory;
	
	private List<DateStgySummary> dateStgySummary = new ArrayList<DateStgySummary>();
	
	private List<StgyRelValuation> stgyRelValuations = new ArrayList<StgyRelValuation>();
	
	@Override
	public List<StgyRelValuation> getValuationsforStrategy(
	        int StgyId, EnumInterval interval
	) throws Exception
	{
		
		//0. clear all
		clearAll();
		
		//1. Get the Strategy Scrips and Allocations
		populateAllocationforStgy(StgyId);
		
		if (this.stgyAllocs != null)
		{
			//2. Get the Price for Each Scrip in strategy as per Interval
			if (interval != null)
			{
				getScripCodes();
				
				if (scrips.length > 0)
				{
					populatePriceData(interval);
					setDates();
				}
				
				//3. Calculate Value for Each Day for each scrip as per allocation
				ParseData();
				
				//4. Base the result from starting day and populate relative valuation
				BaseRefData();
			}
		}
		
		return this.stgyRelValuations;
	}
	
	private void clearAll(
	)
	{
		this.dateStgySummary   = new ArrayList<DateStgySummary>();
		this.stgyRelValuations = new ArrayList<StgyRelValuation>();
		this.fromDate          = null;
		this.stgyAllocs        = null;
		this.amount            = 0;
		this.from              = null;
		this.intervalCal       = 0;
		this.scrips            = null;
		this.stocksHistory     = null;
		
	}
	
	private void populateAllocationforStgy(
	        int StgyId
	)
	{
		if (StgyId > 0 && repoStgyAlloc != null)
		{
			this.stgyAllocs = repoStgyAlloc.findAllByStrategyStid(StgyId);
		}
	}
	
	private void getScripCodes(
	)
	{
		
		if (this.stgyAllocs != null)
		{
			if (this.stgyAllocs.size() > 0)
			{
				scrips = new String[this.stgyAllocs.size()];
				int i = 0;
				for (IStgyAllocShort stgyAlloc : this.stgyAllocs)
				{
					
					scrips[i] = stgyAlloc.getSccode();
					i++;
				}
				
			}
		}
		
	}
	
	private void populatePriceData(
	        EnumInterval interval
	) throws Exception
	{
		switch (interval)
		{
			case LastMonth:
				this.stocksHistory = StockPricesUtility.getHistoricalPricesforScrips(scrips, Calendar.MONTH, 1,
				        Interval.DAILY);
				amount = 1;
				intervalCal = Calendar.MONTH;
				break;
			
			case Last3Months:
				this.stocksHistory = StockPricesUtility.getHistoricalPricesforScrips(scrips, Calendar.MONTH, 3,
				        Interval.DAILY);
				amount = 3;
				intervalCal = Calendar.MONTH;
				break;
			
			case Last6Months:
				this.stocksHistory = StockPricesUtility.getHistoricalPricesforScrips(scrips, Calendar.MONTH, 6,
				        Interval.DAILY);
				amount = 6;
				intervalCal = Calendar.MONTH;
				break;
			
			case Last1Yr:
				this.stocksHistory = StockPricesUtility.getHistoricalPricesforScrips(scrips, Calendar.YEAR, 1,
				        Interval.DAILY);
				amount = 1;
				intervalCal = Calendar.YEAR;
				break;
			
			case Last2Yrs:
				this.stocksHistory = StockPricesUtility.getHistoricalPricesforScrips(scrips, Calendar.YEAR, 2,
				        Interval.DAILY);
				amount = 2;
				intervalCal = Calendar.YEAR;
				break;
			
			case Last3Yrs:
				this.stocksHistory = StockPricesUtility.getHistoricalPricesforScrips(scrips, Calendar.YEAR, 3,
				        Interval.DAILY);
				amount = 3;
				intervalCal = Calendar.YEAR;
				break;
			
			case Last5Yrs:
				this.stocksHistory = StockPricesUtility.getHistoricalPricesforScrips(scrips, Calendar.YEAR, 5,
				        Interval.DAILY);
				amount = 5;
				intervalCal = Calendar.YEAR;
				break;
			
			case Last7Yrs:
				this.stocksHistory = StockPricesUtility.getHistoricalPricesforScrips(scrips, Calendar.YEAR, 7,
				        Interval.DAILY);
				amount = 7;
				intervalCal = Calendar.YEAR;
				break;
			
			case Last10Yrs:
				this.stocksHistory = StockPricesUtility.getHistoricalPricesforScrips(scrips, Calendar.YEAR, 10,
				        Interval.DAILY);
				amount = 10;
				intervalCal = Calendar.YEAR;
				break;
			
			default:
				break;
		}
	}
	
	private void setDates(
	
	)
	{
		this.from = Calendar.getInstance();
		this.to   = Calendar.getInstance();
		from.add(intervalCal, amount * -1); // from Calendar.Interval Amounts ago
		
		fromDate = getNextDate();
		
	}
	
	private String getNextDate(
	)
	{
		String nextDate = null;
		if (from != null)
		{
			from.add(Calendar.DAY_OF_MONTH, 1); //Add 1 Day to Current FRom
			if (to.after(from)) //Only if FRom is Less than Last to which is Today's Date
			{
				
				nextDate = format1.format(from.getTime()); //Populate the next Date in String Format for Filtering
				
			}
			
		}
		
		return nextDate;
	}
	
	private void ParseData(
	)
	{
		if (fromDate != null && this.stocksHistory != null)
		{
			while (fromDate != null)
			{
				
				/*
				 * For each date starting From Date 
				 * -------  Prepare the Stock Price Allocation Value
				 * --------------Finally Summarize for Each Date
				 */
				DateStgySummary dateStgySum  = new DateStgySummary();
				double          sumzeroAlloc = 0;
				
				for (IStgyAllocShort stgyAlloc : this.stgyAllocs) //for Each Scrip in Strategy
				{
					
					//Get the Price for from Date - Current Date
					Optional<StockHistory> scripHistPrice = this.stocksHistory.stream().filter(
					        
					        x ->
					        {
						        
						        /*
						         *  Filtering when the Scrip Code in Price history matches and also the Date with
						         *  Current Date - from Date 
						         */
						        
						        if (
						            x.getScCode().equals(stgyAlloc.getSccode())
						                    && (x.priceHistory.stream().filter(w -> w.getDate().equals(fromDate)))
						                            .findFirst().isPresent()
						        )
						        {
							        return true;
						        }
						        return false;
						        
					        }).findFirst();
					
					dateStgySum.setDate(fromDate);
					
					/*
					 * Historical Price Found - Calculate Value and Append
					 */
					StockPriceAllocVal stpAlloc = new StockPriceAllocVal();
					stpAlloc.setScCode(stgyAlloc.getSccode());
					stpAlloc.setAlloc(stgyAlloc.getAlloc());
					
					if (scripHistPrice.isPresent())
					{
						
						Optional<HistoricalQuote> stockHistO = scripHistPrice.get().getPriceHistory().stream()
						        .filter(w -> w.getDate().equals(fromDate)).findFirst();
						if (stockHistO.isPresent())
						{
							stpAlloc.setClosePrice(stockHistO.get().getClosePrice());
							stpAlloc.setValue(
							        Precision.round(stpAlloc.getClosePrice() * (stpAlloc.getAlloc() / 100), 1));
						}
						
					} else
					/*
					 * Historical Price not found - Leave the Value blank and append
					 */
					{
						sumzeroAlloc += stgyAlloc.getAlloc();
					}
					
					dateStgySum.getStockVals().add(stpAlloc);
					
				}
				
				/*
				 * Calculate Nett. Value for the Date - Adjusting for Not Found Scrip allocations
				 */
				
				double sumVal    = dateStgySum.getStockVals().stream().mapToDouble(StockPriceAllocVal::getValue).sum();
				double sumValSet = Precision.round(sumVal * (100 / (100 - sumzeroAlloc)), 0);
				if (sumValSet >= 0)
				{
					dateStgySum.setTotalValue(sumValSet);
					this.dateStgySummary.add(dateStgySum);
					
				}
				sumVal    = 0;
				sumValSet = 0;
				
				//Iterate for until valid Next Date
				fromDate = getNextDate();
				
			}
		}
	}
	
	private void BaseRefData(
	)
	{
		if (this.dateStgySummary != null)
		{
			if (this.dateStgySummary.size() > 0)
			{
				//Remove All Market Holidays when Closed Price is Zero
				this.dateStgySummary.removeIf(x -> x.getTotalValue() == 0);
				if (this.dateStgySummary.size() > 0)
				{
					int    count   = 1;
					double baseVal = 0;
					for (DateStgySummary dateStgySummaryItem : dateStgySummary)
					{
						if (count == 1)
						{
							baseVal = dateStgySummaryItem.getTotalValue();
							this.stgyRelValuations.add(new StgyRelValuation(dateStgySummaryItem.getDate(), 100));
						} else
						{
							this.stgyRelValuations.add(new StgyRelValuation(dateStgySummaryItem.getDate(),
							        UtilPercentages.getProportion(baseVal, dateStgySummaryItem.getTotalValue(), 0)
							));
						}
						count++;
						
					}
				}
			}
		}
	}
	
}
