package br.iesb.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ClientThread extends Thread {

    private String clientName = null;
    private BufferedReader buffer = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private final ClientThread[] threads;
    private int maxClientsCount;

    public ClientThread(Socket clientSocket, ClientThread[] threads) {
	this.clientSocket = clientSocket;
	this.threads = threads;
	maxClientsCount = threads.length;
    }

    public void run() {
	int maxClientsCount = this.maxClientsCount;
	ClientThread[] threads = this.threads;
	try {
	    /*
	     * Create input and output streams for this client.
	     */
	    buffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	    os = new PrintStream(clientSocket.getOutputStream());
	    String name;
	    while (true) {
		os.println("Enter your name.");
		name = buffer.readLine().trim();
		if (name.indexOf('@') == -1) {
		    break;
		} else {
		    os.println("The name should not contain '@' character.");
		}
	    }
	    /* Welcome the new the client. */
	    os.println("Bem-vindo(a) " + name + " à nossa sala de chat.\nPara sair digite /sair.");
	    synchronized (this) {
		for (int i = 0; i < maxClientsCount; i++) {
		    if (threads[i] != null && threads[i] == this) {
			clientName = "@" + name;
			break;
		    }
		}
		for (int i = 0; i < maxClientsCount; i++) {
		    if (threads[i] != null && threads[i] != this) {
			threads[i].os.println("*** Um novo usuário (" + name + ") entrou na sala de chat !!! ***");
		    }
		}
	    }
	    /* Start the conversation. */
	    while (true) {
		String line = buffer.readLine();
		if (line.startsWith("/sair")) {
		    break;
		}
		/* If the message is private sent it to the given client. */
		if (line.startsWith("@")) {
		    String[] words = line.split("\\s", 2);
		    if (words.length > 1 && words[1] != null) {
			words[1] = words[1].trim();
			if (!words[1].isEmpty()) {
			    synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
				    if (threads[i] != null && threads[i] != this && threads[i].clientName != null && threads[i].clientName.equals(words[0])) {
					threads[i].os.println("<" + name + "> " + words[1]);
					/*
					 * Echo this message to let the client
					 * know the private message was sent.
					 */
					this.os.println(">" + name + "> " + words[1]);
					break;
				    }
				}
			    }
			}
		    }
		} else {
		    /* The message is public, broadcast it to all other clients. */
		    synchronized (this) {
			for (int i = 0; i < maxClientsCount; i++) {
			    if (threads[i] != null && threads[i].clientName != null) {
				threads[i].os.println("<" + name + "> " + line);
			    }
			}
		    }
		}
	    }
	    synchronized (this) {
		for (int i = 0; i < maxClientsCount; i++) {
		    if (threads[i] != null && threads[i] != this && threads[i].clientName != null) {
			threads[i].os.println("*** O usuário (" + name + ") está saindo da sala de chat !!! ***");
		    }
		}
	    }
	    os.println("*** Tchau " + name + " ***");
	    /*
	     * Clean up. Set the current thread variable to null so that a new
	     * client could be accepted by the server.
	     */
	    synchronized (this) {
		for (int i = 0; i < maxClientsCount; i++) {
		    if (threads[i] == this) {
			threads[i] = null;
		    }
		}
	    }
	    /*
	     * Close the output stream, close the input stream, close the
	     * socket.
	     */
	    buffer.close();
	    os.close();
	    clientSocket.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

}
