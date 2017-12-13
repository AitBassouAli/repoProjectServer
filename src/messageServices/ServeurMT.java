/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messageServices;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextArea;

public class ServeurMT extends Thread {

    private int nbclient = 0;
    private TextArea consoleServer;
    private ServerSocket serverSocket;
    String[] portIpSepareted;
    Vector<Socket> lesSocket = new Vector<Socket>();

    public ServeurMT(TextArea ConsoleServer) {
        this.consoleServer = ConsoleServer;
    }

    public void arreter() throws IOException {
        serverSocket.close();
        this.stop();
       consoleServer.appendText("Le serveur est arreter !!!\n");
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(50834);
            consoleServer.appendText("Le serveur est demarer...\n");
            consoleServer.appendText("En attent d'un client...\n");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("accepting a new socket");
                lesSocket.add(socket);
                new Service(socket, lesSocket, consoleServer).start();
            }
        } catch (Exception ex) {
            System.out.println("exception from ServerMt ****************");
            Logger.getLogger(ServeurMT.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
