import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Logging {
    private FileWriter fileWriter;
    private String logFileName = "server.log";
    private String action;
    private Date time;


    public Logging() throws IOException {
        fileWriter = new FileWriter(logFileName);
    }


    void writeToLog (String logMessage) throws IOException {
        time = new Date();
        fileWriter.write(time.toString().concat(" ").concat(logMessage));
        fileWriter.flush();
        fileWriter.append('\n');
    }
}
