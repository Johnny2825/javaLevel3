package client;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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

    Network() {
      connect();
    }

    public void sendAuth(String login, String password) {
        try {
            if(socket.isClosed()) {
                connect();
            }
            if (!socket.isClosed() && out != null){
                listener();
                out.writeUTF("/auth " + login + " " + password);
            } else {
                callOnException.callback("Соединение с сервером не установлено");
            }
            HistoryLogger.INSTANCE.setLogin(login);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect(){
        try {
            socket = new Socket("localhost", 8190);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void listener() {
        Thread clientListenerThread = new Thread(() -> {
            try {
                String msg;
                while (true) {
                    msg = in.readUTF();
                    System.out.println(msg);
                    if (msg.startsWith("/authok ")) {
                        callOnAuthenticated.callback(msg.split("\\s")[1]);
                        HistoryLogger.INSTANCE.createFile();
                        break;
                    } else {
                        callOnException.callback(msg);
                    }
                }

                List<String> list = HistoryLogger.INSTANCE.readFile();
                System.out.println(list);
                if (!list.isEmpty()){
                    for(int i = list.size() - 1; i >= 0; i--){
                        callOnMsgReceived.callback(list.get(i));
                    }
                }

                while (true) {
                    msg = in.readUTF();
                    HistoryLogger.INSTANCE.write(msg);
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
        HistoryLogger.INSTANCE.writeAndCloseFile();
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
