package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

public class Pioche {

	private Stack<Carte> cartes;
	
	public Pioche() {
		this.cartes = new Stack<Carte>();
		this.cartes.addAll(this.init());
	}
	
	private List<Carte> init() {
		List<Carte> Cartes = Arrays.asList(new Carte(Famille.Stark, Statut.Grand_father, "Rickard"),
				new Carte(Famille.Stark, Statut.Grand_mother, "Lyarra"), new Carte(Famille.Stark, Statut.Father, "Eddard"),
				new Carte(Famille.Stark, Statut.Mother, "Catelyn"), new Carte(Famille.Stark, Statut.Son, "Brandon"),
				new Carte(Famille.Stark, Statut.Daugther, "Arya"),

				new Carte(Famille.Barbapapa, Statut.Grand_father, "Talus"),
				new Carte(Famille.Barbapapa, Statut.Grand_mother, "Annette"),
				new Carte(Famille.Barbapapa, Statut.Father, "Barbapapa"),
				new Carte(Famille.Barbapapa, Statut.Mother, "Barbamama"),
				new Carte(Famille.Barbapapa, Statut.Son, "Barbidur"),
				new Carte(Famille.Barbapapa, Statut.Daugther, "Barbalala"),

				new Carte(Famille.Uzumaki, Statut.Grand_father, "Minato"),
				new Carte(Famille.Uzumaki, Statut.Grand_mother, "Kushina"),
				new Carte(Famille.Uzumaki, Statut.Father, "Naruto"), new Carte(Famille.Uzumaki, Statut.Mother, "Hinata"),
				new Carte(Famille.Uzumaki, Statut.Son, "Boruto"), new Carte(Famille.Uzumaki, Statut.Daugther, "Himawari"),

				new Carte(Famille.Simpson, Statut.Grand_father, "Abraham"),
				new Carte(Famille.Simpson, Statut.Grand_mother, "Mona"), new Carte(Famille.Simpson, Statut.Father, "Homer"),
				new Carte(Famille.Simpson, Statut.Mother, "Marge"), new Carte(Famille.Simpson, Statut.Son, "Bart"),
				new Carte(Famille.Simpson, Statut.Daugther, "Lisa"),

				new Carte(Famille.Potter, Statut.Grand_father, "James"),
				new Carte(Famille.Potter, Statut.Grand_mother, "Lily"), new Carte(Famille.Potter, Statut.Father, "Harry"),
				new Carte(Famille.Potter, Statut.Mother, "Ginevra"), new Carte(Famille.Potter, Statut.Son, "Albus Severus"),
				new Carte(Famille.Potter, Statut.Daugther, "Lily Luna"),

				new Carte(Famille.Skywalker, Statut.Grand_father, "The force ?!"),
				new Carte(Famille.Skywalker, Statut.Grand_mother, "Shmi"),
				new Carte(Famille.Skywalker, Statut.Father, "Anakin"), new Carte(Famille.Skywalker, Statut.Mother, "Padmé"),
				new Carte(Famille.Skywalker, Statut.Son, "Luke"), new Carte(Famille.Skywalker, Statut.Daugther, "Leia"),

				new Carte(Famille.Smiths, Statut.Grand_father, "Willard"),
				new Carte(Famille.Smiths, Statut.Grand_mother, "Caroline"),
				new Carte(Famille.Smiths, Statut.Father, "Will"), new Carte(Famille.Smiths, Statut.Mother, "Jada"),
				new Carte(Famille.Smiths, Statut.Son, "Jaden"), new Carte(Famille.Smiths, Statut.Daugther, "Willow"));
		Collections.shuffle(Cartes);
		return Cartes;
	}
	
	public Carte piocher() throws EmptyStackException {
		return this.cartes.pop();
	}
	
	public List<Carte> piocher(int nombre) throws EmptyStackException {
		List<Carte> cartes = new ArrayList<Carte>();
		for (int i = 0; i < nombre; i++) {
			cartes.add(this.piocher());
		}
		return cartes;
	}
}
