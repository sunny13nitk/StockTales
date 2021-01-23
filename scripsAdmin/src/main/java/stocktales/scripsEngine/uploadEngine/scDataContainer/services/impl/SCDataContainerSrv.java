package stocktales.scripsEngine.uploadEngine.scDataContainer.services.impl;

import java.lang.reflect.Method;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scDataContainer.DAO.interfaces.ISCDataContainerDAO;
import stocktales.scripsEngine.uploadEngine.scDataContainer.DAO.types.scDataContainer;
import stocktales.scripsEngine.uploadEngine.scDataContainer.services.interfaces.ISCDataContainerSrv;

@Service
public class SCDataContainerSrv implements ISCDataContainerSrv
{
	@Autowired
	private ISCDataContainerDAO scDCDao;
	
	private scDataContainer scDC;
	
	@Override
	@Transactional
	public void load(
	        String scCode
	) throws EX_General, Exception
	{
		if (scDCDao != null)
		{
			this.scDC = scDCDao.load(scCode);
		}
	}
	
	@Override
	public void load(
	        String scCode, String sheetName
	) throws Exception
	{
		if (scDCDao != null)
		{
			this.scDC = scDCDao.load(scCode, sheetName);
		}
		
	}
	
	@Override
	public scDataContainer getScDC(
	)
	{
		// TODO Auto-generated method stub
		return this.scDC;
	}
	
	@Override
	public Method getMethodfromSCDataContainer(
	        String sheetName, char Type
	)
	{
		
		return scDCDao.getMethodfromSCDataContainer(sheetName, Type);
	}
	
}
