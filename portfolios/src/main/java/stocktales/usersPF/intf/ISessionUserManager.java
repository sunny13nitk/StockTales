package stocktales.usersPF.intf;

import stocktales.usersPF.helperPojo.PFBalDepSummary;
import stocktales.usersPF.model.UserPFConfig;

public interface ISessionUserManager
{
	public String getUserName(
	);
	
	public UserPFConfig getUserPFDetails(
	);
	
	public void setUserName(
	        String userName
	);
	
	public void loadPFDetails(
	);
	
	public PFBalDepSummary getPFBalDepSummary(
	);
	
}
