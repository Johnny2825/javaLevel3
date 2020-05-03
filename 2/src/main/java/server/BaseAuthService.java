package server;

import java.sql.*;

public class BaseAuthService implements AuthService {
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;

    @Override
    public void start() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Clients"
                    , "postgres", "3164TfCx");
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public String getNickByLoginPass(String login, String pass) {
        try {
            //ResultSet resultSet = statement.executeQuery("SELECT * FROM \"Users\"");
            preparedStatement = connection.prepareStatement("select \"Login\", \"Password\"," +
                    "\"Nickname\" from \"Users\" where \"Login\" = ? and \"Password\" = ?");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, pass);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean hasNext = resultSet.next();
            if (!hasNext) {
                System.out.println("RS is empty");
                return null;
            }
            System.out.println(resultSet.getString(1) + " | " +
                                resultSet.getString(2) + " | " +
                                resultSet.getString(3)
            );
            return resultSet.getString(3);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateNick(String nick, String newNick){
        boolean status = false;
        try {
            preparedStatement = connection.prepareStatement("UPDATE \"Users\" SET \"Nickname\" = ? WHERE \"Nickname\" = ?");
            preparedStatement.setString(1, newNick);
            preparedStatement.setString(2, nick);
            preparedStatement.executeUpdate();
            status = true;
            System.out.println("Ник изменен в БД");
        } catch (SQLException throwables) {
            System.out.println("Не удалось изменить ник в БД");
            throwables.printStackTrace();
        }

        return status;
    }

//    public void selectAllFromDB() throws SQLException {
//        ResultSet resultSet = statement.executeQuery("SELECT * FROM \"Users\"");
//        boolean hasNext = resultSet.next();
//        if (!hasNext) {
//            System.out.println("RS is empty");
//            return;
//        }
//        do {
//            System.out.println(
//                    resultSet.getInt(1) + " | " +
//                            resultSet.getString(2) + " | " +
//                            resultSet.getString(3) + " | " +
//                            resultSet.getString(4)
//            );
//        } while (resultSet.next());
//    }

    @Override
    public void stop() {
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Сервис аутентификации остановлен");
    }
}
