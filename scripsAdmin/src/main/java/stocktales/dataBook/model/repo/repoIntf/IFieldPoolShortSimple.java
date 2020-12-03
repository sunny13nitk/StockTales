package stocktales.dataBook.model.repo.repoIntf;

import org.springframework.beans.factory.annotation.Value;

import stocktales.dataBook.enums.EnumInterval;

public interface IFieldPoolShortSimple
{
	Long getId(
	);
	
	EnumInterval getInterval(
	);
	
	@Value("#{target.valm + '- Q' + target.vald}")
	String getIntervalText(
	);
	
}
