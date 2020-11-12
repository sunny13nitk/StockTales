package stocktales.basket.allocations.autoAllocation.strategy.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.basket.allocations.autoAllocation.pojos.ScAllocation;
import stocktales.basket.allocations.autoAllocation.strategy.interfaces.IStrategySrv;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.StAllocItem;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.StQuickStats;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.Strategy;
import stocktales.basket.allocations.autoAllocation.strategy.repo.IRepoStrategy;
import stocktales.helperPOJO.NameValDouble;
import stocktales.predicates.manager.PredicateManager;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class StrategySrv implements IStrategySrv
{
	@Autowired
	private IRepoStrategy repoStgy;
	
	@Autowired
	private PredicateManager predMgr;
	
	private Strategy strategy;
	
	@Override
	public void Initialize(
	)
	{
		this.strategy = new Strategy();
		
	}
	
	@Override
	public void loadAllocationItems(
	        List<ScAllocation> allocItems
	)
	{
		if (allocItems != null)
		{
			if (allocItems.size() > 0)
			{
				this.strategy.setAllocItems(new ArrayList<StAllocItem>());
				for (ScAllocation scAllocation : allocItems)
				{
					
					StAllocItem scallocItem = new StAllocItem();
					
					scallocItem.setSccode(scAllocation.getScCode());
					scallocItem.setMos(scAllocation.getMoS());
					scallocItem.setAlloc(scAllocation.getAllocation());
					
					this.strategy.addAllocationItem(scallocItem);
				}
			}
		}
		
	}
	
	@Override
	public void saveHeaderInfo(
	        Strategy stgyHeader
	)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Strategy save(
	        Strategy strategyPojo
	)
	{
		if (strategyPojo != null)
		{
			this.strategy.setName(strategyPojo.getName());
			this.strategy.setConcept(strategyPojo.getConcept());
			this.strategy.setPredicatebean(predMgr.getActivePredicateBeanName(null));
			this.strategy.setActive(strategyPojo.isActive());
			this.strategy.setRebalanceallowed(strategyPojo.isRebalanceallowed());
			this.strategy = repoStgy.save(this.strategy);
		}
		
		return this.strategy;
	}
	
	@Override
	public StQuickStats getQuickStats(
	)
	{
		StQuickStats qStats = new StQuickStats();
		
		if (this.getStrategy().getAllocItems() != null)
		{
			if (this.getStrategy().getAllocItems().size() > 0)
			{
				qStats.setNumScrips(strategy.getAllocItems().size());
				
				StAllocItem maxItem = Collections.max(strategy.getAllocItems(),
				        Comparator.comparing(x -> x.getAlloc()));
				if (maxItem != null)
				{
					qStats.setMaxAlloc(new NameValDouble(maxItem.getSccode(), maxItem.getAlloc()));
				}
				
				StAllocItem minItem = Collections.min(strategy.getAllocItems(),
				        Comparator.comparing(x -> x.getAlloc()));
				if (minItem != null)
				{
					qStats.setMinAlloc(new NameValDouble(minItem.getSccode(), minItem.getAlloc()));
				}
			}
		}
		
		return qStats;
	}
	
}
