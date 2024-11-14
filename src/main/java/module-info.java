module es.guillearana.practica2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens es.guillearana.practica2 to javafx.fxml;
    exports es.guillearana.practica2;
}