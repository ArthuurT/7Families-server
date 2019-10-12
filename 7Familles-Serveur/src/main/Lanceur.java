package main;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import model.Serveur;
import remote.IServeur;

public class Lanceur {

	public static void main(String[] args) {
		int port = Integer.parseInt(args[0]);
		System.setProperty(
			"java.security.policy",
			"file:///home/asao/Documents/Universite/Semestre9/Middlewares/7Families/7Families-server/7Familles-Serveur/src/security.policy");
		if (System.getSecurityManager() == null) {
		    System.setSecurityManager(new SecurityManager());
		}
		try {
			IServeur serveur = new Serveur();
			IServeur skeleton = (IServeur) UnicastRemoteObject.exportObject(serveur, 0);
			Registry registry = LocateRegistry.createRegistry(port);
			registry.rebind("serveur", skeleton);
		} catch(RemoteException e) {
			e.printStackTrace();
		}
	}
}
