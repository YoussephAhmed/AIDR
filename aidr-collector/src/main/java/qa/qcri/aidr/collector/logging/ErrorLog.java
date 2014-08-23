package qa.qcri.aidr.collector.logging;

import qa.qcri.aidr.collector.utils.Config;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class ErrorLog {

    static Object writeLock = new Object();

    public static void Print(String sourceName, String text, Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        Print(sourceName, text + " | Exception:" + System.getProperty("line.separator") + sw.toString());
    }

    public static void Print(String sourceName, String text) {
        synchronized (writeLock) {
            if (Config.LOG_FILE_NAME.equals("console")) {
                System.out.println(sourceName + ": " + text);
            } else {
                try {
                    System.out.println(sourceName + ": " + text);
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(Config.LOG_FILE_NAME, true)));
                    out.println(getTimestamp() + sourceName + ": " + text);
                    out.close();
                } catch (IOException e) {
                    System.out.println("Cannot write to log file!");
                }
            }
        }
    }
    
    public String toStringException(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        
        StringBuilder retVal = new StringBuilder(new String("Exception:")).append(System.getProperty("line.separator")).append(sw);
        return retVal.toString();
    }
    
    static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static String getTimestamp() {
        Date date = new Date();
        return dateFormat.format(date);
    }
}
