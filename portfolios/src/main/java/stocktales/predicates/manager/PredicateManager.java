package stocktales.predicates.manager;

import stocktales.predicates.GenericSCEDRCSummaryPredicate;

public interface PredicateManager
{
	public GenericSCEDRCSummaryPredicate getActivePredicateBean(
	);
	
	public GenericSCEDRCSummaryPredicate getActivePredicateBean(
	        String predicateBeanNameVal
	);
}
