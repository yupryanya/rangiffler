package guru.qa.rangiffler.data.tpl;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.p6spy.engine.spy.P6DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DataSources {
  private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

  public static DataSource dataSource(String jdbcUrl) {
    return dataSources.computeIfAbsent(
        jdbcUrl,
        key -> {
          AtomikosDataSourceBean dsBean = new AtomikosDataSourceBean();
          final String uniqId = StringUtils.substringAfterLast(jdbcUrl, "/");
          dsBean.setUniqueResourceName(uniqId);
          dsBean.setXaDataSourceClassName("com.mysql.cj.jdbc.MysqlXADataSource");

          Properties props = new Properties();
          props.put("url", jdbcUrl);
          props.put("user", "root");
          props.put("password", "secret");
          dsBean.setXaProperties(props);

          dsBean.setPoolSize(6);
          dsBean.setMaxPoolSize(20);

          P6DataSource p6DataSource = new P6DataSource(dsBean);

          try {
            InitialContext context = new InitialContext();
            String jndiName = "java:comp/env/jdbc/" + uniqId;
            context.bind(jndiName, p6DataSource);
          } catch (NamingException e) {
            throw new RuntimeException(e);
          }

          return p6DataSource;
        }
    );
  }
}