package stocktales.scripsEngine.uploadEngine.scDataContainer.services.interfaces;

import java.lang.reflect.Method;
import java.util.ArrayList;

import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scDataContainer.DAO.types.scDataContainer;

public interface ISCDataContainerSrv
{
	
	public void load(
	        String scCode
	) throws EX_General, Exception;
	
	public <T> ArrayList<T> load(
	        String scCode, String bobjName
	);
	
	public scDataContainer getScDC(
	);
	
	public Method getMethodfromSCDataContainer(
	        String sheetName, char Type
	);
}
