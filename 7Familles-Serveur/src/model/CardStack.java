package model;

import java.util.Arrays;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

public class CardStack {

	private Stack<Card> cards;
	
	public CardStack() {
		this.cards = new Stack<Card>();
		this.cards.addAll(this.init());
	}
	
	private List<Card> init() {
		List<Card> cards = Arrays.asList(new Card(Family.Stark, Status.Grand_father, "Rickard"),
				new Card(Family.Stark, Status.Grand_mother, "Lyarra"), new Card(Family.Stark, Status.Father, "Eddard"),
				new Card(Family.Stark, Status.Mother, "Catelyn"), new Card(Family.Stark, Status.Son, "Brandon"),
				new Card(Family.Stark, Status.Daugther, "Arya"),

				new Card(Family.Barbapapa, Status.Grand_father, "Talus"),
				new Card(Family.Barbapapa, Status.Grand_mother, "Annette"),
				new Card(Family.Barbapapa, Status.Father, "Barbapapa"),
				new Card(Family.Barbapapa, Status.Mother, "Barbamama"),
				new Card(Family.Barbapapa, Status.Son, "Barbidur"),
				new Card(Family.Barbapapa, Status.Daugther, "Barbalala"),

				new Card(Family.Uzumaki, Status.Grand_father, "Minato"),
				new Card(Family.Uzumaki, Status.Grand_mother, "Kushina"),
				new Card(Family.Uzumaki, Status.Father, "Naruto"), new Card(Family.Uzumaki, Status.Mother, "Hinata"),
				new Card(Family.Uzumaki, Status.Son, "Boruto"), new Card(Family.Uzumaki, Status.Daugther, "Himawari"),

				new Card(Family.Simpson, Status.Grand_father, "Abraham"),
				new Card(Family.Simpson, Status.Grand_mother, "Mona"), new Card(Family.Simpson, Status.Father, "Homer"),
				new Card(Family.Simpson, Status.Mother, "Marge"), new Card(Family.Simpson, Status.Son, "Bart"),
				new Card(Family.Simpson, Status.Daugther, "Lisa"),

				new Card(Family.Potter, Status.Grand_father, "James"),
				new Card(Family.Potter, Status.Grand_mother, "Lily"), new Card(Family.Potter, Status.Father, "Harry"),
				new Card(Family.Potter, Status.Mother, "Ginevra"), new Card(Family.Potter, Status.Son, "Albus Severus"),
				new Card(Family.Potter, Status.Daugther, "Lily Luna"),

				new Card(Family.Skywalker, Status.Grand_father, "The force ?!"),
				new Card(Family.Skywalker, Status.Grand_mother, "Shmi"),
				new Card(Family.Skywalker, Status.Father, "Anakin"), new Card(Family.Skywalker, Status.Mother, "Padmé"),
				new Card(Family.Skywalker, Status.Son, "Luke"), new Card(Family.Skywalker, Status.Daugther, "Leia"),

				new Card(Family.Smiths, Status.Grand_father, "Willard"),
				new Card(Family.Smiths, Status.Grand_mother, "Caroline"),
				new Card(Family.Smiths, Status.Father, "Will"), new Card(Family.Smiths, Status.Mother, "Jada"),
				new Card(Family.Smiths, Status.Son, "Jaden"), new Card(Family.Smiths, Status.Daugther, "Willow"));
		Collections.shuffle(cards);
		return cards;
	}
	
	public Card pickCard() throws EmptyStackException {
		return this.cards.pop();
	}
}
