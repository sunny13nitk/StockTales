package stocktales.healthcheck.beanSrv.srv;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.healthcheck.beanSrv.helperPOJO.NameVal;
import stocktales.healthcheck.beanSrv.intf.IAvgSalesGrowth;
import stocktales.healthcheck.intf.IHC_Srv;
import stocktales.healthcheck.model.helperpojo.HCBeanReturn;

@Service("AvgSalesGrowth")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvgSalesGrowth implements IAvgSalesGrowth
{
	@Autowired
	private IHC_Srv hcSrv;
	
	@Autowired
	private MessageSource msgSrc;
	
	private List<NameVal> salesTrends = new ArrayList<NameVal>();
	
	private List<NameVal> positiveYrs = new ArrayList<NameVal>();;
	
	private List<NameVal> negativeYrs = new ArrayList<NameVal>();
	
	private HCBeanReturn beanReturn = new HCBeanReturn();
	
	@Override
	public HCBeanReturn returnAfterEvaluation(
	)
	{
		// TODO Auto-generated method stub
		return this.beanReturn;
	}
	
}
