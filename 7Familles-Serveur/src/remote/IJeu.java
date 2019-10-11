package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import model.Carte;

public interface IJeu extends Remote {
	
	public void jouer(IJoueur joueur) throws RemoteException, InterruptedException;
	
	public Carte piocher(Carte expected) throws RemoteException;
	
	public boolean finPartie() throws RemoteException;
}
