package stocktales.basket.allocations.config.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import stocktales.basket.allocations.config.pojos.CalibrationItem;

/*
 * Impacting SS by UPH (Un-pledged Promoter holding), Valuations Ratio by Market, WC cycle:( REceivebales + Inventory )/ Sales, 
 *  IDByPAT : ( Interest + Depriciation) /PAT
 */
@Component
@Getter
@Setter
public class CLRConfig implements CommandLineRunner
{
	
	/*
	 * Valuation Soothing for Very High and Low ValR
	 * - Extremely High - Risk of Time correction
	 * - Low - Risk of weaker business model or other risks that market is factoring in the growth
	 */
	private List<CalibrationItem> valSoothConfigL = new ArrayList<CalibrationItem>();
	
	private List<CalibrationItem> UPHConfigL = new ArrayList<CalibrationItem>();
	
	private List<CalibrationItem> WCConfigL = new ArrayList<CalibrationItem>();
	
	private List<CalibrationItem> IDBPConfigL = new ArrayList<CalibrationItem>();
	
	private List<CalibrationItem> CFOPATConfigL = new ArrayList<CalibrationItem>();
	
	@Override
	public void run(
	        String... args
	) throws Exception
	{
		
		//Populate Value Soothing Strength Score List
		/*
		 * Tone down on both the Expensive and too cheap market perception
		 */
		CalibrationItem zone1 = new CalibrationItem(0, 3, .8);
		valSoothConfigL.add(zone1);
		CalibrationItem zone2 = new CalibrationItem(3, 5, .85);
		valSoothConfigL.add(zone2);
		CalibrationItem zone3 = new CalibrationItem(5, 12, 1);
		valSoothConfigL.add(zone3);
		CalibrationItem zone4 = new CalibrationItem(12, 18, .95);
		valSoothConfigL.add(zone4);
		CalibrationItem zone5 = new CalibrationItem(18, 20, .92);
		valSoothConfigL.add(zone5);
		CalibrationItem zone6 = new CalibrationItem(20, 25, .88);
		valSoothConfigL.add(zone6);
		CalibrationItem zone7 = new CalibrationItem(25, 30, .85);
		valSoothConfigL.add(zone7);
		CalibrationItem zone8 = new CalibrationItem(30, 35, .8);
		valSoothConfigL.add(zone8);
		CalibrationItem zone9 = new CalibrationItem(35, 40, .76);
		valSoothConfigL.add(zone9);
		CalibrationItem zone10 = new CalibrationItem(40, 50, .72);
		valSoothConfigL.add(zone10);
		CalibrationItem zone11 = new CalibrationItem(50, 200, .68);
		valSoothConfigL.add(zone11);
		
		//Populate UPH Calibration
		/*
		 * REWARD High Promoter Equity respect and disregard high dilution to Scrip strength
		 */
		CalibrationItem pzone1 = new CalibrationItem(0, 50, .85);
		UPHConfigL.add(pzone1);
		CalibrationItem pzone2 = new CalibrationItem(50, 55, .9);
		UPHConfigL.add(pzone2);
		CalibrationItem pzone3 = new CalibrationItem(55, 60, .92);
		UPHConfigL.add(pzone3);
		CalibrationItem pzone4 = new CalibrationItem(60, 65, .95);
		UPHConfigL.add(pzone4);
		CalibrationItem pzone5 = new CalibrationItem(65, 70, 1);
		UPHConfigL.add(pzone5);
		CalibrationItem pzone6 = new CalibrationItem(70, 73, 1.05);
		UPHConfigL.add(pzone6);
		CalibrationItem pzone7 = new CalibrationItem(73, 90, 1.08);
		UPHConfigL.add(pzone7);
		
		//Populate WC Calibration
		/*
		 * REWARD leaner WC cycle and punish the fatter ones
		 */
		CalibrationItem wzone1 = new CalibrationItem(0, 12, 1.07);
		WCConfigL.add(wzone1);
		CalibrationItem wzone2 = new CalibrationItem(12, 20, .97);
		WCConfigL.add(wzone2);
		CalibrationItem wzone3 = new CalibrationItem(20, 25, .95);
		WCConfigL.add(wzone3);
		CalibrationItem wzone4 = new CalibrationItem(25, 30, .92);
		WCConfigL.add(wzone4);
		CalibrationItem wzone5 = new CalibrationItem(30, 40, .88);
		WCConfigL.add(wzone5);
		CalibrationItem wzone6 = new CalibrationItem(40, 100, .8);
		WCConfigL.add(wzone6);
		
		//Populate IDBPAT Calibration
		/*
		 * REWARD leaner WIDBPAT cycle and punish the fatter ones
		 */
		CalibrationItem izone1 = new CalibrationItem(0, 20, 1);
		IDBPConfigL.add(izone1);
		CalibrationItem izone2 = new CalibrationItem(20, 30, .95);
		IDBPConfigL.add(izone2);
		CalibrationItem izone3 = new CalibrationItem(30, 70, .9);
		IDBPConfigL.add(izone3);
		
		//Populate CFO/PAT Calibration
		/*
		 * REWARD where CFO PAT Adherence is close to 100 and punish ones deviating chances of profits 
		 * being real inc. the risk
		 */
		CalibrationItem czone1 = new CalibrationItem(0, 50, .7);
		CFOPATConfigL.add(czone1);
		CalibrationItem czone2 = new CalibrationItem(50, 75, .8);
		CFOPATConfigL.add(czone2);
		CalibrationItem czone3 = new CalibrationItem(75, 85, .9);
		CFOPATConfigL.add(czone3);
		CalibrationItem czone4 = new CalibrationItem(85, 95, .95);
		CFOPATConfigL.add(czone4);
		CalibrationItem czone5 = new CalibrationItem(95, 200, 1.03);
		CFOPATConfigL.add(czone5);
		
	}
	
}
