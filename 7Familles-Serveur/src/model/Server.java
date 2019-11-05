package model;

import java.rmi.RemoteException;
import java.rmi.UnexpectedException;

import remote.IGame;
import remote.IPlayer;
import remote.IServer;

public class Server implements IServer {
		
	private GameBuilder twoPlayersGame = new GameBuilder(2);
	private GameBuilder threePlayersGame = new GameBuilder(3);
	private GameBuilder fourPlayersGame = new GameBuilder(4);
	private GameBuilder fivePlayersGame = new GameBuilder(5);
	private GameBuilder sixPlayersGame = new GameBuilder(6);
	
	public IGame searchGame(int capacity, IPlayer player) throws IllegalArgumentException, UnexpectedException, RemoteException, InterruptedException {
		System.out.println(String.format("Looking for a %d players game", capacity));
		switch (capacity) {
		case 2:
			return this.twoPlayersGame.waitForPlayers(player);
		case 3:
			return this.threePlayersGame.waitForPlayers(player);
		case 4:
			return this.fourPlayersGame.waitForPlayers(player);
		case 5:
			return this.fivePlayersGame.waitForPlayers(player);
		case 6:
			return this.sixPlayersGame.waitForPlayers(player);
		default:
			throw new IllegalArgumentException("Only games with two to six players are available");
		}
	}
}
