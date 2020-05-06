package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class ClientHandler {
    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Timer timer;
    private TimerTask task;

    private String name;

    public ClientHandler(MyServer myServer, Socket socket) {  //конструктор обработчика клиента, на входе сервер и сокет
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "";

//            task = new TimerTask() {
//                public synchronized void run() {
//                    closeConnection();
//                }
//            };
//            timer = new Timer("Timer");
//            long delay = 5000L;
//            timer.schedule(task, delay);

            new Thread(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Проблемы при создании обработчика клиента");
        }
    }


    public void authentication() throws IOException {

        while (true) {
            String str = null;
            str = in.readUTF();
            if(str.startsWith("/end")){
                closeConnection();
            }
            if (str.startsWith("/auth")) {
                String[] parts = str.split("\\s");
                String nick = myServer.getAuthService().getNickByLoginPass(parts[1], parts[2]);
                if (nick != null) {
                    if (!myServer.isNickBusy(nick)) {
                        //timer.cancel();
                        sendMsg("/authok " + nick);
                        name = nick;
                        myServer.broadcastMsg(name + " зашел в чат");
                        myServer.subscribe(name, this);
                        return;
                    } else {
                        sendMsg("Учетная запись уже используется");
                    }
                } else {
                    sendMsg("Неверные логин/пароль");
                }
            }
        }
    }

    public void readMessages() throws IOException {
        while (true) {
            String strFromClient = in.readUTF();
            System.out.println("от " + name + ": " + strFromClient);
            if (strFromClient.startsWith("/")){
                if (strFromClient.equals("/end")) {
                    return;
                }
                if (strFromClient.startsWith("/change nick")){      //смена ника
                    String[] tokens = strFromClient.split("\\s");
                    myServer.updateNick(name, tokens[2], this);
                    name = tokens[2];
                }
                if (strFromClient.startsWith("/w")){        //личная отсылка
                    String[] tokens = strFromClient.split("\\s");
                    myServer.unicastMsg(name, tokens[1], strFromClient.substring(6 + tokens[1].length()));
                    sendMsg("Personally to " + tokens[1] + ":" + strFromClient.substring(5 + tokens[1].length()));
                }
            } else {
                myServer.broadcastMsg(name, strFromClient);
            }
        }
    }

    public void sendMsg(String msg) {
        try {
            System.out.println(msg);
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        myServer.unsubscribe(name);
        myServer.broadcastMsg(name + " вышел из чата");
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

