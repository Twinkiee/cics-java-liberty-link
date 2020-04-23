package com.sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdbcConfig {

  @Bean
  public DataSource dataSource() {
    try {
      return (DataSource) new InitialContext().lookup("jdbc/defaultCICSDataSource");
    } catch (NamingException e) {
      throw new ApplicationContextException(
          "An error occurred while initializing the default data source", e);
    }
  }

  @Bean
  public DriverManagerConnectionFactory driverManagerConnectionFactory() {
      try {
          Class.forName("com.ibm.db2.jcc.DB2Driver");
      } catch (ClassNotFoundException e) {
          throw new ApplicationContextException("An error occurred while instantiating [ driverManagerConnectionFactory ] component bean", e);
      }
      return new DriverManagerConnectionFactory("jdbc:default:connection");
  }

  public class DriverManagerConnectionFactory {
    private final String connectionUrl;


    DriverManagerConnectionFactory(String connection) {
      this.connectionUrl = connection;
    }

    public Connection getConnection() throws SQLException {
      return DriverManager.getConnection(connectionUrl);
    }
  }
}