package br.iesb.chat;

import javax.swing.JOptionPane;

import br.iesb.client.MultiThreadChatServerSync;
import br.iesb.server.ChatClient;

public class ClientServer {

    public static void main(String[] args) {
	Object[] selectioValues = { "Server", "Client" };
	String initialSection = "Server";
	Object selection = JOptionPane.showInputDialog(null, "Login as : ", "MyChatApp", JOptionPane.QUESTION_MESSAGE, null, selectioValues, initialSection);
	if ("Server".equals(selection)) {
	    String[] arguments = new String[] {};
	    MultiThreadChatServerSync.main(arguments);
	} else if ("Client".equals(selection)) {
	    String IPServer = JOptionPane.showInputDialog("Enter the Server ip adress");
	    String[] arguments = new String[] { IPServer };
	    ChatClient.main(arguments);
	}
    }

}
