package model;

import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * This class is used to create new game instances. Players are put in a waiting list
 * until the require number of players is reached. Then a new game instance is created
 * and the waiting list is cleared.
 * 
 */
public class GameBuilder {

	private int capacity; // require number of players to create a new game instance
	private List<Player> players; // waiting list
	
	private Lock lock;
	private Condition enoughPlayer;
		
	private int waitingPlayersCount;
	
	private Game game;
	private boolean gameAvailable; // used to indicate if an error occurred during the game initialization
	
	public GameBuilder(int capacity) {
		this.capacity = capacity;
		this.players = new ArrayList<Player>();
		this.lock = new ReentrantLock();
		this.enoughPlayer = this.lock.newCondition();
		this.waitingPlayersCount = 0;
		this.gameAvailable = false;
	}
	
	public Game waitForPlayers(Player player) throws RemoteException, InterruptedException, UnexpectedException {
		this.lock.lock();
		this.players.add(player);
		this.waitingPlayersCount++;
		System.out.println(String.format("Waiting for players : %d/%d", this.waitingPlayersCount, this.capacity));
		if (this.players.size() == this.capacity) {
			System.out.println("Création de la partie");
			try {
				this.game = new Game(this.players, this.capacity);
			} catch (RemoteException | InterruptedException exception) {
				System.out.println("Erreur ! On vire tout le monde !");
				this.waitingPlayersCount = 0;
				this.players.clear();
				this.enoughPlayer.signalAll(); 
				this.lock.unlock();
				throw exception;
			}
			System.out.println("Partie crée, je réveil tout le monde !");
			this.gameAvailable = true;
			this.waitingPlayersCount--;
			this.enoughPlayer.signalAll();
			this.lock.unlock();
			return this.game;
		} else {
			System.out.println("J'attends d'autres joueurs");
			try {
				this.enoughPlayer.await();
			} catch (InterruptedException exception) {
				System.out.println("Erreur, je quitte la file d'attente !");
				this.players.remove(player);
				this.waitingPlayersCount--;
				this.lock.unlock();
				throw exception;
			}
			if (!this.gameAvailable) {
				this.lock.unlock();
				throw new UnexpectedException("An error occurred during game initialization");
			} else {
				System.out.println("Je rejoinds la partie !");
				this.waitingPlayersCount--;
				if (this.waitingPlayersCount == 0) {
					System.out.println("JE SUIS LE DERNIER A PARTIR");
					this.gameAvailable = false;
					this.players.clear();
				}
				this.lock.unlock();
				return this.game;
			}
		}
	}
}
