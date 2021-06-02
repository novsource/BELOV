package sample.Model;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.DB.DBWorker;

import java.sql.SQLException;

public enum DatabaseObjects {
    CLIENTS("Client","Клиенты"), DISCOUNTS("Discount", "Скидки");

    private String text;
    private String code;

    DatabaseObjects(String code, String text) {
        this.text = text;
        this.code = code;
    }

    public static DatabaseObjects getByCode(String code) {
        for (DatabaseObjects objects: DatabaseObjects.values()) {
            if (objects.code.equals(code)) {
                return objects;
            }
        }
        return null;
    }

    public static ObservableList<DatabaseObjects> nameDBObjects() throws SQLException {
        DBWorker dbWorker = new DBWorker();
        ObservableList<String> list = dbWorker.getAllTables();
        ObservableList<DatabaseObjects> databaseObjects = FXCollections.observableArrayList();
        for (String text: list) {
            databaseObjects.add(getByCode(text));
        }
        return databaseObjects;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
