package stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces;

import java.util.List;

import stocktales.scripsEngine.uploadEngine.entities.EN_SC_GeneralQ;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;

/**
 * 
 * Scrip exists in DB Service
 */
public interface ISCExistsDB_Srv
{
	public boolean Is_ScripExisting_DB(
	        String scCode
	) throws EX_General;
	
	public EN_SC_GeneralQ Get_ScripExisting_DB(
	        String scCode
	) throws EX_General;
	
	public boolean Is_ScripExisting_DB_DescSW(
	        String scDesc
	) throws EX_General;
	
	public EN_SC_GeneralQ Get_ScripExisting_DB_DescSW(
	        String scDesc
	) throws EX_General;
	
	public List<EN_SC_GeneralQ> getAllScripsHeaders(
	) throws EX_General;
	
	public List<String> getAllScripNames(
	) throws EX_General;
}
