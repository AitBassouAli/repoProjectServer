/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Conversation;
import bean.User;
import java.util.Date;
import java.util.List;

/**
 *
 * @author HP
 */
public class ConversationFacade extends AbstractFacade<Conversation> {

    public ConversationFacade() {
        super(Conversation.class);
    }

    public Conversation createConversation(Conversation conversation) {
        Conversation senderReciever = findBySenderOrRecevier(conversation);
        if (senderReciever == null) {
            conversation.setId(generateId("Conversation", "id"));
            conversation.setDateModification(new Date());
            create(conversation);
            return conversation;
        }
        return senderReciever;
    }

    public Conversation findBySenderOrRecevier(Conversation conversation) {
        Conversation senderC = findBySender(conversation);
        if (senderC == null) {
            Conversation recieverC = findByReciever(conversation);
            if (recieverC == null) {
                return null;
            } else {
                return recieverC;
            }
        }
        return senderC;
    }

    public Conversation findBySender(Conversation conversation) {
        String requette = "SELECT c FROM Conversation c WHERE c.sender.id = " + conversation.getSender().getId();
        requette += " AND c.reciever.id = " + conversation.getReciever().getId();
        System.out.println(requette);
        try {
            conversation = (Conversation) getEntityManager().createQuery(requette).getSingleResult();
            if (conversation != null) {
                return conversation;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public Conversation findByReciever(Conversation conversation) {
        String requette = "SELECT c FROM Conversation c WHERE c.sender.id = " + conversation.getReciever().getId();
        requette += " AND c.reciever.id = " + conversation.getSender().getId();
        System.out.println(requette);
        try {
            conversation = (Conversation) getEntityManager().createQuery(requette).getSingleResult();
            if (conversation != null) {
                return conversation;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public boolean removeConversation(Conversation conversation) {
        if (conversation == null) {
            return false;
        }
        remove(conversation);
        return true;
    }

    public List<Object> getConversationsByUser(User user) {
        String rqt = "SELECT c FROM Conversation c WHERE c.sender.id = " + user.getId() + " OR c.reciever.id = " + user.getId() + " ORDER BY c.dateModification DESC";
        System.out.println(rqt);
        return getEntityManager().createQuery(rqt).getResultList();
    }

}
