/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messageServices;

import bean.Conversation;
import bean.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextArea;
import service.ConversationFacade;

/**
 *
 * @author abdel
 */
class Service extends Thread {
    
    private Socket socket;
    private TextArea console;
    Vector<Socket> lesSocket;
    ConversationFacade conversationFacade = new ConversationFacade();
    
    public Service(Socket socket, Vector<Socket> lesSocket, TextArea console) {
        this.socket = socket;
        this.console = console;
        this.lesSocket = lesSocket;
    }
    
    @Override
    public void run() {
        InputStream is = null;
        try {
            console.appendText("Connexion du client: " + socket.getRemoteSocketAddress() + "\n");
            while (true) {
                ObjectInputStream inObject = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) inObject.readObject();
                for (int i = 0; i < lesSocket.size(); i++) {
                    if (lesSocket.elementAt(i).getPort() == message.getPort()) {
                        editConversationDate(message.getConversation());
                        ObjectOutputStream outObject = new ObjectOutputStream(lesSocket.elementAt(i).getOutputStream());
                        outObject.writeObject(message);
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
    
    public void editConversationDate(Conversation conversation) {
        conversation.setDateModification(new Date());
        conversationFacade.edit(conversation);
    }
    
}
