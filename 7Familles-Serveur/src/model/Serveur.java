package model;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import remote.IJeu;
import remote.IServeur;

public class Serveur implements IServeur {

	private Map<String, IJeu> jeux = new HashMap<String, IJeu>();
	
	public Serveur() {
		try {
			Jeu jeu = new Jeu(2);
			this.jeux.put("ALMA", jeu);
		} catch (RemoteException e) {
			e.printStackTrace();
		}		
	}
	
	@Override
	public synchronized boolean creerJeu(String nom, int nbJoueurs) {
		if (this.jeux.containsKey(nom)) {
			return false;
		}
		try {
			Jeu jeu = new Jeu(nbJoueurs);
			this.jeux.put(nom, jeu);
			return true;
		} catch (RemoteException e) {
			return false;
		}
	}
	
	@Override
	public synchronized Map<String, IJeu> listerJeux() {
		return this.jeux;
	}
	
	/*@Override
	public IJeu join(String nom, IJoueur joueur) throws RemoteException {
		if (!this.jeux.containsKey(nom)) {
			return null;
		}
		try {
			Jeu jeu = this.jeux.get(nom);
			jeu.enregistrer(joueur);
			return jeu;
		} catch (GameFullException e) {
			return null;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}*/
}
