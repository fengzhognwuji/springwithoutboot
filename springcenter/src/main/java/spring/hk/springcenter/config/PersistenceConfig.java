package spring.hk.springcenter.config;

import java.sql.SQLException;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.transaction.PlatformTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

import spring.hk.springcenter.exception.CommonPersistenceException;

public abstract class PersistenceConfig implements AutoCloseable {

	/**
	 * 默认的Spring容器扫描路径
	 */
	public static final String PACKAGE_NAMESPACE = "com.sap.jnc.marketing.persistence";

	/**
	 * 默认的数据库Schema
	 */
	public static final String ENTITY_SCHEMA_NAME = "MMP";

	/**
	 * 默认的所有的JPA实体类的包名
	 */
	private static final String[] ENTITY_PACKAGES = { "com.sap.jnc.marketing.persistence.model" };

	private static final String PROPERTY_NAME_DATABASE_DRIVER = "spring.datasource.driverClassName";
	private static final String PROPERTY_NAME_DATABASE_URL = "spring.datasource.url";
	private static final String PROPERTY_NAME_DATABASE_USERNAME = "spring.datasource.username";
	private static final String PROPERTY_NAME_DATABASE_PASSWORD = "spring.datasource.password";

	@Autowired
	private Environment environment;

	private DataSource dataSource;

	private LocalContainerEntityManagerFactoryBean entityManagerFactory;

	private EntityManager entityManager;

	private PlatformTransactionManager annotationDrivenTransactionManager;

	protected Properties jpaProperties;

	public abstract JpaVendorAdapter jpaVendorAdapter();

	public abstract java.util.Properties jpaProperties();

	public abstract LoadTimeWeaver loadTimeWeaver();

	@Bean(name = "dataSource")
	public synchronized DataSource dataSource() {
		if (this.dataSource == null) {
			final DruidDataSource druidDataSource = new DruidDataSource();
			druidDataSource.setDriverClassName(this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
			druidDataSource.setUrl(this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
			druidDataSource.setUsername(this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
			druidDataSource.setPassword(this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));
			druidDataSource.setInitialSize(1);
			druidDataSource.setMinIdle(1);
			druidDataSource.setMaxActive(10);
			druidDataSource.setMaxWait(60000);
			druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
			druidDataSource.setMinEvictableIdleTimeMillis(300000);
			druidDataSource.setValidationQuery("SELECT * FROM DUMMY");
			druidDataSource.setTestWhileIdle(true);
			druidDataSource.setTestOnBorrow(false);
			druidDataSource.setTestOnReturn(false);
			druidDataSource.setPoolPreparedStatements(true);
			druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
			druidDataSource.setRemoveAbandoned(true);
			druidDataSource.setRemoveAbandonedTimeout(1800);
			druidDataSource.setLogAbandoned(true);
			druidDataSource.setUseGlobalDataSourceStat(true);
			// druid.stat.mergeSql=true;
			druidDataSource.setConnectionProperties("druid.stat.slowSqlMillis=5000;druid.stat.logSlowSql=true");
			try {
				druidDataSource.setFilters("stat");
			} catch (final SQLException e1) {
				throw new RuntimeException(e1);
			}
			try {
				druidDataSource.init();
			} catch (final SQLException e) {
				throw new RuntimeException(e);
			}
			this.dataSource = druidDataSource;
		}
		return this.dataSource;
	}

	@Bean(name = "entityManagerFactory")
	public synchronized LocalContainerEntityManagerFactoryBean entityManagerFactory()
			throws CommonPersistenceException {
		if (this.entityManagerFactory == null) {
			final LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
			entityManagerFactory.setDataSource(this.dataSource());
			entityManagerFactory.setJpaVendorAdapter(this.jpaVendorAdapter());
			entityManagerFactory.setLoadTimeWeaver(this.loadTimeWeaver());
			entityManagerFactory.setJpaProperties(this.jpaProperties());
			entityManagerFactory.setPackagesToScan(ENTITY_PACKAGES);
			this.entityManagerFactory = entityManagerFactory;
		}
		return this.entityManagerFactory;
	}

	@Bean(name = "transactionManager")
	public synchronized PlatformTransactionManager annotationDrivenTransactionManager() {
		if (this.annotationDrivenTransactionManager == null) {
			final JpaTransactionManager annotationDrivenTransactionManager = new JpaTransactionManager();
			annotationDrivenTransactionManager
					.setEntityManagerFactory(this.entityManagerFactory.getNativeEntityManagerFactory());
			annotationDrivenTransactionManager.setJpaDialect(new EclipseLinkJpaDialect());
			this.annotationDrivenTransactionManager = annotationDrivenTransactionManager;
		}
		return this.annotationDrivenTransactionManager;
	}

	@Bean(name = "entityManager")
	@Primary
	public synchronized EntityManager entityManager() {
		if (this.entityManager == null) {
			final EntityManager entityManager = SharedEntityManagerCreator
					.createSharedEntityManager(this.entityManagerFactory.getNativeEntityManagerFactory());
			this.entityManager = entityManager;
		}
		return this.entityManager;
	}

	@Bean
	public synchronized JdbcTemplate jdbcTemplate() throws SQLException {
		return new JdbcTemplate(this.dataSource());
	}

	@Bean
	public synchronized NamedParameterJdbcTemplate namedParameterJdbcTemplate() throws SQLException {
		return new NamedParameterJdbcTemplate(this.dataSource());
	}

	@Override
	public void close() throws Exception {
		if ((this.entityManager != null) && this.entityManager.isOpen()) {
			this.entityManager.close();
		}
		if (this.entityManagerFactory != null) {
			this.entityManagerFactory.destroy();
		}
		if ((this.dataSource != null) && (this.dataSource instanceof DruidDataSource)) {
			((DruidDataSource) this.dataSource).close();
		}
	}

	protected void assignProperty(String propertyName, String defaultValue) {
		this.jpaProperties.put(propertyName, this.environment.getProperty(propertyName, defaultValue));
	}

	protected void assignProperty(String propertyName) {
		this.jpaProperties.put(propertyName, this.environment.getProperty(propertyName));
	}
}
