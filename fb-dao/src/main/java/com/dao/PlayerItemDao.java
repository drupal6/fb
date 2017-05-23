package com.dao;

import java.util.List;
import com.bean.game.PlayerItem;
/**
* t_player_item dao接口的生成
* @author 
* @date Tue May 16 09:55:22 CST 2017
*/ 
public interface PlayerItemDao {

	public boolean createPlayerItem(PlayerItem playerItem);

	public List<PlayerItem> getPlayerItemList();

	public PlayerItem getPlayerItemById(int id);

	public boolean updatePlayerItem(PlayerItem playerItem);

}

