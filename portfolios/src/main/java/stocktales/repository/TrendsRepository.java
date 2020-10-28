package stocktales.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.scripsEngine.uploadEngine.entities.EN_SC_Trends;

@Repository
public interface TrendsRepository extends JpaRepository<EN_SC_Trends, Integer>
{
	public List<EN_SC_Trends> findAllBySCCodeIgnoreCase(
	        String scCode
	);
}
