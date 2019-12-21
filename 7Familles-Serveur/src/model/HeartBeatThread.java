package model;

import java.rmi.RemoteException;

/**
 * 
 * This class is used to check if players are still reachable. To do that, a player's function is
 * called periodically. If an exception is raised when the function is called, then the player is
 * no more reachable, else he is.
 *
 */
public class HeartBeatThread extends Thread {

	private Game game;
	private boolean running;
	
	public HeartBeatThread(Game game) {
		this.game = game;
		this.running = false;
	}
	
	public void stopRunning() {
		this.running = false;
	}
	
	@Override
	public void run() {
		this.running = true;
		while (this.running) {
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (Player player : this.game.getPlayers()) {
				try {
					player.remote.getPseudo();
				} catch (RemoteException e) {
					this.game.reportError();
					this.running = false;
				}
			}
		}
		System.out.println("Stopping heartbeat");
	}
}
