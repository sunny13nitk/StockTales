package stocktales.scsnapshot.model.pojo;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockTrends
{
	private java.util.List<StockRevWCCycleTrends> revWCTrends = new ArrayList<StockRevWCCycleTrends>();
}
