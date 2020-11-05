package stocktales.basket.allocations.autoAllocation.interfaces;

import java.util.List;

import stocktales.basket.allocations.autoAllocation.pojos.ScAllocation;
import stocktales.helperPOJO.ErrorsValidation;

public interface IScAllocationListRepo
{
	public List<ScAllocation> getScAllocList(
	);
	
	public double getTotalAllocationPercentage(
	);
	
	public ErrorsValidation isAllocationValid(
	);
	
	public void addNew(
	        String scCode
	);
	
	public void delete(
	        String ScCode
	);
	
	public void stageAllocationsforScrips(
	        List<String> scrips
	);
	
}
