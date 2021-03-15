package stocktales.scsnapshot.model.pojo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockQuoteBasic
{
	private String scCode;
	private String sector;
	
	private double uph;
	private String name;
	
	private BigDecimal MCap;
	
	private String MCapTxt;
	
	private BigDecimal cmp;
	private BigDecimal dma200;
	
	private BigDecimal High52W;
	
	private BigDecimal Low52W;
	
	private BigDecimal epsCurr;
	
	private BigDecimal peRatio;
	
	private BigDecimal bvps;
	
	private BigDecimal pbRatio;
	
	private BigDecimal perChg200DMA;
	
	private BigDecimal perChg50DMA;
	
	private BigDecimal perChg52WkLow;
	
	private BigDecimal perChg52WkHigh;
	
}
