import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientConnection {
    private Socket socket;

    private String name;

    private String inMessage;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean isCreated = false;


    public boolean isCreated() {
        return isCreated;
    }

    public Socket getSocket() {
        return socket;
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public String getName() {
        return name;
    }

    ClientConnection(Socket socket) throws IOException, InterruptedException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        setName();
    }

    private void setName() throws IOException, InterruptedException {
        Thread.sleep(2000);
        if (in.available() > 0 ) {
            inMessage = in.readUTF();
            if (inMessage.startsWith(Variables.LOGIN_ACTION)) {
                this.name = inMessage.split(Variables.DELIMITER)[1];
                isCreated = true;
            }
            else {
                isCreated = false;
                System.out.println("Неправильный формат имени");
                // TODO: close socket
            }
        }
        else {
            isCreated = false;
            System.out.println("Сервер не получил имя клиента");
            // TODO: close socket
        }
    }
}

