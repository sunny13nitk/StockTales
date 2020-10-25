package stocktales.scripsEngine.utilities.interfaces;

import java.util.Date;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

public interface IDateService
{

	/**
	 * Get Number of Days between Sql Date and Current Util Date
	 * 
	 * @param sqlDbDate
	 *             - Date in Format yyyy-MM-dd as stored in Sql Db Tables
	 * @param implDate
	 *             - Date in format dd/mm/yyyy as generated by user
	 * @return - Integer number of Days difference
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	public int getNumDaysbwSqlSysDates(Date sqlDbDate, Date implDate) throws java.text.ParseException;

	public int getNumDaysbwSysDates(Date Date1, Date Date2) throws java.text.ParseException;

}
