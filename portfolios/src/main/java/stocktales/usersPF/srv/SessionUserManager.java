package stocktales.usersPF.srv;

import java.util.Optional;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.money.UtilDecimaltoMoneyString;
import stocktales.usersPF.helperPojo.PFBalDepSummary;
import stocktales.usersPF.intf.ISessionUserManager;
import stocktales.usersPF.model.UserPFConfig;
import stocktales.usersPF.model.UserStrategy;
import stocktales.usersPF.repo.RepoHoldings;
import stocktales.usersPF.repo.RepoUserPFConfig;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)

public class SessionUserManager implements ISessionUserManager
{
	@Autowired
	private RepoUserPFConfig repoUserPF;
	
	@Autowired
	private RepoHoldings repoHoldings;
	
	private String userName;
	
	private UserPFConfig userPFDetails;
	
	@Override
	public void loadPFDetails(
	)
	{
		if (this.userName != null)
		{
			Optional<UserPFConfig> userConfigO = repoUserPF.findByUsername(getUserName());
			if (userConfigO.isPresent())
			{
				userPFDetails = userConfigO.get();
			}
		}
		
	}
	
	@Override
	public PFBalDepSummary getPFBalDepSummary(
	)
	{
		
		PFBalDepSummary summary = null;
		loadPFDetails(); //Refresh
		if (userPFDetails != null)
		{
			summary = new PFBalDepSummary();
			summary.setCashBal(userPFDetails.getCashbalance());
			summary.setCashBalS(UtilDecimaltoMoneyString.getMoneyStringforDecimal(userPFDetails.getCashbalance(), 2));
			summary.setDeployBal(getTotalDeployments());
			if (summary.getDeployBal() > 0)
			{
				summary.setCashBalS(UtilDecimaltoMoneyString.getMoneyStringforDecimal(summary.getDeployBal(), 2));
				summary.setDeployPer(Precision.round((summary.getDeployBal() / summary.getCashBal()), 0));
			}
		}
		
		return summary;
	}
	
	/*
	 * -----------------------  PRIVATE METHODS ----------------------
	 */
	
	private double getTotalDeployments(
	)
	{
		double totalDep = 0;
		if (userPFDetails != null)
		{
			if (userPFDetails.getUserStrategies() != null)
			{
				if (userPFDetails.getUserStrategies().size() > 0)
				{
					double totalDeployment = 0;
					for (UserStrategy usrStgy : userPFDetails.getUserStrategies())
					{
						totalDeployment += repoHoldings.getTotalAllocation(usrStgy.getId());
						
					}
				}
			}
		}
		
		return totalDep;
	}
	
}
