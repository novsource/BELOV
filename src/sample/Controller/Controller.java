package sample.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.DB.DBWorker;
import sample.Model.*;

import java.sql.SQLException;
import java.util.List;

public class Controller {
    private DBWorker dbWorker = new DBWorker();
    private SortedList sortedList = new SortedList<>(FXCollections.observableArrayList());

    private ObservableList<Discount> discounts;
    private ObservableList<Client> clientsList;

    //--------------------------------ФИЛЬТРАЦИЯ ТАБЛИЦЫ--------------------------------

    public void tableDiscountFilter(ObservableList<Discount> allData,
                                   TextField textProperty, TableView table) {
        FilteredList<Discount> filteredData = new FilteredList<>(allData, p -> true);
        textProperty.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(discount -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if(String.valueOf(discount.getValue()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
        sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);
    }

    public void tableClientFilter(ObservableList<Client> allData, TextField textProperty, TableView table) {
        FilteredList<Client> filteredData = new FilteredList<>(allData, p -> true);
        textProperty.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(client -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (client.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (client.getPhoneNumber().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
        sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);
    }

    public ObservableList<Discount> buildDiscountTableData() throws SQLException {
        //DBWorker.addArticle(new Article(1, "Мурзилка","1","Крутая","20.02"));
        this.discounts = FXCollections.observableArrayList(DBWorker.getAllDiscount());
        return this.discounts;
    }

    public ObservableList<Client> buildClientTableData(TableView tableView) throws SQLException {
        this.clientsList = FXCollections.observableArrayList(DBWorker.getAllClients());
        tableView.setItems(this.clientsList);
        /*if (this.authorsList.isEmpty()) {
            try {
                List<Author> authorList = DBWorker.getAllAuthors();
                for (Author author : authorList) {
                    this.authorsList.add(author);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }*/
        return this.clientsList;
    }

    public ObservableList<Client> buildDiscountClientData() throws SQLException {
        return FXCollections.observableArrayList(dbWorker.getDiscountsFromClient());
    }

    public void addData(Object addingObject, Stage windowForm) throws SQLException {
        if (addingObject instanceof Client) {
            Client client = (Client) addingObject;

            dbWorker.addClient(client);
            client.setId(DBWorker.getClientId(client.getName()));

            this.clientsList.add(client);

        }
        else if (addingObject instanceof Discount) {
            Discount discount = (Discount) addingObject;
            DBWorker.addDiscount(discount);

            //discount.setId(dbWorker.getMagazineId(discount));

            this.discounts.add(discount);

        }
        windowForm.close();
    }

    public void editData(Object editedObject, Stage windowForm, int index) throws SQLException {
        if (editedObject instanceof Client) {
            dbWorker.editClient((Client) editedObject);

            this.clientsList.set(index, (Client) editedObject);
        }
        else {
            DBWorker.editDiscount((Discount) editedObject);

            this.discounts.set(index, (Discount) editedObject);
        }
        windowForm.close();
    }

    public void deleteData(Object item, TableView tableView, ObservableList list) throws SQLException {

        /* Удаление из ObservableList */

        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            int sourceIndex = sortedList.getSourceIndexFor(list, selectedIndex);
            list.remove(sourceIndex);
        }

        /* Удаление из БД */

            if (item instanceof Client) {
                dbWorker.deleteClient((Client) item);
        }
            else {
                dbWorker.deleteDiscount((Discount) item);

            }
    }

    /*public int getCriteriaOfSelectedDiscount(Discount item) throws SQLException {
        ObservableList<Discount> list = FXCollections.observableArrayList(DBWorker.getAllDiscount());
        for (Discount auth : list) {
            if (auth.getId() == DBWorker.getDiscountId(item.getCriteria())) {
                return auth;
            }
        }
        return null;
    }*/

    public ObservableList<Client> getClientList() {
        return this.clientsList;
    }

    public ObservableList<Discount> getDiscountsItems() {
        return this.discounts;
    }


}
