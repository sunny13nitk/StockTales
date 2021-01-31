package stocktales.durations;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UtilDurations
{
	public static List<Integer> getYearsList(
	        int interval
	)
	{
		List<Integer> years = new ArrayList<Integer>();
		
		int year = Year.now().getValue();
		
		if (interval > 0)
		{
			for (int i = 0; i < interval; i++)
			{
				years.add(year);
				year++;
			}
			
		} else if (interval < 0)
		{
			for (int i = interval; i < 0; i++)
			{
				years.add(year);
				year--;
			}
			
		} else
		{
			years.add(year);
		}
		
		return years;
	}
	
	public static int getNumDaysbwSysDates(
	        Date fromDate, Date toDate
	) throws ParseException
	{
		int  numDays  = 0;
		long daysDiff = (toDate.getTime() - fromDate.getTime());
		long noOfDays = TimeUnit.DAYS.convert(daysDiff, TimeUnit.MILLISECONDS);
		long a        = TimeUnit.DAYS.toDays(noOfDays);
		numDays = (int) a;
		return numDays;
		
	}
	
	public static int getNumDaysbwSqlSysDates(
	        Date sqlDbDate, Date implDate
	) throws java.text.ParseException
	{
		int  numDays = 0;
		Date sqlDate = null;
		if (sqlDbDate != null && implDate != null)
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String     sqlDateStr = sqlDbDate.toString();
			
			sqlDate = dateFormat.parse(sqlDateStr);
			
			long daysDiff = (implDate.getTime() - sqlDate.getTime());
			long noOfDays = TimeUnit.DAYS.convert(daysDiff, TimeUnit.MILLISECONDS);
			long a        = TimeUnit.DAYS.toDays(noOfDays);
			numDays = (int) a;
		}
		
		return numDays;
	}
	
}
