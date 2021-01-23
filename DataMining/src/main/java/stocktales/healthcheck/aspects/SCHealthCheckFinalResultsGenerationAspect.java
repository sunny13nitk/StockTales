package stocktales.healthcheck.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

@Aspect
@Service
public class SCHealthCheckFinalResultsGenerationAspect
{
	/*
	 * Used Around as we will adjust the HCEvalResult part of all HCComboResult
	 */
	
	@Around("getScripHeatlhCheckResults()")
	public Object prepareFinalResultsasPerConfig(
	        ProceedingJoinPoint pjp
	) throws Throwable
	{
		if (pjp != null)
		{
			
			/*
			 * Loop through Repo for Config
			 */
			
			/*
			 * For each Srv Bean Name that Implements IHC_GetEvaluationResults
			 */
			
			/*
			 * Get the Bean and Execute its returnAfterEvaluation
			 */
			System.out.println("Results Consolidation Aspect Triggered!");
		}
		
		return pjp.proceed();
	}
	
	// POINTCUTS
	/*
	 * On Process of Scrip Health Check
	 */
	
	@Pointcut("execution( public *  stocktales.healthcheck.impl.HC_Srv.getResults())")
	public void getScripHeatlhCheckResults(
	)
	{
		
	}
}
