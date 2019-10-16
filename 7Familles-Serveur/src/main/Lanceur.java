package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

import model.Serveur;
import remote.IServeur;

public class Lanceur {

	public static void main(String[] args) {
		try {
			String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			String configPath = rootPath + "config.properties";
			
			Properties properties = new Properties();
			properties.load(new FileInputStream(configPath));
			
			String securityPolicyPath = "file:" + properties.getProperty("security.file.path");
			System.setProperty("java.security.policy", securityPolicyPath);
			if (System.getSecurityManager() == null) {
			    System.setSecurityManager(new SecurityManager());
			}
			
			int port = Integer.parseInt(properties.getProperty("server.port"));
			String serverInterface = properties.getProperty("server.interface");
			
			IServeur serveur = new Serveur();
			IServeur skeleton = (IServeur) UnicastRemoteObject.exportObject(serveur, 0);
			Registry registry = LocateRegistry.createRegistry(port);
			registry.rebind(serverInterface, skeleton);
			
			System.out.println(String.format("Server running on port %d", port));
		} catch(RemoteException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
