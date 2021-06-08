package sample.View;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Controller.Controller;
import sample.DB.DBWorker;
import sample.Model.*;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class GUI {

    private GridPane view; //главная панель
    private Stage primaryStage = new Stage();

    private Controller controller = new Controller();
    private DBWorker dbWorker = new DBWorker(); // работа с БД

    private TableView mainTable; // главная таблица
    private ComboBox choiceTable = new ComboBox(); //комбобокс для выбора таблиц, которые загружаются из БД

    private TextField searchField = new TextField(); //поле фильтрации
    private VBox menu = new VBox(); //главное меню

    public GUI() throws SQLException {
        DBWorker.initDatabase(); //инициализируем БД
        this.mainTable = new TableView<>(); //инициализируем главную таблицу
        createMainGridPane(); //создаем главную панель
    }

    public Parent asParent() {
        return view;
    }

    private void createMainGridPane() throws SQLException {
        view = new GridPane();

        view.setHgap(5);
        view.setVgap(10);

        /* Корректное определение местоположения мышки для отображения контекстного меню */

        view.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                ContextMenu contextMenu = createContextMenu();
                contextMenu.show(mainTable.getScene().getWindow(), MouseInfo.getPointerInfo().getLocation().getX(), MouseInfo.getPointerInfo().getLocation().getY());
            }
        });

        ColumnConstraints column = new ColumnConstraints();
        view.getColumnConstraints().add(column);
        column.setHgrow(Priority.ALWAYS);

        view.getRowConstraints().add(new RowConstraints());
        view.getRowConstraints().add(new RowConstraints());

        RowConstraints rowConstraintsTable = new RowConstraints();
        view.getRowConstraints().add(rowConstraintsTable);
        rowConstraintsTable.setVgrow(Priority.ALWAYS);

        searchField.setPromptText("\uD83D\uDD0D Введите для фильтрации");
        searchField.setFocusTraversable(false);

        this.menu = createNewMenu(); // создаем меню

        HBox hBox = new HBox(menu, searchField);
        view.add(hBox, 0, 0);
        hBox.setHgrow(menu, Priority.ALWAYS);

        choiceTable.setItems(FXCollections.observableArrayList(DatabaseObjects.values()));
        choiceTable.getItems().add("Клиенты со скидками");


        choiceTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (DatabaseObjects.CLIENTS.equals(newValue)) {
                try {
                    searchField.setText("");
                    editClientTableView();
                    this.controller.tableClientFilter(mainTable.getItems(), searchField, mainTable);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            else if (DatabaseObjects.DISCOUNTS.equals(newValue)) {
                try {
                    searchField.setText("");
                    editDiscountTableView();
                    this.controller.tableDiscountFilter(mainTable.getItems(), searchField, mainTable);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else if (newValue == "Клиенты со скидками") {
                try {
                    searchField.setText("");
                    createDiscountClientTableView();
                    this.controller.tableClientFilter(mainTable.getItems(), searchField, mainTable);
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        choiceTable.getSelectionModel().select(1);

        HBox hBoxChoice = new HBox(new javafx.scene.control.Label("Выберите таблицу: "), choiceTable);
        HBox hBoxSearch = new HBox(new Label("Поиск по таблице "), searchField);
        HBox hBoxMain = new HBox(hBoxChoice, hBoxSearch);
        hBoxChoice.setAlignment(Pos.CENTER_LEFT);
        hBoxSearch.setAlignment(Pos.CENTER_LEFT);
        hBoxChoice.setSpacing(20);
        hBoxSearch.setSpacing(20);
        hBoxMain.setSpacing(350);

        view.add(hBoxMain, 0, 1);
        view.add(mainTable, 0, 2);
    }

    private VBox createNewMenu() {
        Menu file = new Menu("Файл");
        Menu edit = new Menu("Редактирование");

        // создаем пункты меню

        MenuItem addMenu = new MenuItem("Добавить запись...");

        MenuItem openMenu = new MenuItem("Открыть...");

        MenuItem exit = new MenuItem("Выйти");

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

        MenuItem editMenu = new MenuItem("Изменить запись");

        MenuItem deleteMenuItem = new MenuItem("Удалить запись");

        //горячие клавиши

        exit.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));

        //События

        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.exit(0);
            }
        });

        editMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AddWindow addWindow = new AddWindow(controller);
                try {
                    if (mainTable.getSelectionModel().getSelectedItem() != null) {
                        Stage newWindow = addWindow.editLibraryItemsWindowForm(mainTable.getSelectionModel().getSelectedItem(), mainTable.getSelectionModel().getSelectedIndex());
                        newWindow.setTitle("Редактирование записи");

                        newWindow.initModality(Modality.WINDOW_MODAL);

                        newWindow.initOwner(primaryStage);

                        newWindow.setX(primaryStage.getX());
                        newWindow.setY(primaryStage.getY());

                        newWindow.show();
                    }
                    else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка!");
                        alert.setHeaderText(null);
                        alert.setContentText("Вы не выбрали объект для редактирования из таблицы!");
                        alert.showAndWait();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    deleteFromTableAlert();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        addMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AddWindow addWindow = new AddWindow(controller);
                try {
                    Stage newWindow = addWindow.addWindowForm();
                    newWindow.setTitle("Добавление записи");

                    newWindow.initModality(Modality.WINDOW_MODAL);

                    newWindow.initOwner(primaryStage);

                    newWindow.setX(primaryStage.getX());
                    newWindow.setY(primaryStage.getY());

                    newWindow.show();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        // добавить пункты меню в меню
        file.getItems().addAll(openMenu, separatorMenuItem, exit);
        edit.getItems().addAll(addMenu, editMenu, deleteMenuItem);

        // создаем меню

        MenuBar mb = new MenuBar();

        // добавить меню в меню

        mb.getMenus().addAll(file, edit);

        // создаем VBox

        VBox vb = new VBox(mb);

        return vb;
    }

    //-------------------------------- КОНТЕКСТНОЕ МЕНЮ --------------------------------

    private ContextMenu createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem addData = new MenuItem("Добавить запись");
        MenuItem editData = new MenuItem("Редактировать данные");
        MenuItem deleteData = new MenuItem("Удалить запись");

        contextMenu.getItems().addAll(addData, editData, deleteData);

        addData.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AddWindow addWindow = new AddWindow(controller);
                try {
                    Stage newWindow = addWindow.addWindowForm();
                    newWindow.setTitle("Добавление записи");

                    newWindow.initModality(Modality.WINDOW_MODAL);

                    newWindow.initOwner(primaryStage);

                    newWindow.setX(primaryStage.getX());
                    newWindow.setY(primaryStage.getY());

                    newWindow.show();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        editData.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AddWindow addWindow = new AddWindow(controller);
                try {
                    if (mainTable.getSelectionModel().getSelectedItem() != null) {
                        Stage newWindow = addWindow.editLibraryItemsWindowForm(mainTable.getSelectionModel().getSelectedItem(), mainTable.getSelectionModel().getSelectedIndex());
                        newWindow.setTitle("Редактирование записи");

                        newWindow.initModality(Modality.WINDOW_MODAL);

                        newWindow.initOwner(primaryStage);

                        newWindow.setX(primaryStage.getX());
                        newWindow.setY(primaryStage.getY());

                        newWindow.show();
                    }
                    else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка!");
                        alert.setHeaderText(null);
                        alert.setContentText("Вы не выбрали объект для редактирования из таблицы!");
                        alert.showAndWait();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        deleteData.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    deleteFromTableAlert();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        return contextMenu;
    }

    //-------------------------------- ТАБЛИЦА "КЛИЕНТЫ" (clients)  --------------------------------

    private void editClientTableView() throws SQLException {

       /* mainTable.setRowFactory(tv -> {
            TableRow<Client> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2 && (!row.isEmpty()) ) {
                    Client client = row.getItem();
                    VisitsWindow visitsWindow = new VisitsWindow(controller, client);
                    try {
                        Stage window = visitsWindow.VisitsWindow();

                        window.initModality(Modality.WINDOW_MODAL);

                        window.initOwner(primaryStage);

                        window.setX(primaryStage.getX());
                        window.setY(primaryStage.getY());

                        window.show();
                    }
                    catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                }
            });
            return row;
        });*/

        mainTable.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    if (mainTable.getSelectionModel().getSelectedItem() instanceof Client) {
                        Client client = (Client) mainTable.getSelectionModel().getSelectedItem();
                        VisitsWindow visitsWindow = null;
                        try {
                            visitsWindow = new VisitsWindow(controller, client);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        try {
                            Stage window = visitsWindow.VisitsWindow();

                            window.initModality(Modality.WINDOW_MODAL);

                            window.initOwner(primaryStage);

                            window.setX(primaryStage.getX());
                            window.setY(primaryStage.getY());

                            window.show();
                        }
                        catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                }
            }
        });

        mainTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Client, Integer> idColumn = new TableColumn<>("id");
        TableColumn<Client, String> FIOColumn = new TableColumn<>("ФИО");
        TableColumn<Client, String> phoneNumberColumn = new TableColumn<>("Номер телефона");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        FIOColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        mainTable.getColumns().setAll(idColumn, FIOColumn, phoneNumberColumn);

        controller.buildClientTableData(this.mainTable);


    }

    //-------------------------------- ТАБЛИЦА "СКИДКИ" (Discount) --------------------------------

    private void editDiscountTableView() throws SQLException {
        TableView<Discount> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Discount, Integer> idColumn = new TableColumn<>("id");
        TableColumn<Discount, Integer> releaseNumberColumn = new TableColumn<>("Процент скидки");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        releaseNumberColumn.setCellValueFactory(new PropertyValueFactory<>("value"));


        mainTable.getColumns().setAll(idColumn, releaseNumberColumn);

        mainTable.setItems(controller.buildDiscountTableData());
    }

    private void deleteFromTableAlert() throws SQLException {
        if (mainTable.getSelectionModel().getSelectedItem() != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Информация");
            alert.setHeaderText("Удалить строку номер " + (mainTable.getSelectionModel().getSelectedIndex() + 1) + ": " +
                    mainTable.getSelectionModel().getSelectedItem() + "из таблицы ?");
            Optional<ButtonType> option = alert.showAndWait();
            option.get();
            if (option.get() == ButtonType.OK) {
                if (choiceTable.getValue().equals(DatabaseObjects.CLIENTS)) {
                    controller.deleteData(mainTable.getSelectionModel().getSelectedItem(), mainTable , controller.getClientList());
                }
                else {
                    controller.deleteData(mainTable.getSelectionModel().getSelectedItem(), mainTable, controller.getDiscountsItems());
                }
            }
        } else {
            Alert aler = new Alert(Alert.AlertType.WARNING);
            aler.setTitle("Информация");
            aler.setHeaderText(null);
            aler.setContentText("Не выбрано ни одной строчки из таблицы !");
            aler.showAndWait();
        }
    }

    private void createDiscountClientTableView() throws SQLException {
        TableView table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Client, Integer> idColumn = new TableColumn<>("id");
        TableColumn<Client, Integer> releaseNumberColumn = new TableColumn<>("Процент скидки");
        TableColumn<Client, String> clientFIO = new TableColumn<>("ФИО клиента");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        releaseNumberColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));
        clientFIO.setCellValueFactory(new PropertyValueFactory<>("name"));

        mainTable.getColumns().setAll(idColumn, clientFIO, releaseNumberColumn);

        mainTable.setItems(controller.buildDiscountClientData());
    }

}
