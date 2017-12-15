/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.User;
import java.io.IOException;
import java.util.List;
import javax.mail.MessagingException;
import util.EmailUtil;
import util.HashageUtil;
import util.RandomStringUtil;

/**
 *
 * @author HP
 */
public class UserFacade extends AbstractFacade<User> {

    public UserFacade() {
        super(User.class);
    }
    ConnectedUsersFacade connectedUsersFacade = new ConnectedUsersFacade();

    public User find(String id) {
        try {
            User user = (User) getEntityManager().createQuery("select u from User u where u.userName ='" + id + "'").getSingleResult();
            if (user != null) {
                return user;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public User findByCreteres(User user) {
        try {
            String requette = "select u from User u where u.id=" + user.getId() + " And u.userName ='" + user.getUserName() + "' And u.email ='" + user.getEmail() + "'";
            User user1 = (User) getEntityManager().createQuery(requette).getSingleResult();
            if (user1 != null) {
                return user1;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public List<Object> getUsers() {
        List<Object> connectedUsers = connectedUsersFacade.getUsers();
        return connectedUsers;
    }

    public List<User> findConnectedUsers() {
        String requette = "Select u from User u where u.status = 1";
        return getEntityManager().createQuery(requette).getResultList();

    }

    public List<Object> findUsersObjectsContaints(String motRechercher) {
        String requette = "Select u from User u where (u.userName LIKE '%" + motRechercher + "%' OR u.email LIKE '%" + motRechercher + "%') AND u.status = 1";
        System.out.println(requette);
        return getEntityManager().createQuery(requette).getResultList();
    }

    public User deconnecter(User user) {
        user.setStatus(false);
        user.setIpAdress(null);
        connectedUsersFacade.deconnect(user);
        edit(user);
        return null;
    }

    public User modifier(User newUser, User oldUser) {
        User loadedUser = findByCreteres(newUser);
        if (loadedUser == null) {
            User userEmail = findByEmail(newUser.getEmail());
            User userUserName = find(newUser.getUserName());
            if ((userUserName != null && !userUserName.getId().equals(newUser.getId())) || (userEmail != null && !userEmail.getId().equals(newUser.getId()))) {
                return null;
            }
        }
        return changer(newUser, oldUser);
    }

    public User changer(User newUser, User oldUser) {
        if (!newUser.getPassword().isEmpty()) {
            newUser.setPassword(HashageUtil.sha256(newUser.getPassword()));
        } else {
            newUser.setPassword(oldUser.getPassword());
        }
        newUser.setId(oldUser.getId());
        edit(newUser);
        return newUser;
    }

    public Object[] seConnecter(User user) throws IOException {
        if (user == null || user.getUserName() == null) {
            //Veuilliez saisir votre login
            return new Object[]{-1, null};
        } else {
            User loadedUser = find(user.getUserName());
            if (loadedUser == null) {
                return new Object[]{-2, null};
            } else if (connectedUsersFacade.findByUser(loadedUser) != null) {
                return new Object[]{-6, null};
            } else if (!loadedUser.getPassword().equals(HashageUtil.sha256(user.getPassword()))) {
                if (loadedUser.getNbrCnx() < 3) {
                    System.out.println(" loadedUser.getNbrCnx() < 3 ::: " + loadedUser.getNbrCnx());
                    loadedUser.setNbrCnx(loadedUser.getNbrCnx() + 1);
                    edit(loadedUser);
                    return new Object[]{-3, null};
                } else {//(loadedUser.getNbrCnx() >= 3)
                    System.out.println(" loadedUser.getNbrCnx() >= 3::: " + loadedUser.getNbrCnx());
                    loadedUser.setBlocked(1);
                    edit(loadedUser);
                    return new Object[]{-4, null};
                }
            } else if (loadedUser.getBlocked() == 1) {
                // Cet utilisateur est bloqué
                return new Object[]{-5, null};
            } else {
                loadedUser.setNbrCnx(0);
                loadedUser.setStatus(true);
                edit(loadedUser);
                user = clone(loadedUser);
                user.setPassword(null);
                return new Object[]{1, loadedUser};
            }
        }
    }

    public Object[] addUser(User user) {

        User loadedUser = find(user.getUserName());
        User loadedUserEmail = findByEmail(user.getEmail());
        if (loadedUser != null || loadedUserEmail != null) {
            if (loadedUser != null) {
                return new Object[]{-1, null};
            }
            return new Object[]{-2, null};
        } else {
            user.setNbrCnx(0);
            user.setBlocked(0);
            user.setPassword(HashageUtil.sha256(user.getPassword()));
            create(user);
            return new Object[]{1, user};
        }

    }

    public User findByEmail(String email) {
        try {
            User user = (User) getEntityManager().createQuery("select u from User u where u.email LIKE '" + email + "'").getSingleResult();
            if (user != null) {
                return user;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public int sendPW(String email) {
        User user = findByEmail(email);
        if (user == null) {
            return -1;
        } else {
            String pw = RandomStringUtil.generate();
            String msg = "Bienvenu Mr. " + user.getUserName() + ",<br/>"
                    + "D'après votre demande de reinitialiser le mot de passe de votre compte ChatApp, nous avons genérer ce mot de passe pour vous.\n"
                    + "<br/><br/>"
                    + "      Nouveau Mot de Passe: <br/><center><b>"
                    + pw
                    + "</b></center><br/><br/><b><i>PS:</i></b>  SVP changer ce mot de passe apres que vous avez connecter pour des raisons du securité .<br/> Cree votre propre mot de passe";
            try {
                EmailUtil.sendMail("pfetaxe@gmail.com", "taxeCommune", msg, email, "Demande de reanitialisation du mot de pass");
            } catch (MessagingException ex) {
                //  Logger.getLogger(UserFacade.class.getName()).log(Level.SEVERE, null, ex);
                return -2;
            }

            user.setBlocked(0);
            user.setPassword(HashageUtil.sha256(pw));
            edit(user);
            return 1;
        }
    }

    public User clone(User user) {
        User clone = new User();
        clone.setUserName(user.getUserName());
        clone.setBlocked(user.getBlocked());
        clone.setNbrCnx(user.getNbrCnx());
        clone.setNom(user.getNom());
        clone.setPassword(user.getPassword());
        clone.setPrenom(user.getPrenom());

        return clone;
    }

}
