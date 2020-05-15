package client;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryLogger {
    public static final HistoryLogger INSTANCE = new HistoryLogger();
    private StringBuilder sb;
    private File file;
    private String login;
    private int countLines;
    private final int HISTORY_LINES_LIMIT = 100;

    public void setLogin(String login) {
        this.login = login;
    }

    private HistoryLogger(){
    }

    public void createFile(){
        File directory = new File("C:\\Users\\Usme\\Desktop\\Учеба\\Java\\GeekBrains\\javaLevel3\\2\\src\\main\\java\\client\\historyChat");
        directory.mkdir();
        file = new File("C:\\Users\\Usme\\Desktop\\Учеба\\Java\\GeekBrains\\javaLevel3\\2\\src\\main\\java\\client\\historyChat\\history_" + this.login + ".txt");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sb = new StringBuilder();
    }

    public void write(String line){
        if (sb != null){
            if (!line.startsWith("/")) {
                sb.append(line).append(System.getProperty("line.separator"));
                countLines++;
                int countLineForWrite = 10;
                if (countLines == countLineForWrite){
                    writeAndCloseFile();
                    sb = new StringBuilder();
                    countLines = 0;
                }
            }
        }
    }

    public List readFile(){
        List<String> line = new ArrayList<>();
        char c;
        int lines = 0;
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            for(long point = file.length() - 2; point >= 0; point--){
                raf.seek(point);
                c = (char) raf.read();
                if ((c == '\n') || (point == 0)){
                    line.add(new String(raf.readLine().getBytes("ISO-8859-1"), "UTF-8"));
                    lines ++;
                    if (lines == HISTORY_LINES_LIMIT){
                        break;
                    }
                }
            }
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(line);
        return line;
    }

    public void writeAndCloseFile(){
        if (file != null && sb != null) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                bw.write(String.valueOf(sb));
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
