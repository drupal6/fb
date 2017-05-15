package com.dao;

import java.util.List;
import com.bean.game.PlayerItem;
/**
* t_player_item dao接口的生成
* @author 
* @date Mon May 15 17:43:46 CST 2017
*/ 
public interface PlayerItemDao {

	public boolean createPlayerItem(PlayerItem playerItem);

	public List<PlayerItem> getPlayerItemList();

	public PlayerItem getPlayerItemById(int id);

	public boolean updatePlayerItem(PlayerItem playerItem);

}

