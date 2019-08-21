package game;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.IOException;


public class FileManager {
    private String FILENAME; 
    private Object obj;
    private File file;

    public FileManager(String filename) {
        this.FILENAME = filename;
    }

    public boolean exists() {
        File file = new File(FILENAME);
        return file.exists();
    }

    public Object read() {
        try {
            ObjectInput oi = new ObjectInputStream(new FileInputStream(FILENAME));
            obj = oi.readObject();
            return obj;
        } catch(IOException e) {
            e.printStackTrace();
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void write(Object obj) {
        try {
            ObjectOutput oo = new ObjectOutputStream(new FileOutputStream(FILENAME));
            oo.writeObject(obj);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}