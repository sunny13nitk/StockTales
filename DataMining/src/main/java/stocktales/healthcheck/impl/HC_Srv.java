package stocktales.healthcheck.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stocktales.healthcheck.intf.IHC_Srv;
import stocktales.healthcheck.model.helperpojo.HCComboResult;
import stocktales.scripsEngine.uploadEngine.scDataContainer.services.interfaces.ISCDataContainerSrv;
import stocktales.services.interfaces.ScripService;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class HC_Srv implements IHC_Srv
{
	@Autowired
	private ISCDataContainerSrv scDcSrv;
	
	@Autowired
	private ApplicationContext appCtxt;
	
	@Autowired
	private ScripService scSrv;
	
	@Autowired
	private MessageSource msgSrc;
	
	private boolean financialScrip;
	
	private List<HCComboResult> results = new ArrayList<HCComboResult>();
	
	@Override
	public void Initialize(
	        String scCode
	) throws Exception
	{
		if (scCode != null)
		{
			scDcSrv.load(scCode);
			if (scSrv != null)
			{
				this.financialScrip = scSrv.isScripBelongingToFinancialSector(scCode);
			}
		}
		
	}
	
	@Override
	public void processScripHealthCheck(
	)
	{
		/*
		 * All the Beans Srv specified in Customizing of Type IHC_GetEvaluationResults to be called
		 * and processed - Return Values to be persisted here in this session Srv
		 */
		
	}
	
}
