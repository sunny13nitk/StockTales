package stocktales.predicates.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import stocktales.predicates.GenericSCEDRCSummaryPredicate;

@Component
public class PredicateManagerImpl implements PredicateManager
{
	@Autowired
	private ApplicationContext appCtxt;
	
	@Value("${predicateBeanName}")
	private String predicateBeanName;
	
	/*
	 * Using #Predicate Bean Qualifier -predicateBeanName= ValueHuntPredicate
	 */
	
	@Override
	public GenericSCEDRCSummaryPredicate getActivePredicateBean(
	)
	{
		GenericSCEDRCSummaryPredicate predBean = null;
		if (appCtxt != null && predicateBeanName != null)
		{
			predBean = (GenericSCEDRCSummaryPredicate) appCtxt.getBean(predicateBeanName);
		}
		
		return predBean;
	}
	
	/*
	 * Using External Called Bean Name : Can get from UI by selecting Bean Name to show diff Filter Strategies
	 */
	@Override
	public GenericSCEDRCSummaryPredicate getActivePredicateBean(
	        String predicateBeanNameVal
	)
	{
		GenericSCEDRCSummaryPredicate predBean = null;
		
		if (appCtxt != null)
		{
			if (predicateBeanNameVal != null)
			{
				if (predicateBeanName.trim().length() > 0)
				{
					predBean = (GenericSCEDRCSummaryPredicate) appCtxt.getBean(predicateBeanNameVal);
				}
			} else
			{
				predBean = (GenericSCEDRCSummaryPredicate) appCtxt.getBean(predicateBeanName);
			}
		}
		
		return predBean;
	}
	
}
