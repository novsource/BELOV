package sample.DB;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBWorker {
    public static final String PATH_TO_DB_FILE = "belov.db";
    public static final String URl = "jdbc:sqlite:" + PATH_TO_DB_FILE;
    public static Connection connection;

    public static void initDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(URl);
            if (connection != null) {
                System.out.println("Подключение к БД прошло успешно!");
                DatabaseMetaData meta = connection.getMetaData();
                System.out.println("Драйвер: " + meta.getDriverName());
            }
        }
        catch (SQLException | ClassNotFoundException error) {
            System.out.println("Ошибка подключения к базе данных: " + error);
        }
    }

    public Connection getConnection() {
        try {
            connection = DriverManager.getConnection(URl);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public ObservableList getAllTables() throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet resultSet = databaseMetaData.getTables(null, null,null, null);
        ObservableList<String> tablesList = FXCollections.observableArrayList();
        while (resultSet.next()) {
           tablesList.add(resultSet.getString(3));
        }
        resultSet.close();
        return tablesList;
    }

    public void addClient(Client client) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Client ('client_FIO', 'client_phone_number')" + "VALUES(?, ?)");
        preparedStatement.setString(1, client.getName());
        preparedStatement.setString(2, client.getPhoneNumber());
        preparedStatement.execute();
        preparedStatement.close();
    }

    public void editClient(Client client) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Client SET client_FIO = ?, client_phone_number = ? \n" +
                " WHERE client_id = " + client.getId());
        preparedStatement.setString(1, client.getName());
        preparedStatement.setString(2, client.getPhoneNumber());
        preparedStatement.execute();
        preparedStatement.close();
    }

    public void deleteClient(Client client) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM Client WHERE client_id =" + client.getId());
        System.out.println("Запись удалена");
        statement.close();
    }

    public static int getClientId (String name) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT client_id FROM Client WHERE client_FIO ='"+name+"'");
        int clientId = resultSet.getInt(1);
        resultSet.close();
        statement.close();
        return clientId;
    }

    public String getClientFIO (int id) throws  SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT client_FIO FROM Client WHERE client_id="+id);
        String client_FIO = resultSet.getString(1);
        resultSet.close();
        statement.close();
        return client_FIO;
    }

    public Discount getDiscountById(int id) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT discount_id, discount_pers, criteria_id " +
                "FROM Discount " +
                "WHERE discount_id = "+id);
        Discount discount = new Discount(resultSet.getInt(1), resultSet.getInt(2), resultSet.getInt(3));
        resultSet.close();
        statement.close();
        return discount;
    }

    public static void addDiscount(Discount discount) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Discount ('discount_pers', 'criteria_id')"
                + "VALUES(?, ?)");
        preparedStatement.setInt(1, discount.getValue());
        preparedStatement.setInt(2, discount.getCriteria());

        preparedStatement.execute();
        preparedStatement.close();
    }

    public static void editDiscount(Discount discount) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Discount SET discount_pers = ?, criteria_id = ? " +
                "WHERE discount_id=" + discount.getId());

        preparedStatement.setObject(1, discount.getValue());
        preparedStatement.setObject(2, discount.getCriteria());

        preparedStatement.execute();
        preparedStatement.close();
    }

    public void deleteDiscount(Discount discount) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM Discount WHERE discount_id =" + discount.getId());
        System.out.println("deleted!");
        statement.close();
    }

    public static List<Client> getAllClients() throws SQLException {
        Statement statement = connection.createStatement();
        ArrayList<Client> clientList = new ArrayList<Client>();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM Client");
        while (resultSet.next()) {
            clientList.add(new Client(resultSet.getInt("client_id"), resultSet.getString("client_FIO"),
                    resultSet.getString("client_phone_number")));
        }
        resultSet.close();
        statement.close();
        return clientList;
    }

    public static List<Discount> getAllDiscount() throws SQLException {
        Statement statement = connection.createStatement();
        ArrayList<Discount> discounts = new ArrayList<Discount>();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM Discount");
        while (resultSet.next()) {
            discounts.add(new Discount(resultSet.getInt("discount_id"), resultSet.getInt("discount_pers"), resultSet.getInt("criteria_id")));
        }
        resultSet.close();
        statement.close();
        return discounts;
    }

    public static List<Integer> getCriteriaDiscount() throws SQLException {
        Statement statement = connection.createStatement();
        ArrayList<Integer> criteriaList = new ArrayList<>();

        ResultSet resultSet = statement.executeQuery("SELECT criteria_id FROM Criteria");
        while (resultSet.next()) {
            criteriaList.add(resultSet.getInt("criteria_id"));
        }
        resultSet.close();
        statement.close();
        return criteriaList;
    }

    public List<Client> getDiscountsFromClient() throws SQLException {
        Statement statement = connection.createStatement();
        ArrayList<Client> clients = new ArrayList<>();

        ResultSet resultSet = statement.executeQuery("SELECT client_id, \n" +
                "CASE \n" +
                "\tWHEN \n" +
                "\t\t(SELECT discount_id FROM Discount \n" +
                "\t\t\tWHERE (criteria_id = (SELECT criteria_id \n" +
                "\t\t\t\t\t\t\t\t\tFROM Criteria \n" +
                "\t\t\t\t\t\t\t\t\tWHERE(bill_money < criteria_exists)))) \n" +
                "\t\tTHEN (SELECT (discount_id-1) FROM Discount \n" +
                "\t\t\t\tWHERE (criteria_id = (SELECT criteria_id \n" +
                "\t\t\t\t\t\t\t\t\t\tFROM Criteria \n" +
                "\t\t\t\t\t\t\t\t\t\tWHERE(bill_money < criteria_exists)))) \t\t\t\t\n" +
                "\tELSE \n" +
                "\t\t(SELECT discount_id \n" +
                "\t\tFROM Discount \n" +
                "\t\tWHERE criteria_id =(SELECT criteria_id \n" +
                "\t\t\t\t\t\t\tFROM Criteria \n" +
                "\t\t\t\t\t\t\tWHERE criteria_exists = (SELECT MAX(criteria_exists) FROM Criteria)))\n" +
                "\n" +
                "END discount_id\n" +
                "FROM Client_Bill");

        while(resultSet.next()) {
            if (resultSet.getInt(2) != 0) {
                Client client = new Client(resultSet.getInt(1), getClientFIO(resultSet.getInt(1)), getDiscountById(resultSet.getInt(2)));
                clients.add(client);
            }
        }
        resultSet.close();
        statement.close();
        return clients;
    }

    public List<Bill> getClientVisitsTwoMonths(Client client) throws SQLException{
        Statement statement = connection.createStatement();

        ArrayList bills = new ArrayList<>();

        ResultSet resultSet = statement.executeQuery("SELECT bill_payment, bill_date\n" +
                "FROM Bill\n" +
                "WHERE strftime('%s',bill_date) BETWEEN strftime('%s','now','-2 month') AND strftime('%s','now') \n" +
                "AND client_id ="+client.getId());

        while(resultSet.next()) {
            Bill bill = new Bill(client, resultSet.getString(2), resultSet.getInt(1));
            bills.add(bill);
        }

        statement.close();
        resultSet.close();

        return bills;
    }

    public void setDiscountToClient(Client client, Discount discount) throws SQLException{
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Client SET client_discount_id="+discount.getId()+" WHERE client_id="+client.getId());
        preparedStatement.execute();
        preparedStatement.close();
    }
}
