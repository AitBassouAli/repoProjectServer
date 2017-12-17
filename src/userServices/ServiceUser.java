/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userServices;

import bean.ConnectedUsers;
import bean.Conversation;
import bean.User;
import bean.UserService;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Service;
import javafx.scene.control.TextArea;
import service.ConnectedUsersFacade;
import service.ConversationFacade;
import service.PaysFacade;
import service.UserFacade;

/**
 *
 * @author HP
 */
public class ServiceUser extends Thread {

    private Socket socket;
    private TextArea console;
    private List<Socket> sockets;
    ConnectedUsersFacade connectedUsersFacade = new ConnectedUsersFacade();
    ConversationFacade conversationFacade = new ConversationFacade();
    UserFacade userFacade = new UserFacade();
    PaysFacade paysFacade = new PaysFacade();

    public ServiceUser() {
    }

    public ServiceUser(Socket socket, TextArea console, List<Socket> sockets) {
        this.socket = socket;
        this.console = console;
        this.sockets = sockets;
    }

    @Override
    public void run() {
        try {
            console.appendText("Requette du client: " + socket.getRemoteSocketAddress() + "\n");
            while (true) {
                ObjectInputStream inObject = new ObjectInputStream(socket.getInputStream());
                UserService userService = (UserService) inObject.readObject();
                for (int i = 0; i < sockets.size(); i++) {
                    if (sockets.get(i).getPort() == userService.getPort()) {
                        ObjectOutputStream outObject = new ObjectOutputStream(sockets.get(i).getOutputStream());
                        outObject.writeObject(doExecute(userService));
                    }
                }
            }

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                socket.getInputStream().close();
            } catch (IOException ex) {
                Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public UserService doExecute(UserService service) throws IOException {
        Object[] obj;
        switch (service.getServiceToExecute()) {
            case "seConnecter":
                obj = userFacade.seConnecter((User) service.getObjet());
                service.setServiceReturn((int) obj[0]);
                service.setObjet(obj[1]);
                break;
            case "addUser":
                obj = userFacade.addUser((User) service.getObjet());
                service.setServiceReturn((int) obj[0]);
                service.setObjet(obj[1]);
                break;
            case "find":
                User user = (User) service.getObjet();
                service.setObjet(userFacade.find(user.getId()));
                break;
            case "findByCreteres":
                service.setObjet(userFacade.findByCreteres((User) service.getObjet()));
                break;
            case "getUserObjects":
                service.setObjetList(userFacade.getUsers());
                break;
            case "findUsersObjectsContaints":
                service.setObjetList(userFacade.findUsersObjectsContaints((String) service.getObjet()));
                break;
            case "modifier":
                User user1 = (User) service.getObjet();
                user1.setPays(paysFacade.find(1L));
                service.setObjet(userFacade.modifier(user1, service.getConnectedUser()));
                break;
            case "sendPW":
                User userr = (User) service.getObjet();
                service.setServiceReturn(userFacade.sendPW(userr.getEmail()));
                break;
            case "deconnecter":
                service.setObjet(userFacade.deconnecter((User) service.getObjet()));
                break;
            case "findByUser":
                service.setObjet(connectedUsersFacade.findByUser((User) service.getObjet()));
                break;
            case "createConnectedUser":
                connectedUsersFacade.create((ConnectedUsers) service.getObjet());
                break;
            case "findOrCreateConversation":
                service.setObjet(conversationFacade.createConversation((Conversation) service.getObjet()));
                break;
            case "findConversationObjects":
                service.setObjetList(conversationFacade.getConversationsByUser((User) service.getObjet()));
                break;
            case "supprimerConversation":
                conversationFacade.remove((Conversation) service.getObjet());
                break;
            case "findAllPaysObjects":
                service.setObjetList(paysFacade.findAllPaysObjects());
                break;
            default:
                return new UserService();
        }
        return service;
    }
}
