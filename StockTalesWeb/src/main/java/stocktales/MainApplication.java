package stocktales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication()
@PropertySources(
    { @PropertySource("classpath:weights.properties"), @PropertySource("classpath:application.properties") }
)
public class MainApplication
{
	
	public static void main(
	        String[] args
	)
	{
		SpringApplication.run(MainApplication.class, args);
		
	}
	
}
