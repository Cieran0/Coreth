package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManagement {

    /*
     * TODO: This is really ugly and probably a lot slower than it should be
     * fix it.
     */
    
    private static List<FileDesc> fds = new ArrayList<FileDesc>() {
        @Override
        public FileDesc get(int index) {
            return super.get(index-1);
        };
    };

    public static void writeToFile(String text, int fdID) {
        if(fdID < 0 || fdID > fds.size()) {
            System.out.println(fdID);
            Parser.exitWithError("Tried writing to a file that has not been opened HERE", fdID);
        }

        FileDesc fd = fds.get(fdID);
        if(!fd.isOpen()) {
            Parser.exitWithError("Tried writing to a file that has not been opened", fdID);
        }

        try {
            FileWriter fw = new FileWriter(fd.getPath(),fd.shouldAppend());
            fw.write(text);
            fw.close();
          } catch (IOException e) {
            System.out.println("An error occurred writing to file " + fd.getPath());
            e.printStackTrace();
          }
    }

    public static String readFromFile(int fdID, int size) {
        if(fdID < 0 || fdID >= fds.size()) {
            Parser.exitWithError("Tried reading from a file that has not been opened", fdID);
        }

        FileDesc fd = fds.get(fdID);
        if(!fd.isOpen()) {
            Parser.exitWithError("Tried reading from a file that has not been opened", fdID);
        }

        char[] buff = new char[size];
        try {
            BufferedReader fr = new BufferedReader(new FileReader(fd.getPath()));
            fr.read(buff, 0, size);
            fr.close();
          } catch (IOException e) {
            System.out.println("An error occurred writing to file " + fd.getPath());
            e.printStackTrace();
          }
        return String.valueOf(buff);
    }

    public static int OpenFile(String path, boolean append) {
        fds.add(new FileDesc(path, append));
        return fds.size();
    }

    public static void Close(int fdID) {
        if(fdID < 0 || fdID >= fds.size()) {
            Parser.exitWithError("Tried closing a file that has not been opened", fdID);
        }

        FileDesc fd = fds.get(fdID);
        if(!fd.isOpen()) {
            Parser.exitWithError("Tried closing a file that has not been opened", fdID);
        }
    }

}
