package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MyServer {
    private final int PORT = 8190;

    private Map<String, ClientHandler> clients;
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public MyServer() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            authService = new BaseAuthService();
            authService.start();
            clients = new HashMap<>();

            while (true) {
                System.out.println("Сервер ожидает подключения");
                Socket socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка в работе сервера");
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }
    }

    public synchronized boolean isNickBusy(String nick) {
        if (clients.containsKey(nick)) {
            return true;
        }
        return false;
    }

    public synchronized void unicastMsg(String fromName, String toName, String msg){
        clients.get(toName).sendMsg("Personally from " + formatMessage(fromName, msg));
    }

    public synchronized void broadcastMsg(String msg){
        for (ClientHandler o : clients.values()) {
            o.sendMsg(msg);
        }
    }

    public synchronized void broadcastMsg(String fromName, String msg) {
        for (String name : clients.keySet()) {
            clients.get(name).sendMsg(formatMessage(fromName, msg));
        }
    }

    public synchronized void broadcastClient(){
        StringBuilder builder = new StringBuilder("/clients ");
        for (String name : clients.keySet()) {
            builder.append(name).append(" ");
        }
        broadcastMsg(builder.toString());
    }

    private String formatMessage(String from, String msg) {
        return from + ": " + msg;
    }

    public synchronized void updateNick(String name, String newName, ClientHandler o) {
        if(getAuthService().updateNick(name, newName)){
            clients.put(newName, o);
            clients.remove(name);
            clients.get(newName).sendMsg("/newNick " + newName);
            broadcastMsg(formatMessage(name, "change nick"));
            broadcastClient();
        }
    }

    public synchronized void unsubscribe(String name) {
        clients.remove(name);
        broadcastClient();
    }

    public synchronized void subscribe(String name, ClientHandler o) {
        clients.put(name, o);
        broadcastClient();
    }
}

