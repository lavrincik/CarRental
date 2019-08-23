package project.impl;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import project.CarManager;
import project.CustomerManager;
import project.LeaseManager;

import javax.sql.DataSource;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.DERBY;

/**
 * @author Daniel Jurca
 */
@Configuration
@EnableTransactionManagement
public class MySpringTestConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(DERBY)
                .setScriptEncoding("utf-8")
                .addScript("classpath:createTables.sql")
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public CustomerManager customerManager() {
        return new CustomerManagerImpl(dataSource());
    }

    @Bean
    public CarManager carManager() {
        return new CarManagerImpl(dataSource());
    }

    @Bean
    public LeaseManager leaseManager() {
        return new LeaseManagerImpl(dataSource(), customerManager(), carManager());
    }
}
