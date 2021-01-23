package stocktales.healthcheck.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stocktales.factsheet.interfaces.IFactSheetBufferSrv;
import stocktales.healthcheck.intf.IHC_GetEvaluationResults;
import stocktales.healthcheck.intf.IHC_Srv;
import stocktales.healthcheck.model.entity.Cfg_ScripHealthCheck;
import stocktales.healthcheck.model.helperpojo.HCComboResult;
import stocktales.healthcheck.repo.Repo_CfgHC;
import stocktales.services.interfaces.ScripService;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class HC_Srv implements IHC_Srv
{
	@Autowired
	private IFactSheetBufferSrv fsBuffSrv;
	
	@Autowired
	private ApplicationContext appCtxt;
	
	@Autowired
	private Repo_CfgHC repoCFG_HC;
	
	@Autowired
	private ScripService scSrv;
	
	@Autowired
	private MessageSource msgSrc;
	
	private boolean financialScrip;
	
	private String scCode;
	
	private List<HCComboResult> results = new ArrayList<HCComboResult>();
	
	@Override
	public void Initialize(
	        String scCode
	) throws Exception
	{
		if (scCode != null)
		{
			this.scCode = scCode;
			fsBuffSrv.Initialize(scCode, true);
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
		
		if (repoCFG_HC != null)
		{
			//Get all Customizing
			
			List<Cfg_ScripHealthCheck> cfgList = repoCFG_HC.findAll();
			if (cfgList != null)
			{
				if (cfgList.size() > 0)
				{
					//for each Configuration
					for (Cfg_ScripHealthCheck cfg : cfgList)
					{
						if (cfg.getSrvname() != null && appCtxt != null)
						{
							//Get Main Service Instance
							IHC_GetEvaluationResults mainSrv = (IHC_GetEvaluationResults) appCtxt
							        .getBean(cfg.getSrvname());
							if (mainSrv != null)
							{
								mainSrv.returnAfterEvaluation(this.scCode);
							}
						}
					}
				}
			}
		}
		
		/*
		 * Conversion Layer to be Set up to Use Each HCBeanReturn(valuetocmp) to evaluate as per Config for
		 * each Service and Populate Corresponding HCEvalResult of Result Entity type HCComboResult
		 * Taken care by an Aspect - SCHealtCheckFinalReaultsGenerationAspect
		 */
		
	}
	
}
