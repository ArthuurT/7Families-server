package model;

import java.rmi.RemoteException;
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

public class Game extends UnicastRemoteObject implements IGame {

	private static final long serialVersionUID = 8594995881169048069L;

	private static final int NB_CARDS = 6;
	
	private int capacity;
	private List<IPlayer> players;
	private int currentPlayerIndex;
	private CardStack stack;
	
	private Lock lock = new ReentrantLock();
	private Condition myTurn = lock.newCondition();
	
	public Game(List<IPlayer> players, int capacity) throws RemoteException, InterruptedException {
		super();
		this.capacity = capacity;
		this.players = new ArrayList<IPlayer>(players);
		this.currentPlayerIndex = 0;
		this.stack = new CardStack();
		this.init();
	}

	private void registerOpponents() throws RemoteException {		
		for (IPlayer player : this.players) {
			for (IPlayer opponent : this.players) {
				if (!player.equals(opponent)) {
					player.registerOpponent(opponent);
				}
			}
		}
	}
	
	private void distributeCards() throws RemoteException, InterruptedException {
		for (IPlayer player : this.players) {
			for (int i = 0; i < Game.NB_CARDS; i++) {
				player.giveCard(this.stack.pickCard());
			}
		}
	}
	
	private void init() throws RemoteException, EmptyStackException, InterruptedException {
		this.registerOpponents();
		this.distributeCards();
	}

	@Override
	public void waitTurnOf(IPlayer player) throws RemoteException, InterruptedException {
		this.lock.lock();
		try {
			while (!this.players.get(this.currentPlayerIndex).equals(player)) {
				this.myTurn.await();
			}
		} catch (InterruptedException exception) {
			throw exception;
		} finally {
			this.lock.unlock();
		}
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
	public boolean isGameOver() throws RemoteException, InterruptedException {
		int familiesCounter = 0;
		for (IPlayer player : this.players) {
			familiesCounter += player.getCreatedFamilies().size();
		}
		return familiesCounter == 7;
	}
	
	@Override
	public boolean isWinner(IPlayer player) throws RemoteException, InterruptedException {
		IPlayer winner = this.players.get(0);
		int winnerFamiliesCounter = winner.getCreatedFamilies().size();
		LocalDateTime winnerLastFamilyCreatedDate = winner.getLastFamilyCreatedDate();
		for (int i = 1; i < this.players.size(); i++) {
			IPlayer currentPlayer = this.players.get(i);
			int currentPlayerFamiliesCounter = currentPlayer.getCreatedFamilies().size();
			LocalDateTime currentPlayerLastFamilyCreatedDate = currentPlayer.getLastFamilyCreatedDate();
			if (currentPlayerFamiliesCounter > winnerFamiliesCounter) {
				winner = currentPlayer;
			} else if (currentPlayerFamiliesCounter == winnerFamiliesCounter && currentPlayerLastFamilyCreatedDate.isBefore(winnerLastFamilyCreatedDate)) {
				winner = currentPlayer;
			}
		}
		return winner.equals(player);
	}

	@Override
	public void nextTurn() throws RemoteException {
		this.lock.lock();
		this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.capacity;
		this.myTurn.signalAll();
		this.lock.unlock();
	}
}
