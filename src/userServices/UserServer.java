/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userServices;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TextArea;
/**
 *
 * @author HP
 */
public class UserServer extends Thread {

    private TextArea consoleServer;
    private ServerSocket serverSocket;
    private List<Socket> sockets = new ArrayList<Socket>();

    public UserServer(TextArea ConsoleServer) {
        this.consoleServer = ConsoleServer;
    }

    public void arreter() throws IOException {
        serverSocket.close();
        this.stop();
        consoleServer.appendText("Le serveur Utilisature est arreter !!!\n");
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(60830);
            consoleServer.appendText("Le serveur Utilisature est demarer...\n");
            consoleServer.appendText("En attent d'un requette Utilisature...\n");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("accepting a new UserSocket");
                sockets.add(socket);
                new ServiceUser(socket, consoleServer, sockets).start();
            }
        } catch (Exception e) {
        }

    }

}
