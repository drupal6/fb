package test.net;

import com.actor.BattleActorManager;
import com.net.BattleServer;
import com.net.GameServer;
import com.net.connect.ConnectServer;

public class TestServer {

	public static void main(String[] args) throws InterruptedException {
		BattleActorManager.getInst().start();
		new Thread((Runnable) BattleServer.getInstance()).start();
		Thread.sleep(1000);
		new Thread((Runnable) GameServer.getInstance()).start();
		new Thread((Runnable) ConnectServer.getInstance()).start();
		
	}
}
