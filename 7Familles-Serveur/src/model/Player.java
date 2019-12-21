package model;

import remote.IPlayer;
import remote.IBoundedBuffer;

public class Player {
	public IPlayer remote;
	public IBoundedBuffer tampon;
	
	public Player(IPlayer remote, IBoundedBuffer tampon) {
		this.remote = remote;
		this.tampon = tampon;
	}
}
