package com.bean.game;

import com.bean.Option;
import com.bean.DataObject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.bean.game.proto.PlayerItemBeanMsg.PlayerItemBean;

/**
* t_player_item 
* @author 
* @date   Tue May 16 09:55:22 CST 2017
*/ 
public class PlayerItem extends DataObject {

	private PlayerItemBean.Builder builder;

	public PlayerItem(){
		this.builder = PlayerItemBean.newBuilder();
	}

	public PlayerItem(int id){
	}

	public void setBuilder(PlayerItemBean.Builder builder) {
		this.builder = builder;
	}

	public PlayerItemBean.Builder getBuilder() {
		return builder;
	}

	public void setId(int id) {
		if (builder.getId() != id) {
			builder.setId(id);
			setOp(Option.Update);
		}
	}

	public int getId() {
		return builder.getId();
	}

	public void setBaseItemId(int baseItemId) {
		if (builder.getBaseItemId() != baseItemId) {
			builder.setBaseItemId(baseItemId);
			setOp(Option.Update);
		}
	}

	public int getBaseItemId() {
		return builder.getBaseItemId();
	}

	public void setNum(int num) {
		if (builder.getNum() != num) {
			builder.setNum(num);
			setOp(Option.Update);
		}
	}

	public int getNum() {
		return builder.getNum();
	}

	public void setType(int type) {
		if (builder.getType() != type) {
			builder.setType(type);
			setOp(Option.Update);
		}
	}

	public int getType() {
		return builder.getType();
	}

	public void setNode(String node) {
		if(builder.getNode() == null || !builder.getNode().equals(node)) {
			builder.setNode(node);
			setOp(Option.Update);
		}
	}

	public String getNode() {
		return builder.getNode();
	}

	public void setTest1(String test1) {
		if(builder.getTest1() == null || !builder.getTest1().equals(test1)) {
			builder.setTest1(test1);
			setOp(Option.Update);
		}
	}

	public String getTest1() {
		return builder.getTest1();
	}

	@Override
	public void parseFrom(byte[] data) {
		try {
			builder = PlayerItemBean.parseFrom(data).toBuilder();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] toByteArray() {
		return builder.build().toByteArray();
	}

}

