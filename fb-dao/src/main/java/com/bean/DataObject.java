package com.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataObject {
	
	private final Logger logger = LoggerFactory.getLogger(DataObject.class); 
	
	private short op = 0;

	public final void setOp(short option) {
		if ((this.op == Option.Insert) && (option == Option.Update)) {
			return;
		}
		this.op = option;
	}

	public final void resetUpdate() {
		this.op = Option.Update;
	}

	public final short getOp() {
		return this.op;
	}

	public boolean beginAdd() {
		if (getOp() == Option.Insert) {
			setOp(Option.None);
			return true;
		}
		return false;
	}

	public void commitAdd(boolean result) {
		if (result == false) {
			setOp(Option.Insert);
			logger.error("添加出错了，状态还原" + toString());
		}
	}

	public boolean beginUpdate() {
		if (getOp() == Option.Update) {
			setOp(Option.None);
			return true;
		}
		return false;
	}

	public void commitUpdate(boolean result) {
		if (result == false) {
			setOp(Option.Update);
			logger.error("更新出错了，状态还原>>>" + toString());
		}
	}
	
	public void parseFrom(byte[] data) {
		
	}
	public byte[] toByteArray() {
		return null;
	}
}