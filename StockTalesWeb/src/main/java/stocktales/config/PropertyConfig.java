package stocktales.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;

import stocktales.basket.allocations.config.pojos.AllocationWeights;
import stocktales.basket.allocations.config.pojos.DurationWeights;
import stocktales.basket.allocations.config.pojos.FinancialSectors;
import stocktales.basket.allocations.config.pojos.FinancialsConfig;
import stocktales.basket.allocations.config.pojos.MCapAllocations;
import stocktales.basket.allocations.config.pojos.StrengthWeights;

/*
 * ------------------ PROPERTY FILES BASED BEANS -----------------------
 */

@Configuration
@PropertySources(
    { @PropertySource("classpath:weights.properties"), @PropertySource(
        "classpath:application.properties"
    ), @PropertySource("classpath:messages.properties"), @PropertySource(
        "classpath:HCMessages.properties"
    ), @PropertySource("classpath:HCRep.properties") }
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
	
	@Bean
	@Autowired //For PropertySourcesPlaceholderConfigurer
	public StrengthWeights strenghtWeightsConfig(
	        @Value("${StrengthWeights.EDRC}") final double EDRC, @Value("${StrengthWeights.ValR}") final double valR
	)
	{
		StrengthWeights strengthWts = new StrengthWeights(EDRC, valR);
		
		return strengthWts;
	}
	
	@Bean
	@Autowired //For PropertySourcesPlaceholderConfigurer
	public FinancialsConfig setFinConfig(
	        @Value("${Financials.UPH}") final double UPH, @Value("${Financials.ROE}") final double ROE, @Value(
	            "${Financials.BOOSTBest}"
	        ) final double BoostBest, @Value("${Financials.BOOSTROE}") final double BoostROE, @Value(
	            "${Financials.BOOSTBASE}"
	        ) final double BoostBase
	)
	{
		FinancialsConfig FinConfig = new FinancialsConfig(UPH, ROE, BoostBest, BoostROE, BoostBase);
		
		return FinConfig;
	}
	
	@Bean
	@Autowired //For PropertySourcesPlaceholderConfigurer
	public MCapAllocations MCapAllocationsConfig(
	        @Value("${MCapValue}") final double MCap, @Value("${MaxAllocLimit}") final double allocMax
	)
	{
		MCapAllocations mCapAlloc = new MCapAllocations(MCap, allocMax);
		
		return mCapAlloc;
	}
	
	@Bean
	public ResourceBundleMessageSource messageSource(
	)
	{
		
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.addBasenames("messages");
		source.addBasenames("HCMessages");
		source.addBasenames("HCRep");
		source.addBasenames("HCTags");
		source.setUseCodeAsDefaultMessage(true);
		
		return source;
	}
	
}
