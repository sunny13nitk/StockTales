package stocktales.usersPF.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.usersPF.model.Holding;

@Repository
public interface RepoHoldings extends JpaRepository<Holding, Integer>
{
	
	//Get Total Allocation by Strategy User strategyID (! Strategy ID)
	@Query("Select SUM( units * ppu) from Holding where userStrategy.id = ?1")
	public double getTotalAllocation(
	        long userStgyId
	);
	
	//Get Total Dividends Realized by Strategy User strategyID 
	@Query("Select SUM( realzdiv) from Holding where userStrategy.id = ?1")
	public double getTotalDividends(
	        long userStgyId
	);
	
	//Get Total P&L Realized by Strategy User strategyID 
	@Query("Select SUM( realzpl) from Holding where userStrategy.id = ?1")
	public double getTotalPandL(
	        long userStgyId
	);
}
