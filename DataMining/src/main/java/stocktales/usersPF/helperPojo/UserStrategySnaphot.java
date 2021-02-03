package stocktales.usersPF.helperPojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.strategy.helperPOJO.StgyStatsShortSummary;
import stocktales.usersPF.repo.intf.IUserStgyNumbers;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserStrategySnaphot
{
	private StgyStatsShortSummary stgyStatsShort;
	
	private String totalAllocation;
	
	private String realzDiv;
	
	private String realzPL;
	
	private boolean active;
	
	private IUserStgyNumbers stgynumbers;
	
	private List<UserHoldingStats> holdingsStatsList = new ArrayList<UserHoldingStats>();
	
}
