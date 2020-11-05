package stocktales.basket.allocations.autoAllocation.pojos;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.basket.allocations.autoAllocation.interfaces.IScAllocationListRepo;
import stocktales.helperPOJO.ErrorsValidation;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ScAllocationListRepo implements IScAllocationListRepo
{
	private List<ScAllocation> scAllocList = new ArrayList<ScAllocation>();
	
	@Override
	public double getTotalAllocationPercentage(
	)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public ErrorsValidation isAllocationValid(
	)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void addNew(
	        String scCode
	)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void delete(
	        String ScCode
	)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void stageAllocationsforScrips(
	        List<String> scrips
	)
	{
		// Set current Allocation List
		
	}
}
