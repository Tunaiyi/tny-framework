package funs.god.games.base.sql;

import com.tny.game.suite.sql.*;
import org.slf4j.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

import static com.tny.game.common.utils.StringAide.*;

public class DBInitiator implements BeanFactoryPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(funs.god.games.base.sql.DBInitiator.class);

    private DataSource dataSource;

    private List<SQLScript> initSQLs;

    private List<SQLScript> startSQLs;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setInitSQLs(List<SQLScript> initSQLs) {
        this.initSQLs = initSQLs;
    }

    public void setStartSQLs(List<SQLScript> startSQLs) {
        this.startSQLs = startSQLs;
    }

    private void doRunSQLScript(Connection connection, List<SQLScript> initSQLs) throws SQLException {
        for (SQLScript sqlScript : initSQLs) {
            try {
                ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
                if (sqlScript.isHasSeparator()) {
                    rdp.setSeparator(sqlScript.getSeparator());
                }
                rdp.addScript(new ClassPathResource(sqlScript.getFile()));
                rdp.populate(connection);
            } catch (Throwable e) {
                throw new SQLException(format("初始化脚本 {} 异常", sqlScript), e);
            }
        }
    }

    private void runInitSQLs(Connection connection) throws SQLException {
        if (this.initSQLs != null && !this.initSQLs.isEmpty()) {
            this.doRunSQLScript(connection, this.initSQLs);
        }
    }

    private void startSQLs(Connection connection) throws SQLException {
        if (this.initSQLs != null && !this.initSQLs.isEmpty()) {
            this.doRunSQLScript(connection, this.startSQLs);
        }
    }

    private void runScript() throws SQLException {
        try (Connection connection = this.dataSource.getConnection()) {
            this.runInitSQLs(connection);
            this.startSQLs(connection);
        } catch (SQLException e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            this.runScript();
        } catch (SQLException e) {
            throw new BeanInitializationException("run sql Script exception", e);
        }
    }

}
