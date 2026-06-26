package foi.andrijastimac.services;

import foi.andrijastimac.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserService {

    private final DatabaseService databaseService =
            new DatabaseService();

    public User findByEmailAndPassword(String email, String password) {

        try (
                Connection connection =
                        databaseService.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                "SELECT id, email, password, name FROM users WHERE email = ? AND password = ?"
                        )
        ) {

            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public User findByEmail(String email) {

        try (
                Connection connection =
                        databaseService.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                "SELECT id, email, password, name FROM users WHERE email = ?"
                        )
        ) {

            statement.setString(1, email);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean register(String email, String password, String name) {

        try (
                Connection connection =
                        databaseService.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                "INSERT INTO users (email, password, name) VALUES (?, ?, ?)"
                        )
        ) {

            statement.setString(1, email);
            statement.setString(2, password);
            statement.setString(3, name);
            statement.executeUpdate();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean emailExists(String email) {

        try (
                Connection connection =
                        databaseService.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                "SELECT id FROM users WHERE email = ?"
                        )
        ) {

            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private User mapRow(ResultSet rs) throws Exception {
        return new User(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("name")
        );
    }
}
