package ru.ac.uniyar.wordplay;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.Optional;

public class MainController{
    @FXML
    private void exitAction(ActionEvent event) {
        Platform.exit();
    }
    @FXML
    private void urbanTheme(){newGame("Города");}
    @FXML
    private void chemistryTheme(){newGame("Химические элементы");}
    @FXML
    private void animalsTheme(){newGame("Животные");}
    @FXML
    private void plantsTheme(){newGame("Растения");}
    @FXML
    private void foodTheme(){newGame("Еда");}
    @FXML
    private void themeChoice() {
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<String>(
                "Города","Города","Животные","Растения","Еда","Химические элементы");
        choiceDialog.setTitle("Выбор темы");
        choiceDialog.setContentText("Тема");
        Optional<String> result = choiceDialog.showAndWait();
        result.ifPresent(this::newGame);
    }
    @FXML
    private void newGame(String theme) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("GameView.fxml"));
            Parent root = loader.load();
            GameController controller = loader.getController();
            controller.setGame(theme);
            Stage dialog = new Stage();
            dialog.setTitle("Game");
            Scene scene = new Scene(root);
            dialog.setScene(scene);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();
        } catch (Exception e){
            System.out.println("Loader error");
            e.printStackTrace();
        }
    }
    @FXML
    private void aboutAction(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("О программе");
        alert.setHeaderText("Программа для игры в слова с компьютером");
        alert.setContentText("Он обязательно выучит больше слов...");
        alert.showAndWait();
    }
}
