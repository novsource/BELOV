package sample.View;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import sample.Controller.Controller;
import sample.DB.DBWorker;
import sample.Model.Bill;
import sample.Model.Client;

import java.sql.SQLException;
import java.util.List;

public class VisitsWindow {

    private DBWorker dbWorker = new DBWorker();
    private Client client;
    private Controller controller;
    private GridPane view;
    private Scene secondaryScene;
    private TableView tableView;

    public VisitsWindow(Controller controller, Client client) throws SQLException {
        this.controller = controller;
        this.client = client;
        this.tableView = createTableVisits();
        createGridPane();
    }

    public Stage VisitsWindow() throws SQLException {
        Stage visitsWindow = new Stage();

        this.secondaryScene = new Scene(this.view);
        visitsWindow.setScene(secondaryScene);

        return visitsWindow;
    }

    private GridPane createGridPane() throws SQLException {
        view = new GridPane();

        view.setHgap(5);
        view.setVgap(10);

        ColumnConstraints column = new ColumnConstraints();
        view.getColumnConstraints().add(column);
        column.setHgrow(Priority.ALWAYS);

        view.getRowConstraints().add(new RowConstraints());
        view.getRowConstraints().add(new RowConstraints());

        RowConstraints rowConstraintsTable = new RowConstraints();
        view.getRowConstraints().add(rowConstraintsTable);
        rowConstraintsTable.setVgrow(Priority.ALWAYS);

        Label moneyLostLabel = new Label("Общая потраченная сумма за 2 месяца: ");
        Label moneyLost = new Label(moneyLostClientTwoMonth() + " рублей");

        view.add(tableView, 0, 1);
        view.add(moneyLostLabel, 0, 2);
        view.add(moneyLost,0,3);

        return view;
    }

    private TableView createTableVisits() throws SQLException {
        TableView tableView = new TableView();

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Client, String> nameColumn = new TableColumn<>("ФИО");
        TableColumn<Bill, String> dateColumn = new TableColumn("Дата посещения");
        TableColumn<Bill, Integer> priceColumn = new TableColumn<>("Сумма чека");

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("client"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        tableView.getColumns().addAll(nameColumn, dateColumn, priceColumn);

        tableView.setItems(FXCollections.observableArrayList(dbWorker.getClientVisitsTwoMonths(client)));

        return tableView;
    }

    private String moneyLostClientTwoMonth() throws SQLException {
        int moneyLost = 0;
        List<Bill> list = dbWorker.getClientVisitsTwoMonths(client);
        for (Bill bill : list) {
            moneyLost += bill.getPrice();
        }
        return String.valueOf(moneyLost);
    }
}
