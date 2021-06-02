package sample.View;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import sample.Controller.Controller;
import sample.DB.DBWorker;
import sample.Model.*;

import java.sql.SQLException;

public class AddWindow {

    private Controller controller = new Controller();

    private GridPane gridPane;
    private Scene secondaryScene;
    private ComboBox tableChoiceCB = new ComboBox(FXCollections.observableArrayList(DatabaseObjects.values()));;

    public AddWindow(Controller controller) {
        this.controller = controller;
    }

    //-------------------------------- ФОРМА ДОБАВЛЕНИЯ ЗАПИСИ --------------------------------

    public Stage addWindowForm() throws SQLException {
        this.gridPane = defaultGridPane();
        Stage newWindow = new Stage();

        TextField nameTextField = new TextField();
        TextField numberTextField = new TextField();

        // Добавление скидки
        TextField discountPersTextField = new TextField();

       /* ObservableList<Author> authors = FXCollections.observableArrayList(DBWorker.getAllAuthors());
        ComboBox<Author> authorComboBox = new ComboBox<>(authors);*/

        Button addButton = new Button("Добавить");

        this.tableChoiceCB.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            if (newValue.equals(DatabaseObjects.CLIENTS)) {

                clientForm();

                this.gridPane.add(nameTextField, 1, 1);
                this.gridPane.add(numberTextField, 1, 2);

                this.gridPane.add(addButton, 0, 4);
                this.secondaryScene.getWindow().setHeight(400);
                this.secondaryScene.getWindow().setWidth(600);

            }
            else {
                discountForm();
                this.secondaryScene.getWindow().setWidth(400);
                this.secondaryScene.getWindow().setHeight(250);
                this.gridPane.add(discountPersTextField, 1,1);
                this.gridPane.add(addButton, 0, 4);
            }

            this.secondaryScene = new Scene(this.gridPane);
            newWindow.setScene(this.secondaryScene);

        }));

        //String.valueOf(authorComboBox.getSelectionModel().getSelectedItem().getName())

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    if (tableChoiceCB.getValue().equals(DatabaseObjects.CLIENTS)) {
                        controller.addData(new Client(0, nameTextField.getText(), numberTextField.getText()),
                                newWindow);
                    }
                    else if(tableChoiceCB.getValue().equals(DatabaseObjects.DISCOUNTS)) {
                        controller.addData(new Discount(0, Integer.parseInt(discountPersTextField.getText())), newWindow);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        this.secondaryScene = new Scene(this.gridPane);
        newWindow.setScene(this.secondaryScene);

        return newWindow;
    }

    public Stage editLibraryItemsWindowForm(Object object, int index) throws SQLException {
        this.gridPane = defaultGridPane();
        Stage newWindow = new Stage();

        TextField nameTextField = new TextField();
        TextField numberTextField = new TextField();

        // Добавление скидки
        TextField discountPersTextField = new TextField();

        /*ObservableList<Author> authors = FXCollections.observableArrayList(DBWorker.getAllAuthors());
        ComboBox<Author> authorComboBox = new ComboBox<>(authors);*/

        Button editButton = new Button("Изменить");

        /* Label для TextField (класс Library Item) */

        if (object instanceof Client) {

            Client client = (Client) object;

            clientForm();

            nameTextField.setText(client.getName());
            numberTextField.setText(client.getPhoneNumber());
            //authorComboBox.getSelectionModel().select(controller.getAuthorOfSelectedLibraryItemCB(item));

            this.gridPane.add(nameTextField, 1, 1);
            this.gridPane.add(numberTextField , 1, 2);

            this.gridPane.add(editButton, 0, 4);

        }
        else {
            discountForm();

            Discount discount = (Discount) object;

            this.tableChoiceCB.setDisable(true);

            discountPersTextField.setText(String.valueOf(discount.getValue()));
            this.gridPane.add(discountPersTextField, 1,1);
            this.gridPane.add(editButton, 0, 2);
        }

        editButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    if (tableChoiceCB.getValue().equals(DatabaseObjects.CLIENTS)) {
                        controller.editData(new Client(0, nameTextField.getText(), numberTextField.getText()), newWindow, index);
                    }
                    else if(tableChoiceCB.getValue().equals(DatabaseObjects.DISCOUNTS)) {
                        controller.editData(new Discount(0, Integer.parseInt(discountPersTextField.getText())), newWindow, index);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        this.secondaryScene = new Scene(this.gridPane);
        newWindow.setScene(secondaryScene);

        return newWindow;
    }

    private void clientForm() {
        this.gridPane = defaultGridPane();
        this.tableChoiceCB.getSelectionModel().select(DatabaseObjects.CLIENTS);

        Label nameLabel = new Label("Введите ФИО: ");
        Label numberLabel = new Label("Введите номер телефона: ");

        this.gridPane.add(nameLabel, 0, 1);
        this.gridPane.add(numberLabel, 0, 2);

    }



    private void discountForm() {
        this.gridPane = defaultGridPane();
        this.tableChoiceCB.getSelectionModel().select(DatabaseObjects.DISCOUNTS);

        Label discountPers = new Label("Процент скидки: ");

        this.gridPane.add(discountPers, 0 , 1);
    }

    private GridPane defaultGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(30);
        grid.setPadding(new Insets(30));

        /* Добавляем 2 новых столбца в планировщик Grid и растягиваем их */

        ColumnConstraints column = new ColumnConstraints();
        grid.getColumnConstraints().add(column);
        column.setHgrow(Priority.ALWAYS);

        ColumnConstraints columnForTextField = new ColumnConstraints();
        grid.getColumnConstraints().add(columnForTextField);
        columnForTextField.setHgrow(Priority.ALWAYS);

        Label tableChoiceLabel = new Label("Выбранный объект: ");

        grid.add(tableChoiceLabel, 0, 0);
        grid.add(this.tableChoiceCB, 1, 0);

        this.tableChoiceCB.setDisable(false);

        return grid;
    }
}
