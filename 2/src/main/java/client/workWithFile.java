package client;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class workWithFile {
    public static final workWithFile INSTANCE = new workWithFile();
    private StringBuilder sb;
    private BufferedWriter bw;
    private File file;
    private String login;
    private RandomAccessFile raf;

    public void setLogin(String login) {
        this.login = login;
    }

    private workWithFile(){
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
        try {
            bw = new BufferedWriter(new FileWriter(file, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String line){
        sb = new StringBuilder();
        int count = 0;
        if (!line.startsWith("/")) {
            try {
                bw.write(String.valueOf(sb.append(line).append(System.getProperty("line.separator"))));
                count ++;
                if (count == 10){
                    bw.flush();
                    count = 0;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List readFile(){
        List<String> line = new ArrayList<>();
        char c;
        sb = new StringBuilder();
        int lines = 0;

        try {
            raf = new RandomAccessFile(file, "r");
            for(long point = file.length() - 2; point > 0; point--){
                raf.seek(point);
                c = (char) raf.read();
                if (c == '\n'){
                    line.add(new String(raf.readLine().getBytes("ISO-8859-1"), "UTF-8"));
                    lines ++;
                    if (lines == 6){
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(line);
        return line;
    }

    public void closeFile(){
        if(bw != null) {
            try {
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
