package stocktales.basket.allocations.config.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import stocktales.basket.allocations.config.pojos.ValSoothingSS;

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
	private List<ValSoothingSS> valSoothConfigL = new ArrayList<ValSoothingSS>();
	
	@Override
	public void run(
	        String... args
	) throws Exception
	{
		
		//Populate Value Soothing Strength Score List
		ValSoothingSS zone1 = new ValSoothingSS(0, 3, .8);
		valSoothConfigL.add(zone1);
		ValSoothingSS zone2 = new ValSoothingSS(3, 5, .85);
		valSoothConfigL.add(zone2);
		ValSoothingSS zone3 = new ValSoothingSS(5, 12, 1);
		valSoothConfigL.add(zone3);
		ValSoothingSS zone4 = new ValSoothingSS(12, 18, .95);
		valSoothConfigL.add(zone4);
		ValSoothingSS zone5 = new ValSoothingSS(18, 20, .92);
		valSoothConfigL.add(zone5);
		ValSoothingSS zone6 = new ValSoothingSS(20, 25, .88);
		valSoothConfigL.add(zone6);
		ValSoothingSS zone7 = new ValSoothingSS(25, 30, .85);
		valSoothConfigL.add(zone7);
		ValSoothingSS zone8 = new ValSoothingSS(30, 35, .8);
		valSoothConfigL.add(zone8);
		ValSoothingSS zone9 = new ValSoothingSS(35, 40, .76);
		valSoothConfigL.add(zone9);
		ValSoothingSS zone10 = new ValSoothingSS(40, 50, .72);
		valSoothConfigL.add(zone10);
		ValSoothingSS zone11 = new ValSoothingSS(50, 200, .68);
		valSoothConfigL.add(zone11);
	}
	
}
