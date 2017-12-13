/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.app.serveur;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import messageServices.ServeurMT;
import userServices.UserServer;

/**
 * FXML Controller class
 *
 * @author abdel
 */
public class FXMLServeurController implements Initializable {

    @FXML
    private TextArea serveurTextArea;
    @FXML
    private AnchorPane chatAppAnchorPane;
    @FXML
    private FontAwesomeIconView quitterButton;
    @FXML
    private FontAwesomeIconView minimiserButton;
    @FXML
    private JFXButton demarrerButton;
    @FXML
    private JFXButton arreterButton;
    @FXML
    private JFXButton reinitialiserButton;

    private boolean enable = true;
    private ServeurMT serveur;
    private UserServer userServeur;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void quitterButtonOnMouseClicked(MouseEvent event) {
        try {
         if (!enable) {
                serveur.arreter();
                userServeur.arreter();
            }
        } catch (IOException ex) {
            Logger.getLogger(FXMLServeurController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Platform.exit();
    }

    @FXML
    private void minimiserButtonOnMouseClicked(MouseEvent event) {
        Stage stage = (Stage) ((Text) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void demarrerButtonOnAction(ActionEvent event) {
        if (enable) {
            chatAppAnchorPane.toBack();
            serveur = new ServeurMT(serveurTextArea);
            serveur.start();          
            userServeur = new UserServer(serveurTextArea);
            userServeur.start();
            enable = false;
            demarrerButton.setStyle("-fx-background-color:  #F3F3F3;-fx-border-color:  #1976d2;-fx-background-radius: 2em;-fx-border-radius: 2em;-fx-text-fill: #1976d2;");
            arreterButton.setStyle("-fx-background-color:   #1976d2;-fx-background-radius: 2em;-fx-border-radius: 2em;-fx-text-fill: #F3F3F3;");
        }
    }

    @FXML
    private void arreterButtonOnAction(ActionEvent event) {
        if (!enable) {

            try {
                chatAppAnchorPane.toFront();
                serveur.arreter();
                userServeur.arreter();
                enable = true;
                arreterButton.setStyle("-fx-background-color:  #F3F3F3;-fx-border-color:  #1976d2;-fx-background-radius: 2em;-fx-border-radius: 2em;-fx-text-fill: #1976d2;");
                demarrerButton.setStyle("-fx-background-color:   #1976d2;-fx-background-radius: 2em;-fx-border-radius: 2em;-fx-text-fill: #F3F3F3;");
            } catch (IOException ex) {
                Logger.getLogger(FXMLServeurController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void reinitialiserButtonOnAction(ActionEvent event) {
        serveurTextArea.setText("");
    }

}
