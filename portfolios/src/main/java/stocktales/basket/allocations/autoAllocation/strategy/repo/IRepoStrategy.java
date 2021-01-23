package stocktales.basket.allocations.autoAllocation.strategy.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import stocktales.basket.allocations.autoAllocation.strategy.interfaces.IStgyShort;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.Strategy;

public interface IRepoStrategy extends JpaRepository<Strategy, Integer>
{
	public List<Strategy> findAllByConceptIgnoreCase(
	        String concept
	);
	
	public IStgyShort findByStid(
	        int id
	);
}
