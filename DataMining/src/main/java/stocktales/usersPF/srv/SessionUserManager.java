package stocktales.usersPF.srv;

import java.util.ArrayList;
import java.util.List;
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
import stocktales.basket.allocations.autoAllocation.strategy.interfaces.IStgyId;
import stocktales.basket.allocations.autoAllocation.strategy.repo.IRepoStrategy;
import stocktales.money.UtilDecimaltoMoneyString;
import stocktales.strategy.intf.IStrategyStatsSrv;
import stocktales.usersPF.helperPojo.PFBalDepSummary;
import stocktales.usersPF.helperPojo.UserStrategySnaphot;
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
	private IRepoStrategy repoStgy;
	
	@Autowired
	private RepoHoldings repoHoldings;
	
	private String userName;
	
	private UserPFConfig userPFDetails;
	
	@Autowired
	private IStrategyStatsSrv stgyStatsSrv;
	
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
				summary.setDeployBalS(UtilDecimaltoMoneyString.getMoneyStringforDecimal(summary.getDeployBal(), 2));
				summary.setDeployPer((int) (Precision.round((summary.getDeployBal() / summary.getCashBal()) * 100, 0)));
			}
		}
		
		return summary;
	}
	
	@Override
	public boolean CanUserSubscibeTOStrategies(
	)
	{
		
		boolean canSubscribe = false;
		int     numStgy      = repoStgy.getNumberofStrategies();
		
		int currStgy = this.userPFDetails.getUserStrategies().size();
		
		if (numStgy > currStgy)
		{
			canSubscribe = true;
		}
		
		return canSubscribe;
	}
	
	@Override
	public List<Integer> getSubscribableStrategies(
	)
	{
		
		List<IStgyId> stgIds         = null;
		List<Integer> suscribableStg = new ArrayList<Integer>();
		
		stgIds = repoStgy.getAllStrategyIds();
		for (IStgyId stgId : stgIds)
		{
			Optional<UserStrategy> ustGO = this.userPFDetails.getUserStrategies().stream()
			        .filter(x -> x.getStid() == stgId.getStid()).findFirst();
			if (!ustGO.isPresent())
			{
				suscribableStg.add(stgId.getStid());
			}
		}
		
		return suscribableStg;
		
	}
	
	@Override
	public List<UserStrategySnaphot> getUserStgySnapshots(
	) throws Exception
	{
		
		List<UserStrategySnaphot> usStgySnapshotList = null;
		
		if (userPFDetails != null)
		{
			if (userPFDetails.getUserStrategies() != null)
			{
				if (userPFDetails.getUserStrategies().size() > 0)
				{
					for (UserStrategy usStgy : userPFDetails.getUserStrategies())
					{
						UserStrategySnaphot usStgySS = new UserStrategySnaphot();
						usStgySS.setStgyStatsShort(stgyStatsSrv.getShortStatsforStrategy(usStgy.getStid()));
						usStgySS.setStgynumbers(repoHoldings.getStgyNumbers(usStgy.getId()));
						usStgySS.setActive(usStgy.isActive());
						usStgySS.setTotalAllocation(UtilDecimaltoMoneyString
						        .getMoneyStringforDecimal(usStgySS.getStgynumbers().getTotalAllocation(), 1));
						usStgySS.setRealzPL(UtilDecimaltoMoneyString
						        .getMoneyStringforDecimal(usStgySS.getStgynumbers().getTotalPL(), 1));
						
						usStgySS.setRealzDiv(UtilDecimaltoMoneyString
						        .getMoneyStringforDecimal(usStgySS.getStgynumbers().getTotalDiv(), 1));
						
						/*
						 * Loop on Holdings and Compile
						 */
					}
				}
			}
		}
		
		return usStgySnapshotList;
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
					
					for (UserStrategy usrStgy : userPFDetails.getUserStrategies())
					{
						if (usrStgy.getHoldings() != null)
						{
							if (usrStgy.getHoldings().size() > 0)
							{
								totalDep += repoHoldings.getTotalAllocation(usrStgy.getId());
							}
						}
						
					}
				}
			}
		}
		
		return totalDep;
	}
	
}
