package stocktales.healthcheck.intf;

import stocktales.healthcheck.enums.EnumHCCriteria;
import stocktales.healthcheck.model.helperpojo.HCBeanReturn;

/*
 * To Be Implemented by Every Figure Getter Bean Used in Scrip Health Check
 */
public interface IHC_GetEvaluationResults
{
	
	public HCBeanReturn returnAfterEvaluation(
	        String scCode
	);
	
	public EnumHCCriteria getCriteria(
	);
}
