package model;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

import exception.GameFullException;
import remote.IJeu;
import remote.IJoueur;

public class Jeu extends UnicastRemoteObject implements IJeu {

	private static final long serialVersionUID = 8594995881169048069L;

	private static final int MAX_CLIENT = 2;
	private static final int NB_CARTE = 6;
	
	private List<IJoueur> joueurs;
	private int joueurCourant; // index of the current player
	private Pioche pioche;
	
	public Jeu() throws RemoteException {
		super();
		this.joueurs = new ArrayList<IJoueur>();
		this.joueurCourant = 0;
		this.pioche = new Pioche();
	}
	
	private boolean complet() {
		return this.joueurs.size() == Jeu.MAX_CLIENT;
	}

	private void initOpposants() throws RemoteException {		
		for (IJoueur joueur : this.joueurs) {
			for (IJoueur opposant : this.joueurs) {
				if (!joueur.equals(opposant)) {
					joueur.definir(opposant);
				}
			}
		}
	}
	
	private void initMains() throws RemoteException, EmptyStackException {
		for (IJoueur joueur : this.joueurs) {
			joueur.donner(this.pioche.piocher(Jeu.NB_CARTE));
		}
	}
	
	private void init() throws RemoteException, EmptyStackException {
		this.initOpposants();
		this.initMains();
	}
	
	public synchronized void enregistrer(IJoueur joueur) throws GameFullException, InterruptedException, 
			RemoteException, EmptyStackException {
		if (this.complet()) {
			throw new GameFullException("The game is full... Try later !");
		}
		this.joueurs.add(joueur);
		System.err.println(String.format("Joueurs : %d/%d", this.joueurs.size(), Jeu.MAX_CLIENT));
		if (this.complet()) {
			System.err.println("Initialisation");
			this.init();
			notifyAll();
		} else {
			wait();
		}
	}

	@Override
	public synchronized void jouer(IJoueur joueur) throws RemoteException, InterruptedException {
		while (this.joueurs.get(this.joueurCourant) != joueur) {
			wait();
		}
	}

	@Override
	public Carte piocher(Carte expected) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean finPartie() throws RemoteException {
		int nbFamilles = 0;
		for (IJoueur joueur : this.joueurs) {
			nbFamilles += joueur.familles().getFamilles().size();
		}
		return nbFamilles == 7;
	}
}
