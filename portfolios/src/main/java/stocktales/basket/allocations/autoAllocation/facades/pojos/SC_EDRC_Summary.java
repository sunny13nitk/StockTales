package stocktales.basket.allocations.autoAllocation.facades.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SC_EDRC_Summary
{
	private String scCode;
	private String sector;
	private double avWtED;
	private double avWtRR;
	private double avWtCF;
	private double EDRC;
	private double valR;
	private double patCFOAdh;
	private double strengthScore;
}
