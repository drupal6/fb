package com.redis;
//
//import com.ly.bean.game.PlayerItem;
//import com.ly.util.PropertiesConfig;
//
//public class TestRedis {
//
//	public static void main(String[] args) {
//		PropertiesConfig redisConfig = new PropertiesConfig();
//		redisConfig.init("E:/newproject/workspace/NewProject/Lib/config/redis.properties");
//		RedisTemplateMgr.init(redisConfig);
//		PlayerItem playerItem = new PlayerItem();
//		playerItem.setBaseItemId(10);
//		playerItem.setNum(4);
//		playerItem.setType(1);
//		playerItem.setId(1);
//		RedisTemplateMgr.hset("test1", "name", playerItem.getBuilder().build());
//		playerItem.setId(2);
//		playerItem.setType(2);
//		RedisTemplateMgr.hset("test1", "name1", playerItem.getBuilder().build());
//		PlayerItem playerItem1 = RedisTemplateMgr.hget("test1", "name1", PlayerItem.class);
//		System.out.println(playerItem1.getId() + "<>" + playerItem1.getType());
//	}
//
//}
