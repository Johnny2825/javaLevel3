package client;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Network implements Closeable {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private Callback callOnMsgReceived;
    private Callback callOnAuthenticated;
    private Callback callOnException;
    private Callback callOnCloseConnection;

    public void setCallOnMsgReceived(Callback callOnMsgReceived) {
        this.callOnMsgReceived = callOnMsgReceived;
    }

    public void setCallOnAuthenticated(Callback callOnAuthenticated) {
        this.callOnAuthenticated = callOnAuthenticated;
    }

    public void setCallOnException(Callback callOnException) {
        this.callOnException = callOnException;
    }

    public void setCallOnCloseConnection(Callback callOnCloseConnection) {
        this.callOnCloseConnection = callOnCloseConnection;
    }

    public void sendAuth(String login, String password) {
        try {
            if (socket == null || socket.isClosed()) {
                connect();
            }
            out.writeUTF("/auth " + login + " " + password);
            workWithFile.INSTANCE.setLogin(login);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        try {
            socket = new Socket("localhost", 8190);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread clientListenerThread = new Thread(() -> {
                try {
                    String msg;
                    while (true) {
                        msg = in.readUTF();
                        if (msg.startsWith("/authok ")) {
                            callOnAuthenticated.callback(msg.split("\\s")[1]);
                            workWithFile.INSTANCE.createFile();
                            break;
                        } else {
                            callOnException.callback(msg);
                        }
                    }

                    List<String> test;
                    test = workWithFile.INSTANCE.readFile();
                    if (!test.isEmpty()){
                        for(int i = test.size() - 1; i >= 0; i--){
                            callOnMsgReceived.callback(test.get(i));
                        }
                    }

                    while (true) {
                        msg = in.readUTF();
                        workWithFile.INSTANCE.write(msg);
                        if (msg.equals("/end")){
                            break;
                        }
                        System.out.println(msg);
                        callOnMsgReceived.callback(msg);
                    }

                } catch (IOException e) {
                    callOnException.callback("Соединение с сервером разорвано");
                } finally {
                    close();
                }
            });
            clientListenerThread.setDaemon(true);
            clientListenerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean sendMsg(String msg) {
        if (out == null) {
            callOnException.callback("Соединение с сервером не установлено");
        }
        try {
            out.writeUTF(msg);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void close() {
        workWithFile.INSTANCE.closeFile();
        callOnCloseConnection.callback();
        close(in, out, socket);
    }

    private void close(Closeable... objects) {
        for (Closeable o : objects) {
            try {
                o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
