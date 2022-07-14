module ru.ac.uniyar.wordplay {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.ac.uniyar.wordplay to javafx.fxml;
    exports ru.ac.uniyar.wordplay;
}