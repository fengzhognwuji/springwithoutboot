package spring.hk.springcenter.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.EnableLoadTimeWeaving.AspectJWeaving;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = EclipseLinkPersistenceConfig.PACKAGE_NAMESPACE)
@EnableTransactionManagement
@EnableLoadTimeWeaving(aspectjWeaving = AspectJWeaving.ENABLED)
@EnableJpaRepositories("spring.hk.springcenter.repository")
//@PropertySource(value = { "classpath:db.properties" })
public class EclipseLinkPersistenceConfig extends PersistenceConfig implements AutoCloseable {

	private static final String PROPERTY_ECLIPSELINK_LOG = "eclipselink.logging.level.sql";
	private static final String PROPERTY_ECLIPSELINK_BATCH = "eclipselink.jdbc.batch-writing";
	private static final String PROPERTY_ECLIPSELINK_DDL = "eclipselink.ddl-generation";
	private static final String PROPERTY_ECLIPSELINK_TARGET_DATABASE = "eclipselink.target-database";
	private static final String PROPERTY_ECLIPSELINK_UPPERCASE_COLUMNS = "eclipselink.jpa.uppercase-column-names";
	private static final String PROPERTY_ECLIPSELINK_WEAVING = "eclipselink.weaving";
	private static final String PROPERTY_ECLIPSELINK_CACHE_SHARED_DEFAULT = "eclipselink.cache.shared.default";
	private static final String PROPERTY_ECLIPSELINK_SHARED_CACHE_MODE = "shared-cache-mode";

	private EclipseLinkJpaVendorAdapter eclipseLinkJpaVendorAdapter;

	private LoadTimeWeaver loadTimeWeaver;

	@Override
	@Bean(name = "jpaProperties")
	public synchronized Properties jpaProperties() {
		if (this.jpaProperties == null) {
			this.jpaProperties = new Properties();
			this.assignProperty(PROPERTY_ECLIPSELINK_LOG);
			this.assignProperty(PROPERTY_ECLIPSELINK_BATCH);
			this.assignProperty(PROPERTY_ECLIPSELINK_DDL);
			this.assignProperty(PROPERTY_ECLIPSELINK_UPPERCASE_COLUMNS);
			this.assignProperty(PROPERTY_ECLIPSELINK_TARGET_DATABASE, "Auto");
			this.assignProperty(PROPERTY_ECLIPSELINK_WEAVING, "true");
			this.assignProperty(PROPERTY_ECLIPSELINK_CACHE_SHARED_DEFAULT, "false");
			this.assignProperty(PROPERTY_ECLIPSELINK_SHARED_CACHE_MODE, "NONE");
		}
		return this.jpaProperties;
	}

	@Override
	@Bean(name = "jpaVendorAdapter")
	public synchronized EclipseLinkJpaVendorAdapter jpaVendorAdapter() {
		if (this.eclipseLinkJpaVendorAdapter == null) {
			this.eclipseLinkJpaVendorAdapter = new EclipseLinkJpaVendorAdapter();
			this.eclipseLinkJpaVendorAdapter.setShowSql(true);
			return this.eclipseLinkJpaVendorAdapter;
		}
		return this.eclipseLinkJpaVendorAdapter;
	}

	@Override
	@Bean(name = "loadTimeWeaver")
	public synchronized LoadTimeWeaver loadTimeWeaver() {
		if (this.loadTimeWeaver == null) {
			final InstrumentationLoadTimeWeaver loadTimeWeaver = new InstrumentationLoadTimeWeaver();
			this.loadTimeWeaver = loadTimeWeaver;
		}
		return this.loadTimeWeaver;
	}
}
