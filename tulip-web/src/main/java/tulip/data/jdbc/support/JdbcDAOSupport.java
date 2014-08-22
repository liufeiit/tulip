package tulip.data.jdbc.support;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;

import tulip.data.jdbc.named.NamedJdbcTemplate;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年8月21日 上午11:55:35
 */
public class JdbcDAOSupport implements InitializingBean {

	protected final Log log = LogFactory.getLog(getClass());

	protected NamedJdbcTemplate jdbcTemplate;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (jdbcTemplate == null) {
			throw new IllegalArgumentException("'dataSource' or 'jdbcTemplate' is required");
		}
	}

	public final void setDataSource(DataSource dataSource) {
		if (this.jdbcTemplate == null) {
			this.jdbcTemplate = new NamedJdbcTemplate(dataSource);
		}
	}

	public final void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = new NamedJdbcTemplate(jdbcTemplate);
	}

	public final NamedJdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}
}