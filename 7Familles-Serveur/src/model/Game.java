package model;

import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import remote.IGame;
import remote.IPlayer;

/**
 * 
 * This class is used to initialize and store the state of the game :
 * - the list of players
 * - the card game
 * - the name of the current player
 *
 */
public class Game extends UnicastRemoteObject implements IGame {

	private static final long serialVersionUID = 8594995881169048069L;

	private static final int NB_CARDS = 6;
	
	private int capacity;
	private List<Player> players;
	private int currentPlayerIndex;
	private CardStack stack;
	
	private boolean gameOver;
	
	private Lock lock = new ReentrantLock();
	private Condition myTurn = lock.newCondition();
	
	private boolean errorReported; // used to indicate if one or more players are no more reachable
	private HeartBeatThread heartBeat;
	
	public Game(List<Player> players, int capacity) throws RemoteException, InterruptedException {
		super();
		this.capacity = capacity;
		this.players = new ArrayList<Player>(players);
		this.currentPlayerIndex = 0;
		this.stack = new CardStack();
		this.gameOver = false;
		this.errorReported = false;
		this.heartBeat = new HeartBeatThread(this);
		this.heartBeat.start();
		this.init();
	}

	private void registerOpponents() throws RemoteException {		
		for (Player player : this.players) {
			for (Player opponent : this.players) {
				if (!player.equals(opponent)) {
					player.remote.registerOpponent(opponent.remote, opponent.tampon);
				}
			}
		}
	}
	
	private void distributeCards() throws RemoteException, InterruptedException {
		for (Player player : this.players) {
			for (int i = 0; i < Game.NB_CARDS; i++) {
				player.remote.giveCard(this.stack.pickCard());
			}
		}
	}
	
	private void init() throws RemoteException, EmptyStackException, InterruptedException {
		this.registerOpponents();
		this.distributeCards();
	}
	
	public List<Player> getPlayers() {
		return this.players;
	}

	@Override
	public void waitTurnOf(IPlayer iPlayer) throws RemoteException, InterruptedException, UnexpectedException {
		this.lock.lock();
		try {
			while (!this.players.get(this.currentPlayerIndex).remote.equals(iPlayer) && !this.gameOver && !this.errorReported) {
				this.myTurn.await();
			}
		} catch (InterruptedException exception) {
			this.lock.unlock();
			throw exception;
		} 
		if (this.errorReported) {
			this.lock.unlock();
			throw new UnexpectedException("A player is unreachable, the game is stoped");
		}
		this.lock.unlock();
	}

	@Override
	public Card pickCard() throws RemoteException {
		this.lock.lock();
		Card card;
		try {
			card = this.stack.pickCard();
		} catch (EmptyStackException exception) {
			card = null;
		}
		this.lock.unlock();
		return card;
	}

	@Override
	public boolean isGameOver() throws RemoteException {
		return this.gameOver;
	}
	
	@Override
	public boolean isWinner(IPlayer iPlayer) throws RemoteException, InterruptedException {
		IPlayer winner = this.players.get(0).remote;
		int winnerFamiliesCounter = winner.getCreatedFamilies().size();
		LocalDateTime winnerLastFamilyCreatedDate = winner.getLastFamilyCreatedDate();
		for (int i = 1; i < this.players.size(); i++) {
			IPlayer currentPlayer = this.players.get(i).remote;
			int currentPlayerFamiliesCounter = currentPlayer.getCreatedFamilies().size();
			LocalDateTime currentPlayerLastFamilyCreatedDate = currentPlayer.getLastFamilyCreatedDate();
			if (currentPlayerFamiliesCounter > winnerFamiliesCounter) {
				winner = currentPlayer;
			} else if (currentPlayerFamiliesCounter == winnerFamiliesCounter && currentPlayerLastFamilyCreatedDate.isBefore(winnerLastFamilyCreatedDate)) {
				winner = currentPlayer;
			}
		}
		return winner.equals(iPlayer);
	}

	private void checkIfGameIsOver() throws RemoteException, InterruptedException {
		int familiesCounter = 0;
		for (Player player : this.players) {
			familiesCounter += player.remote.getCreatedFamilies().size();
		}
		this.gameOver = familiesCounter == 7;
	}
	
	@Override
	public void nextTurn() throws RemoteException, InterruptedException {
		this.lock.lock();
		this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.capacity;
		this.checkIfGameIsOver();
		if (this.gameOver) {
			this.heartBeat.stopRunning();
		}
		this.myTurn.signalAll();
		this.lock.unlock();
	}
	
	public void reportError() {
		this.lock.lock();
		this.errorReported = true;
		this.myTurn.signalAll();
		this.lock.unlock();
	}
}
