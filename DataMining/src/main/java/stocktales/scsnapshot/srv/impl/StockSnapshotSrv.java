package stocktales.scsnapshot.srv.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Optional;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.basket.allocations.autoAllocation.facades.interfaces.EDRCFacade;
import stocktales.basket.allocations.autoAllocation.valuations.interfaces.SCValuationSrv;
import stocktales.healthcheck.beanSrv.intf.IScDataContSrv;
import stocktales.money.UtilDecimaltoMoneyString;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_GeneralQ;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_Last4QData;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_Trends;
import stocktales.scripsEngine.uploadEngine.scDataContainer.services.interfaces.ISCDataContainerSrv;
import stocktales.scsnapshot.model.pojo.StockFundamentals;
import stocktales.scsnapshot.model.pojo.StockMCapLast4QData;
import stocktales.scsnapshot.model.pojo.StockQuoteBasic;
import stocktales.scsnapshot.model.pojo.StockRevWCCycleTrends;
import stocktales.scsnapshot.model.pojo.StockSnapshot;
import stocktales.scsnapshot.model.pojo.StockTrends;
import stocktales.scsnapshot.srv.intf.IStockSnapshotSrv;
import stocktales.services.interfaces.ScripService;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

@Service
public class StockSnapshotSrv implements IStockSnapshotSrv
{
	@Autowired
	private IScDataContSrv scBalSheetSrv;
	
	@Autowired
	private ScripService scSrv;
	
	@Autowired
	private ISCDataContainerSrv scDCSrv;
	
	@Autowired
	private SCValuationSrv scValSrv;
	
	@Autowired
	private EDRCFacade edrcSrv;
	
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
			MathContext m1 = new MathContext(1);
			MathContext m0 = new MathContext(0);
			
			StockQuoteBasic ssB = new StockQuoteBasic();
			ssB.setScCode(scCode);
			
			ssB.setBvps(stock.getStats().getBookValuePerShare().round(m1));
			ssB.setCmp(stock.getQuote().getPrice().round(m0));
			ssB.setDma50(stock.getQuote().getPriceAvg50().round(m0));
			ssB.setDma200(stock.getQuote().getPriceAvg200().round(m0));
			ssB.setEpsCurr(stock.getStats().getEps().round(m1));
			ssB.setHigh52W(stock.getQuote().getYearHigh().round(m0));
			ssB.setLow52W(stock.getQuote().getYearLow().round(m0));
			ssB.setMCap(stock.getStats().getMarketCap().round(m0));
			ssB.setMCapTxt(UtilDecimaltoMoneyString.getMoneyStringforDecimal(ssB.getMCap().doubleValue(), 0));
			ssB.setName(stock.getName());
			ssB.setPbRatio(stock.getStats().getPriceBook().round(m1));
			ssB.setPeRatio(stock.getStats().getPe().round(m1));
			ssB.setPerChg200DMA(stock.getQuote().getChangeFromAvg200InPercent().round(m0));
			ssB.setPerChg50DMA(stock.getQuote().getChangeFromAvg50InPercent().round(m0));
			ssB.setPerChg52WkHigh(stock.getQuote().getChangeFromYearHighInPercent().round(m0));
			ssB.setPerChg52WkLow(stock.getQuote().getChangeFromYearLowInPercent().round(m0));
			
			if (scDCSrv != null)
			{
				scDCSrv.load(scCode);
				if (scDCSrv.getScDC() != null)
				{
					ssB.setSector(scDCSrv.getScDC().getSCRoot().getSector());
					ssB.setUph(scDCSrv.getScDC().getSCRoot().getUPH());
					ssB.setPeg(scDCSrv.getScDC().getSCRoot().getPEG());
					ssB.setCps(Precision.round(scDCSrv.getScDC().getSCRoot().getCPS(), 0));
					if (scDCSrv.getScDC().getTenYData() != null)
					{
						
						double     fcf   = scDCSrv.getScDC().getTenYData().getFCF();
						BigDecimal cfcCr = new BigDecimal(fcf * 10000000);
						double     cffld = (cfcCr.doubleValue() / ssB.getMCap().doubleValue()) * 100;
						
						ssB.setCFOPAT(scDCSrv.getScDC().getTenYData().getCFOPATR());
						ssB.setFCFCFO(scDCSrv.getScDC().getTenYData().getFCFCFOR());
						
						ssB.setFcfyield(Precision.round(cffld, 1));
					}
					
					if (scSrv != null)
					{
						ssB.setFinancial(scSrv.isScripBelongingToFinancialSector(scCode));
					}
					
				}
				
			}
			
			this.ss.setQuoteBasic(ssB);
			populateTargetPrice();
			populateEDRCSummary();
			populateLast4QData();
			populateFundamentals();
			populateTrends();
		}
		
		return ss;
	}
	
	/*
	 * Populate the Target Price
	 */
	private void populateTargetPrice(
	)
	{
		if (scValSrv != null && this.ss.getQuoteBasic().getCmp().doubleValue() > 0)
		{
			this.ss.setTargetPrice(scValSrv.getValuationforScrip(this.ss.getQuoteBasic().getScCode(),
			        this.ss.getQuoteBasic().getCmp().doubleValue(), 1));
		}
	}
	
	/*
	 * Populate EDRC Summary
	 */
	private void populateEDRCSummary(
	)
	{
		if (edrcSrv != null)
		{
			this.ss.setSummary(edrcSrv.getEDRCforSCrip(this.ss.getQuoteBasic().getScCode()));
		}
	}
	
	private void populateLast4QData(
	)
	{
		if (this.ss.getQuoteBasic().getMCap().doubleValue() > 0)
		{
			if (this.scDCSrv.getScDC() != null)
			{
				EN_SC_Last4QData enLast4QData = this.scDCSrv.getScDC().getLast4QData();
				if (enLast4QData != null)
				{
					BigDecimal ttmsalesCr = new BigDecimal(enLast4QData.getSales4Q() * 10000000);
					if (ttmsalesCr.doubleValue() > 0)
					{
						this.ss.setLast4QData(new StockMCapLast4QData());
						this.ss.getLast4QData().setMcapToSales(Precision.round(
						        (this.ss.getQuoteBasic().getMCap().doubleValue() / ttmsalesCr.doubleValue()), 1));
						this.ss.getLast4QData().setOPM(enLast4QData.getOPM4Q());
						this.ss.getLast4QData().setNPM(enLast4QData.getNPM4Q());
						
					}
					
				}
			}
		}
	}
	
	private void populateFundamentals(
	)
	{
		if (this.scDCSrv.getScDC() != null)
		{
			Optional<EN_SC_Trends> trends3YO = this.scDCSrv.getScDC().getTrends_L().stream()
			        .filter(x -> x.getPeriod().equals("3Yr")).findFirst();
			if (trends3YO.isPresent())
			{
				EN_SC_Trends trends3Y = trends3YO.get();
				if (trends3Y != null)
				{
					this.ss.setFundamentals(new StockFundamentals());
					this.ss.getFundamentals().setROCE3Y(trends3Y.getROCEAvg());
					this.ss.getFundamentals().setROE3Y(trends3Y.getROEAvg());
					
					this.ss.getFundamentals().setRecvInvBySales3Y(trends3Y.getWCCAvg());
					this.ss.getFundamentals().setIntDepByPAT3Y(trends3Y.getFViabAvg());
					
					EN_SC_GeneralQ enRoot = this.scDCSrv.getScDC().getSCRoot();
					if (enRoot != null)
					{
						this.ss.getFundamentals().setSGToCapex(Precision.round(enRoot.getSGToCapex() * 100, 0));
						this.ss.getFundamentals().setDeRatio(Precision.round(enRoot.getDERatio(), 2));
					}
					
					this.ss.getFundamentals().setDivGrowth3Y(trends3Y.getDivGR());
					this.ss.getFundamentals().setDivPayout3Y(trends3Y.getDivPayAvg());
					
					this.ss.getFundamentals().setAvgSSGR3Y(trends3Y.getSSGRAvg());
					this.ss.getFundamentals().setSalesG3Y(trends3Y.getSalesGR());
					
				}
			}
		}
	}
	
	private void populateTrends(
	)
	{
		if (this.scDCSrv.getScDC() != null)
		{
			List<EN_SC_Trends> trendsList = scDCSrv.getScDC().getTrends_L();
			if (trendsList != null)
			{
				if (trendsList.size() > 0)
				{
					this.ss.setTrends(new StockTrends());
					
					for (EN_SC_Trends trend : trendsList)
					{
						/*
						 *  New Trends - Initialize
						 */
						
						StockRevWCCycleTrends revWCTrend = new StockRevWCCycleTrends();
						revWCTrend.setPeriod(trend.getPeriod());
						revWCTrend.setRevenue(trend.getSalesGR());
						revWCTrend.setRecv(trend.getRecvTR());
						revWCTrend.setWcByrev(trend.getWCCAvg());
						revWCTrend.setSsgr(trend.getSSGRAvg());
						
						/*
						 * Adding to Final Structures Here
						 */
						
						this.ss.getTrends().getRevWCTrends().add(revWCTrend);
						
					}
					
				}
			}
		}
	}
}
