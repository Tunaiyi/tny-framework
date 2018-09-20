package funs.god.games.base.sql;

import static com.tny.game.common.utils.StringAide.*;
import com.tny.game.suite.sql.SQLScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DBIniter implements BeanFactoryPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBIniter.class);

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

    private void doRunSQLScrpit(Connection connection, List<SQLScript> initSQLs) throws SQLException {
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
        if (this.initSQLs != null && !this.initSQLs.isEmpty())
            this.doRunSQLScrpit(connection, this.initSQLs);
    }

    private void startSQLs(Connection connection) throws SQLException {
        if (this.initSQLs != null && !this.initSQLs.isEmpty())
            this.doRunSQLScrpit(connection, this.startSQLs);
    }

    private void runScript() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
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
