package ru.ac.uniyar.wordplay;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Game {
    private String theme;
    private StringProperty currentWord;
    private final List<String> words;
    private final List<String> newWords;
    private List<String> usedWords;

    public final Thread gameThread = new Thread(this::save);

    public StringProperty currentWordStringProperty() {
        if (currentWord == null) currentWord = new SimpleStringProperty();
        return currentWord;
    }
    public void setCurrentWord(String value) {currentWordStringProperty().setValue(value);}
    public void setTheme(String theme) {this.theme = theme;}
    public String getCurrentWord() {return currentWordStringProperty().get();}
    public List<String> getUsedWords() {return usedWords;}
    public List<String> getNewWords(){return newWords;}
    public String getTheme() {return theme;}
    public List<String> getWords() {return words;}
    public Game(String theme){
        this.theme = theme;
        words = new LinkedList<>();
        usedWords = new LinkedList<>();
        newWords = new LinkedList<>();
        getWordsFromFile();
    }
    private void getWordsFromFile(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(theme+".txt"));
            String str;
            while ((str = reader.readLine()) != null) {
                words.add(str);
            }
            reader.close();
        }catch (IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Ошибка чтения базы слов");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    public void nextWord(String usersWord) {
        String word = words
                .stream()
                .filter(w -> w.toLowerCase().charAt(0) == usersWord.charAt(usersWord.length()-1))
                .findFirst()
                .orElse("Кончились слова :(");
        words.remove(word);
        setCurrentWord(word);
    }


    private void save(){
        try {
            FileWriter writer = new FileWriter(theme+".txt", true);
            for (String str: newWords){
                writer.append("\n"+str);
            }
            writer.close();
        }catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("alertStyle.css")).toExternalForm());
            alert.setHeaderText("Ошибка записи слов в базу");
            alert.showAndWait();
        }
    }
}
