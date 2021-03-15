package stocktales.scsnapshot.srv.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.healthcheck.beanSrv.intf.IScDataContSrv;
import stocktales.money.UtilDecimaltoMoneyString;
import stocktales.scripsEngine.uploadEngine.scDataContainer.services.interfaces.ISCDataContainerSrv;
import stocktales.scsnapshot.model.pojo.StockQuoteBasic;
import stocktales.scsnapshot.model.pojo.StockSnapshot;
import stocktales.scsnapshot.srv.intf.IStockSnapshotSrv;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

@Service
public class StockSnapshotSrv implements IStockSnapshotSrv
{
	@Autowired
	private IScDataContSrv scBalSheetSrv;
	
	@Autowired
	private ISCDataContainerSrv scDCSrv;
	
	private StockSnapshot ss;
	
	@Override
	public StockSnapshot getStockSnapshot(
	        String scCode
	) throws Exception
	{
		
		Stock stock = YahooFinance.get(scCode + ".NS");
		if (stock != null)
		{
			ss = new StockSnapshot();
			
			StockQuoteBasic ssB = new StockQuoteBasic();
			ssB.setScCode(scCode);
			
			ssB.setBvps(stock.getStats().getBookValuePerShare());
			ssB.setCmp(stock.getQuote().getPrice());
			ssB.setDma200(stock.getQuote().getPriceAvg200());
			ssB.setEpsCurr(stock.getStats().getEps());
			ssB.setHigh52W(stock.getQuote().getYearHigh());
			ssB.setLow52W(stock.getQuote().getYearLow());
			ssB.setMCap(stock.getStats().getMarketCap());
			ssB.setMCapTxt(UtilDecimaltoMoneyString.getMoneyStringforDecimal(ssB.getMCap().doubleValue(), 0));
			ssB.setName(stock.getName());
			ssB.setPbRatio(stock.getStats().getPriceBook());
			ssB.setPeRatio(stock.getStats().getPe());
			ssB.setPerChg200DMA(stock.getQuote().getChangeFromAvg200InPercent());
			ssB.setPerChg50DMA(stock.getQuote().getChangeFromAvg50InPercent());
			ssB.setPerChg52WkHigh(stock.getQuote().getChangeFromYearHighInPercent());
			ssB.setPerChg52WkLow(stock.getQuote().getChangeFromYearLowInPercent());
			
			if (scDCSrv != null)
			{
				scDCSrv.load(scCode);
				if (scDCSrv.getScDC() != null)
				{
					ssB.setSector(scDCSrv.getScDC().getSCRoot().getSector());
					ssB.setUph(scDCSrv.getScDC().getSCRoot().getUPH());
				}
			}
			
			this.ss.setQuoteBasic(ssB);
			
		}
		
		return ss;
	}
	
}
