package spring.hk.springcenter.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = AppConfig.PACKAGE_NAMESPACE)
public class AppConfig {
	public static final String PACKAGE_NAMESPACE = "spring.hk.springcenter";
}
