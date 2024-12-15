package Drivers;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SDCardDriver {

    private File file;
    private boolean initialized = false ;
    private boolean corrupted = false ;
    private BufferedWriter writer;
    private BufferedReader reader;
    private Mode mode;

    public SDCardDriver(String filePath, Mode RorW) {
        this.file = new File(filePath);
        this.mode = RorW;
        try {
            openFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void openFile() throws IOException {
        if (mode == Mode.R) {
            reader = new BufferedReader(new FileReader(file));
            reader.mark(0);
        } else {
            writer = new BufferedWriter(new FileWriter(file));
        }
    }

    public void closeFile() throws IOException {
        if (mode == Mode.R) {
            if(reader != null){
                reader.close();
            }
        }else {
            if (writer != null){
                writer.close();
            }
        }
    }

    public String[] read() throws IOException {
        if(mode == Mode.W){
            throw new IOException("SDCardDriver is not in read mode. Trying to access Read mode.");
        }
        List<String> dataList = new ArrayList<>();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                dataList.add(line);
            }
        }catch (IOException e){
                e.printStackTrace();
                return new String[0];
            }
        return dataList.toArray(new String[0]);
    }

    public void write(String line) {
        if (mode == Mode.R) {
            throw new IllegalStateException("SDCardDriver is not in write mode. Trying to access Write Mode.");
        }
        try {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // checks for failure if file exist, or SDCard has been initialized, and is not corrupted
    public boolean checkForFailure(){
        return !initialized && !file.exists() && corrupted;
    }
}
