package stocktales.basket.allocations.autoAllocation.strategy.rebalancing.interfaces;

import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.pojos.StgyRebalance;

public interface IStgyRebalanceSrv
{
	public StgyRebalance triggerReBalancingforStgy(
	        int stgId
	);
	
	public boolean addNewScrip(
	        String scCode
	);
	
	public boolean deleteScrip(
	        String scCode
	);
}
