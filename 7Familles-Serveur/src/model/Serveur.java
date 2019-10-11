package model;

import java.rmi.RemoteException;

import exception.GameFullException;
import remote.IJeu;
import remote.IJoueur;
import remote.IServeur;

public class Serveur implements IServeur {

	private Jeu jeu = null;
	
	@Override
	public IJeu join(IJoueur joueur) throws RemoteException {
		try {
			if (this.jeu == null) {
				this.jeu = new Jeu();
			}
			this.jeu.enregistrer(joueur);
			return this.jeu;
		} catch (GameFullException e) {
			return null;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

}
