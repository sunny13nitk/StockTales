package stocktales.healthcheck.aspects;

import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import stocktales.healthcheck.annotations.HealthCheckCriteria;
import stocktales.healthcheck.intf.IHC_GetEvaluationResults;
import stocktales.healthcheck.intf.IHC_SubSrvRes;
import stocktales.healthcheck.model.helperpojo.HCBeanReturn;
import stocktales.healthcheck.model.helperpojo.HCSubSrvRes;

@Aspect
@Service
public class SCHealthCheckEvaluateAspect
{
	@Autowired
	private ApplicationContext appCtxt;
	
	private IHC_GetEvaluationResults srvInstance;
	
	//- On Interface at specific Method with Argument
	@Around("IHC_GetEvaluationResultsInterface() && returnAfterEvaluationMethodCall() && args(scCode)")
	public Object evaluateMainSrv(
	        ProceedingJoinPoint pjp, String scCode
	) throws Throwable
	{
		// To actually get the BaseValue from Main Service - SubServices Calls are subsequent 
		HCBeanReturn beanReturnResult = new HCBeanReturn();
		beanReturnResult = (HCBeanReturn) pjp.proceed();
		
		/*
		 * Find the Custom annotation @HealthCheckCriteria ( Attribute MainSrv )
		 *  --Scan Through all the Services that Implement IHC_SubSrvRes where their Bean name matches pjp Bean Name
		 *  - Call its getSubServiceResults and append to pjp's results.HCBeanReturn
		 */
		
		if (pjp != null && scCode != null)
		{
			System.out.println("Evaluation Aspect Triggered for Scrip Code : " + scCode);
			
			if (pjp.getTarget() instanceof IHC_GetEvaluationResults)
			{
				srvInstance = (IHC_GetEvaluationResults) pjp.getTarget();
				if (srvInstance != null)
				{
					Map<String, Object> subSrvList = appCtxt.getBeansWithAnnotation(HealthCheckCriteria.class);
					if (subSrvList != null)
					{
						if (subSrvList.size() > 0)
						{
							for (Map.Entry<String, Object> entry : subSrvList.entrySet())
							{
								if (entry.getValue() instanceof IHC_SubSrvRes)
								{
									IHC_SubSrvRes subSrvRef = (IHC_SubSrvRes) entry.getValue();
									if (subSrvRef != null)
									{
										List<HCSubSrvRes> subSrvRes = subSrvRef.getSubServiceResults(scCode);
										if (subSrvRes != null)
										{
											for (HCSubSrvRes hcSubSrvRes : subSrvRes)
											{
												beanReturnResult.getMessages().addAll(hcSubSrvRes.getMessages());
												beanReturnResult.getDataContainers()
												        .addAll(hcSubSrvRes.getContainers());
											}
										}
										
									}
								}
							}
						}
					}
				}
			}
			
		}
		
		return beanReturnResult;
	}
	
	// ------------------- POINTCUTS -------------------------------------------
	
	/*
	 * Implements Interface IHC_GetEvaluationResults
	 */
	
	@Pointcut("target(stocktales.healthcheck.intf.IHC_GetEvaluationResults)")
	public void IHC_GetEvaluationResultsInterface(
	)
	{
		
	}
	
	/*
	 * For Method Execution returnAfterEvaluation - Argument of Type String
	 */
	
	@Pointcut("execution(public stocktales.healthcheck.model.helperpojo.HCBeanReturn *.returnAfterEvaluation(String))")
	public void returnAfterEvaluationMethodCall(
	)
	{
		
	}
	
}
