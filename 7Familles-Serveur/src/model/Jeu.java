package model;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

import exception.GameFullException;
import remote.IJeu;
import remote.IJoueur;

public class Jeu extends UnicastRemoteObject implements IJeu {

	private static final long serialVersionUID = 8594995881169048069L;

	private static final int MAX_JOUEURS = 2;
	private static final int NB_CARTE = 6;
	
	private EtatJeu etatJeu;
	private int nbJoueursMax;
	
	private List<IJoueur> joueurs;
	private int joueurCourant; // index of the current player
	private Pioche pioche;
	
	public Jeu(int nbJoueursMax) throws RemoteException {
		super();
		this.etatJeu = EtatJeu.ATTENTE_JOUEURS;
		this.nbJoueursMax = nbJoueursMax;
		this.joueurs = new ArrayList<IJoueur>();
		this.joueurCourant = 0;
		this.pioche = new Pioche();
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
	
	@Override
	public synchronized void rejoindre(IJoueur joueur) throws GameFullException, RemoteException {
		if (this.etatJeu == EtatJeu.EN_JEU) {
			throw new GameFullException("The game is full... Try later !");
		}
		this.joueurs.add(joueur);
		System.err.println(String.format("Joueurs : %d/%d", this.joueurs.size(), Jeu.MAX_JOUEURS));
		if (this.joueurs.size() == this.nbJoueursMax) {
			System.err.println("Initialisation");
			this.etatJeu = EtatJeu.EN_JEU;
			try {
				this.init();
			} catch (EmptyStackException e) {
				e.printStackTrace();
			}
			notifyAll();
		} else {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public synchronized void jouer(IJoueur joueur) throws RemoteException {
		while (!this.joueurs.get(this.joueurCourant).equals(joueur)) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public synchronized Carte piocher() throws RemoteException {
		try {
			return this.pioche.piocher();
		} catch (EmptyStackException e) {
			return null;
		}
	}

	@Override
	public synchronized boolean finPartie() throws RemoteException {
		int nbFamilles = 0;
		for (IJoueur joueur : this.joueurs) {
			nbFamilles += joueur.familles().getFamilles().size();
		}
		notifyAll();
		return nbFamilles == 7;
	}
	
	@Override
	public boolean estGagnant(IJoueur joueur) throws RemoteException {
		IJoueur gagnant = this.joueurs.get(0);
		int nbFamillesGagnant = gagnant.familles().getFamilles().size();
		LocalDateTime derniereFamilleGagnant = gagnant.familles().getTimestamp();
		for (int i = 1; i < this.joueurs.size(); i++) {
			IJoueur joueurCourant = this.joueurs.get(i);
			int nbFamillesJoueur = joueurCourant.familles().getFamilles().size();
			LocalDateTime derniereFamilleJoueur = joueurCourant.familles().getTimestamp();
			if (nbFamillesJoueur > nbFamillesGagnant) {
				gagnant = joueurCourant;
			} else if (nbFamillesJoueur == nbFamillesGagnant && derniereFamilleJoueur.isBefore(derniereFamilleGagnant)) {
				gagnant = joueurCourant;
			}
		}
		return gagnant.equals(joueur);
	}

	@Override
	public synchronized void passerTour() throws RemoteException {
		this.joueurCourant = (this.joueurCourant + 1) % Jeu.MAX_JOUEURS;
		notifyAll();
	}

	@Override
	public synchronized void quitter(IJoueur joueur) throws RemoteException {
		this.joueurs.remove(joueur);
		if (this.joueurs.size() == 0) {
			this.etatJeu = EtatJeu.ATTENTE_JOUEURS;
			this.pioche = new Pioche();
			this.joueurCourant = 0;
		}
	}
}
