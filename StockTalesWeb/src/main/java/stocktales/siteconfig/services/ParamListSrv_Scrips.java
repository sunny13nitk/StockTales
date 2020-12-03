package stocktales.siteconfig.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.services.interfaces.ScripService;
import stocktales.siteconfig.interfaces.IParamList;

@Service("AllScripNames")
public class ParamListSrv_Scrips implements IParamList
{
	@Autowired
	private ScripService scSrv;
	
	@Override
	public List<String> getParams(
	)
	{
		List<String> params = null;
		try
		{
			params = scSrv.getAllScripNames();
		} catch (EX_General e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return params;
	}
	
}
