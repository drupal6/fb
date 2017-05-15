package com.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import com.dao.PlayerItemDao;
import java.util.List;
import java.util.Map;
import java.sql.Types;
import com.db.DbParameter;
import java.util.HashMap;
import java.util.ArrayList;
import com.db.pool.DBPoolMgr;
import com.bean.Option;
import com.bean.game.PlayerItem;
import com.db.BaseDao;

/**
* t_player_item daoImpl实现类的生成
* @author 
* @date Mon May 15 17:52:57 CST 2017
*/ 
public class PlayerItemDaoImpl extends BaseDao implements PlayerItemDao {

	@Override
	public boolean createPlayerItem(PlayerItem playerItem) {
		boolean result = false;
		if (playerItem.beginAdd()) {
			String sql = "insert into t_player_item (id,base_item_id,num,type) values (?,?,?,?);";
			Map<Integer, DbParameter> param = new HashMap<Integer, DbParameter>();
			param.put(1, new DbParameter(Types.INTEGER, playerItem.getId()));
			param.put(2, new DbParameter(Types.INTEGER, playerItem.getBaseItemId()));
			param.put(3, new DbParameter(Types.INTEGER, playerItem.getNum()));
			param.put(4, new DbParameter(Types.SMALLINT, playerItem.getType()));
			int id = execLastId(sql, param);
			if(id > 0) {
				playerItem.setId(id);
				result = true;
			}
			playerItem.commitAdd(result);
		}
		return result;
	}

	@Override
	public List<PlayerItem> getPlayerItemList() {
		String sql = "select * from t_player_item;";
		PreparedStatement pstmt = execQuery(sql, null);
		ResultSet rs = null;
		List<PlayerItem> infos = null;
		if (pstmt != null) {
			infos = new ArrayList<PlayerItem>();
			try {
				rs = pstmt.executeQuery();
				while (rs.next()) {
					infos.add(resultToBean(rs));
				}
			} catch (SQLException e) {
			} finally {
				closeConn(pstmt, rs);
			}
		}
		return infos;
	}

	@Override
	public PlayerItem getPlayerItemById(int id) {
		String sql = "select * from t_player_item where id=?;";
		Map<Integer, DbParameter> param = new HashMap<Integer, DbParameter>();
		param.put(1, new DbParameter(Types.INTEGER, id));
		PreparedStatement pstmt = execQuery(sql, param);
		ResultSet rs = null;
		PlayerItem playerItem = null;
		if (pstmt != null) {
			try {
				rs = pstmt.executeQuery();
				if(rs.next()){
					playerItem = resultToBean(rs);
				}
			} catch (SQLException e) {
			} finally {
				closeConn(pstmt, rs);
			}
		}
		return playerItem;
	}

	@Override
	public boolean updatePlayerItem(PlayerItem playerItem) {
		boolean result = false;
		if (playerItem.beginUpdate()) {
			String sql = "update t_player_item set base_item_id=?,num=?,type=? where id=?;";
			Map<Integer, DbParameter> param = new HashMap<Integer, DbParameter>();
			param.put(1, new DbParameter(Types.INTEGER, playerItem.getBaseItemId()));
			param.put(2, new DbParameter(Types.INTEGER, playerItem.getNum()));
			param.put(3, new DbParameter(Types.SMALLINT, playerItem.getType()));
			param.put(4, new DbParameter(Types.INTEGER, playerItem.getId()));
			result = execNoneQuery(sql, param) > -1;
			playerItem.commitUpdate(result);
		}
		return result;
	}

	public PlayerItem resultToBean(ResultSet rs) throws SQLException {
		PlayerItem playerItem = new PlayerItem();
		playerItem.setId(rs.getInt("id"));
		playerItem.setBaseItemId(rs.getInt("base_item_id"));
		playerItem.setNum(rs.getInt("num"));
		playerItem.setType(rs.getInt("type"));
		playerItem.setOp(Option.None);
		return playerItem;
	}

	@Override
	protected Connection openConn() {
		return DBPoolMgr.getMainConn();
	}
}

