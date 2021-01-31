package stocktales.money;

import org.apache.commons.math3.util.Precision;

public class UtilDecimaltoMoneyString
{
	public static String getMoneyStringforDecimal(
	        double decimalval, int scale
	)
	{
		String moneyStr = null;
		
		if (decimalval > 0)
		{
			decimalval = Precision.round(decimalval, scale);
			String s = Double.toString(decimalval);
			
			int dot = s.indexOf('.');
			
			if (dot < 4) // less than 1K
			{
				if (s.length() >= 12) // Cr . Case
				{
					moneyStr = s.substring(0, scale + 2) + " Cr.";
				} else
				{
					moneyStr = Double.toString(decimalval);
				}
			} else if (dot >= 4 && dot <= 5) // 1000 to 99999
			{
				decimalval = decimalval / 1000;
				decimalval = Precision.round(decimalval, scale);
				moneyStr   = Double.toString(decimalval) + " K";
			} else if (dot > 5 && dot <= 7) // 10000 to 99999
			{
				decimalval = decimalval / 100000;
				decimalval = Precision.round(decimalval, scale);
				moneyStr   = Double.toString(decimalval) + " Lacs";
			}
			
		}
		
		return moneyStr;
		
	}
}
