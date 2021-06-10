package stocktales.historicalPrices.utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Precision;

import stocktales.historicalPrices.pojo.StockCurrQuote;
import stocktales.historicalPrices.pojo.StockHistory;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class StockPricesUtility
{
	/**
	 * Get Historical Prices for List of Scrips as per Choice of Interval(Year/Month/Week), Amount(number) ,
	 *  Frequency of Prices Lookout (Daily/Weekly/Monthly) within Interval
	 * @param scrips - Array Of Scrips Symbols
	 * @param interval - Calendar ENum
	 * @param amount - Amount of Intervals to Traverse Back
	 * @param frequency - Interval Enum from Yahoo.Finance
	 * @return - List of Stock symbols and their Historical Prices
	 * @throws Exception
	 */
	public static List<StockHistory> getHistoricalPricesforScrips(
	        String[] scrips, int interval, int amount, Interval frequency
	) throws Exception
	{
		List<StockHistory> stocksPrices = null;
		
		//1. Format the scrips symbols with exchange Info
		
		if (scrips != null)
		{
			if (scrips.length > 0)
			{
				for (int i = 0; i < scrips.length; i++)
				{
					scrips[i] = scrips[i] + ".NS";
				}
				
				Map<String, Stock> stocks = YahooFinance.get(scrips, true);
				if (stocks != null)
				{
					SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
					stocksPrices = new ArrayList<StockHistory>();
					if (stocks.size() > 0)
					{
						for (Map.Entry<String, Stock> stockset : stocks.entrySet())
						{
							Stock stock = stockset.getValue();
							if (stock != null)
							{
								//New Stock History
								StockHistory stHist = new StockHistory();
								String       symbol = stock.getSymbol();
								if (symbol != null)
								{
									String[] scNames = symbol.split("\\.");
									if (scNames.length > 1)
									{
										stHist.setScCode(scNames[0]);
									}
								}
								
								//Prepare the Duration
								Calendar from = Calendar.getInstance();
								Calendar to   = Calendar.getInstance();
								from.add(interval, amount * -1); // from Calendar.Interval Amounts ago
								
								List<HistoricalQuote> HistQuotes = stock.getHistory(from, to, frequency);
								if (HistQuotes != null)
								{
									if (HistQuotes.size() > 0)
									{
										for (HistoricalQuote hQuote : HistQuotes)
										{
											if (hQuote != null)
											{
												if (hQuote.getDate() != null && hQuote.getClose() != null)
												{
													stocktales.historicalPrices.pojo.HistoricalQuote newQuote = new stocktales.historicalPrices.pojo.HistoricalQuote(
													        format1.format(hQuote.getDate().getTime()),
													        Precision.round(hQuote.getClose().doubleValue(), 1));
													stHist.getPriceHistory().add(newQuote);
												}
											}
										}
									}
								}
								stocksPrices.add(stHist);
							}
							
						}
					}
				}
				
			}
		}
		
		return stocksPrices;
	}
	
	/**
	 * Get Current Prices for Scrips List
	 * @param scrips - List of Scrips
	 * @return - Current Scrips Prices
	 * @throws Exception
	 */
	public static List<StockCurrQuote> getCurrentPricesforScrips(
	        String[] scrips
	) throws Exception
	{
		List<StockCurrQuote> stocksPrices = null;
		
		//1. Format the scrips symbols with exchange Info
		
		if (scrips != null)
		{
			if (scrips.length > 0)
			{
				for (int i = 0; i < scrips.length; i++)
				{
					scrips[i] = scrips[i] + ".NS";
				}
				
				Map<String, Stock> stocks = YahooFinance.get(scrips);
				if (stocks != null)
				{
					
					stocksPrices = new ArrayList<StockCurrQuote>();
					if (stocks.size() > 0)
					{
						for (Map.Entry<String, Stock> stockset : stocks.entrySet())
						{
							Stock stock = stockset.getValue();
							if (stock != null)
							{
								//New Stock History
								StockCurrQuote stCP   = new StockCurrQuote();
								String         symbol = stock.getSymbol();
								if (symbol != null)
								{
									String[] scNames = symbol.split("\\.");
									if (scNames.length > 1)
									{
										stCP.setScCode(scNames[0]);
									}
									
									stCP.setCurrPrice(Precision.round(stock.getQuote().getPrice().doubleValue(), 1));
									stocksPrices.add(stCP);
								}
								
							}
							
						}
					}
				}
				
			}
		}
		
		return stocksPrices;
	}
	
}
