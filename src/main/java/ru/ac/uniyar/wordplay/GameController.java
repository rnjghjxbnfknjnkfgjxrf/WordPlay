package ru.ac.uniyar.wordplay;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GameController {
    @FXML
    private ListView<String> listView;
    @FXML
    private SplitPane gameWindow;
    @FXML
    private Text currentWord;
    @FXML
    private Text theme;
    @FXML
    private TextField usersWordField;
    private Game game;
    private List<String> words;
    private String usersWord;
    private List<String> pristineWordsList;
    private Map<Character, Long> allChars;
    public void setGame(String theme){
        game = new Game(theme);
        words = game.getWords();
        pristineWordsList = new LinkedList<>(words);
        getAllChars();
        currentWord.textProperty().bind(game.currentWordStringProperty());
        String firstWord = words.remove(new Random().nextInt(words.size()));
        game.setCurrentWord(firstWord);
        game.getUsedWords().add(firstWord);
        this.theme.setText(game.getTheme());
        listView.setItems(FXCollections.observableList(game.getUsedWords()));
        Runtime.getRuntime().addShutdownHook(game.gameThread);
    }
    //получение слова, введенного пользователем и проверка, подходит ли оно
    @FXML
    private void getUsersWord(){
        usersWord = usersWordField.getText();
        if (isWordWriteCorrectChecker()
                && isWordComplyToGameRulesChecker()
                && isWordIsNotUsedChecker()
                && isWordIsNotNewChecker()){
            try {
                words.remove(usersWord);
                String word = findWord();
                listView.refresh();
                words.remove(word);
                game.setCurrentWord(word);
            }catch (GameOutOfWordsException e){
                showMessage(e.getMessage(), Alert.AlertType.ERROR);
                Stage stage = (Stage)gameWindow.getScene().getWindow();
                stage.close();
            }
        }
    }
    //далее идут фунции, проверяющие валидность введенного слова
    private boolean isWordIsNotUsedChecker() {
        if (game.getUsedWords().contains(usersWord)){
            showMessage("Данное слово уже использовалось!", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    private boolean isWordIsNotNewChecker(){
        if (!words.contains(usersWord)){
            if (wordAddingMessage(usersWord)) {
                game.getNewWords().add(usersWord);
                game.getUsedWords().add(usersWord);
                listView.refresh();
                return true;
            }
            return false;
        }
        return true;
    }
    private boolean isWordWriteCorrectChecker(){
        String ruleRegex;
        switch (game.getTheme()){
            case "Города":
            case "Еда":
                ruleRegex = "[a-zA-Z \\u0400-\\u04FF]+[ -]?[a-zA-Z \\u0400-\\u04FF]*-?[a-zA-Z \\u0400-\\u04FF]*";
                break;
            case "Химические элементы":
                ruleRegex = "[a-zA-Z \\u0400-\\u04FF]+";
                break;
            default:
                ruleRegex = "[a-zA-Z \\u0400-\\u04FF]+ ?[a-zA-Z \\u0400-\\u04FF]*";
                break;
        }
        if (!usersWord.toLowerCase().matches(ruleRegex)){
            showMessage("Неправильный формат слова", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    private boolean isWordComplyToGameRulesChecker(){
        char currentWordLastChar = currentWord.getText().toLowerCase().charAt(currentWord.getText().length()-1);
        if (usersWord.toLowerCase().charAt(0) != currentWordLastChar){
            showMessage(String.format("Ваше слова не начинается на \"%c\"!", currentWordLastChar), Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    /*
     * Данное сообщение возникает, если введенного пользователем слова нет в базе компьютера.
     * Если пользователь подтвердит корректность введенного слова, оно будет добавлено в базу. 
     *  */
    private boolean wordAddingMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Неизвестное слово");
        alert.setHeaderText("Данного слова нет в базе данных. Вы уверены в правильности написания?");
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty()){
            return false;
        }else return result.get() == ButtonType.OK;
    }

    private void showMessage(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Ошибка");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
    /*
    * Отбор всех буквы, с которых начинаются слова в базе слов, и их последующая фильтрации
    * по частоте появления.
    * */
    private void getAllChars(){
        allChars = pristineWordsList
                .stream()
                .map(str -> str.toLowerCase().charAt(0))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k, v) -> k, LinkedHashMap::new));
    }
    /*
     * Поиск ответа на слово пользовтеля.
     * Сначала идет отбор слов, которые кончаются на самые непопулярные буквы.
     * Если такого слов не найдено, компьютер ищет любое подходящее слово.
     * Если же не будет и такого, то будет выдано сообщение о том, что слова в базе кончились.
     * */
    private String findWord() throws GameOutOfWordsException{
        String requiredWord = "";
        for (char rareChar: allChars.keySet()){
            if (!requiredWord.equals("")) {
                return requiredWord;
            }
            requiredWord = words
                    .stream()
                    .filter(w -> w.toLowerCase().charAt(0) == usersWord.charAt(usersWord.length()-1))
                    .filter(w -> w.toLowerCase().charAt(w.length() - 1) == rareChar).findFirst().orElse("");
        }
        requiredWord = words
                .stream()
                .filter(w -> w.toLowerCase().charAt(0) == usersWord.charAt(usersWord.length()-1))
                .findFirst()
                .orElseThrow(() -> new GameOutOfWordsException("Кончились слова :("));
        return requiredWord;
    }
}

class GameOutOfWordsException extends Exception {
    public GameOutOfWordsException(String message) {
        super(message);
    }
}