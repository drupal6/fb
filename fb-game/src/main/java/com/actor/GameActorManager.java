package com.actor;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.actor.impl.ActTimer;
import com.actor.impl.ActorDispatcher;
import com.actor.impl.IActor;

public class GameActorManager {

	private static final GameActorManager inst = new GameActorManager();

	private ActorDispatcher loginActors = new ActorDispatcher(16, "Login", 200 * 1000);
	
	private ActorDispatcher playerActors = new ActorDispatcher(50, "Player", 200 * 1000);
	
	private ActorDispatcher battleActors = new ActorDispatcher(10, "Battle", 200 * 1000);

	private ScheduledThreadPoolExecutor[] scheduledThreadPoolExecutors;

	/**
	 * timer
	 */
	private ActTimer timer = new ActTimer("Channel");

	private GameActorManager() {}

	public static GameActorManager getInst() {
		return inst;
	}

	public boolean start() {
		if (false == loginActors.start()) {
			return false;
		}
		if (false == playerActors.start()) {
			return false;
		}
		if (false == battleActors.start()) {
			return false;
		}
		if (false == timer.start(1)) {
			return false;
		}
		int cpunumber = getAvailableProcessors();
		if (cpunumber < 1) {
			cpunumber = 1;
		}
		scheduledThreadPoolExecutors = new ScheduledThreadPoolExecutor[cpunumber];
		for (int i = 0; i < cpunumber; i++) {
			scheduledThreadPoolExecutors[i] = new ScheduledThreadPoolExecutor(1);
		}
		return true;

	}

	public void stop() {
		loginActors.stop();
		playerActors.stop();
		battleActors.stop();
		timer.stop();
	}

	public String getActorStatus() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(loginActors.getActorStatus());
		stringBuffer.append("\n");
		stringBuffer.append(playerActors.getActorStatus());
		stringBuffer.append("\n");
		stringBuffer.append(battleActors.getActorStatus());
		stringBuffer.append("\n");
		stringBuffer.append(timer.getActorStatus());
		stringBuffer.append("\n");
		return stringBuffer.toString();
	}

	public static ActTimer getTimer() {
		return getInst().timer;
	}

	public static IActor getLoginActor(int clientId) {
		return getInst().loginActors.getActor(clientId);
	}
	public static IActor getPlayerActor(int clientId) {
		return getInst().playerActors.getActor(clientId);
	}
	public static IActor getBattleActor(int clientId) {
		return getInst().battleActors.getActor(clientId);
	}

	public ScheduledThreadPoolExecutor getSceneExecutor(int sceneid) {

		int index = sceneid % scheduledThreadPoolExecutors.length;
		return scheduledThreadPoolExecutors[index];
	}

	public int getAvailableProcessors() {
		Runtime runtime = Runtime.getRuntime();
		return runtime.availableProcessors();

	}

}
