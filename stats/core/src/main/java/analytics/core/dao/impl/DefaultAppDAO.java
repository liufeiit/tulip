package analytics.core.dao.impl;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import tulip.data.jdbc.mapper.BeanParameterMapper;
import tulip.data.jdbc.mapper.BeanRowMapper;
import analytics.core.dao.AppDAO;
import analytics.core.dao.BaseDAO;
import analytics.core.dao.DAOException;
import analytics.core.dataobject.AppDO;

/**
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年8月25日 下午12:06:50
 */
@Repository("appDAO")
public class DefaultAppDAO extends BaseDAO implements AppDAO {

	public static final String ADD_SQL = "INSERT INTO app "
			+ "(user_id, name, token, description, gmt_created, gmt_modified) VALUES "
			+ "(:user_id, :name, :token, :description, NOW(), NOW());";
	
	public static final String UPDATE_SQL = "UPDATE app SET "
			+ "user_id = :user_id, name = :name, token = :token, description = :description, gmt_modified = NOW() WHERE id = :id;";

	public static final String SELECT_SQL = "SELECT id, user_id, name, token, description, gmt_created, gmt_modified FROM app WHERE id = :id;";

	public static final String DELETE_SQL = "DELETE FROM app WHERE id = :id;";

	public static final String SELECT_ALL_SQL = "SELECT id, user_id, name, token, description, gmt_created, gmt_modified FROM app;";

	@Override
	public void insertApp(AppDO app) throws DAOException {
		try {
			KeyHolder holder = new GeneratedKeyHolder();
			jdbcTemplate.update(ADD_SQL, BeanParameterMapper.newInstance(app), holder, new String[]{ "id" });
			Number id = holder.getKey();
			app.setId(id.longValue());
		} catch (DataAccessException e) {
			log.error("AddApp Error.", e);
			throw new DAOException("AddApp Error.", e);
		}
	}

	@Override
	public void updateApp(AppDO app) throws DAOException {
		try {
			jdbcTemplate.update(UPDATE_SQL, BeanParameterMapper.newInstance(app));
		} catch (DataAccessException e) {
			log.error("UpdateApp Error.", e);
			throw new DAOException("UpdateApp Error.", e);
		}
	}

	@Override
	public AppDO selectApp(long appId) throws DAOException {
		try {
			return jdbcTemplate.queryForObject(SELECT_SQL, 
					BeanParameterMapper.newSingleParameterMapper("id", appId), BeanRowMapper.newInstance(AppDO.class));
		} catch (DataAccessException e) {
			log.error("SelectApp Error.", e);
			throw new DAOException("SelectApp Error.", e);
		}
	}

	@Override
	public void deleteApp(AppDO app) throws DAOException {
		try {
			jdbcTemplate.update(DELETE_SQL, BeanParameterMapper.newInstance(app));
		} catch (DataAccessException e) {
			log.error("RemoveApp Error.", e);
			throw new DAOException("RemoveApp Error.", e);
		}
	}

	@Override
	public List<AppDO> selectAll() throws DAOException {
		try {
			return jdbcTemplate.query(SELECT_ALL_SQL, BeanRowMapper.newInstance(AppDO.class));
		} catch (DataAccessException e) {
			log.error("SelectAll Error.", e);
			throw new DAOException("SelectAll Error.", e);
		}
	}
}