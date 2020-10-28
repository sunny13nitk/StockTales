package stocktales.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import stocktales.basket.allocations.config.pojos.AllocationWeights;
import stocktales.basket.allocations.config.pojos.DurationWeights;
import stocktales.basket.allocations.config.pojos.FinancialSectors;

/*
 * ------------------ PROPERTY FILES BASED BEANS -----------------------
 */

@Configuration
@PropertySources(
    { @PropertySource("classpath:weights.properties"), @PropertySource("classpath:application.properties") }
)
public class PropertyConfig
{
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer properties(
	)
	{
		PropertySourcesPlaceholderConfigurer pSConf = new PropertySourcesPlaceholderConfigurer();
		return pSConf;
	}
	
	@Bean
	@Autowired //For PropertySourcesPlaceholderConfigurer
	public DurationWeights durationWeightsConfig(
	        @Value("${DurationWeights.wt3Y}") final double wt3Y, @Value(
	            "${DurationWeights.wt5Y}"
	        ) final double wt5Y, @Value("${DurationWeights.wt7Y}") final double wt7Y, @Value(
	            "${DurationWeights.wt10Y}"
	        ) final double wt10Y
	)
	
	{
		DurationWeights duWts = new DurationWeights(wt3Y, wt5Y, wt7Y, wt10Y);
		
		return duWts;
	}
	
	@Bean
	@Autowired //For PropertySourcesPlaceholderConfigurer
	public AllocationWeights allocationWeightsConfig(
	        @Value("${AllocationWeights.wtED}") final double wtED, @Value(
	            "${AllocationWeights.wtRR}"
	        ) final double wtRR, @Value("${AllocationWeights.wtCF}") final double wtCF
	)
	
	{
		AllocationWeights allocWts = new AllocationWeights(wtED, wtRR, wtCF);
		
		return allocWts;
	}
	
	@Bean
	@Autowired //For PropertySourcesPlaceholderConfigurer
	public FinancialSectors financialSectorsConfig(
	        @Value("${SectorName}") String sectorName
	)
	
	{
		FinancialSectors finSec = new FinancialSectors(sectorName);
		
		return finSec;
	}
}
