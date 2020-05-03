package server;

import java.sql.SQLException;

public interface AuthService {
    void start();
    String getNickByLoginPass(String login, String pass);
    boolean updateNick(String login, String nick);
    void stop();
}
