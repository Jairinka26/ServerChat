import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class Server {
    private ServerSocket serverSocket;
    private Logging logging;
//    private ArrayList<ClientConnection> clientsList = new ArrayList();
    private CopyOnWriteArraySet<ClientConnection> syncClientsList = new CopyOnWriteArraySet();
    private String outMessage;


    Server() {
        try {
            serverSocket = new ServerSocket(Variables.PORT);
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("Server Socket hasn't been created");
            System.exit(0);
        }
        Listener listener = new Listener();
        ConnectionListener connectionListener = new ConnectionListener();
        listener.start();
        connectionListener.start();
        try {
            logging = new Logging();
            logging.writeToLog("Сервер запущен");
        }
        catch (IOException e){
            System.out.println("Логер не запустился");
            e.printStackTrace();
        }

        System.out.println("Сервер запущен");

    }

    protected void sendingMessage (String message) throws IOException {
        for (ClientConnection clientConnection : syncClientsList) {
            try {
                clientConnection.getOut().writeUTF(message);
                clientConnection.getOut().flush();
                logging.writeToLog("Пользователю ".concat(clientConnection.getName()).concat(" отправлено сообщение: ").concat(message));
            }
            catch (SocketException e){
                removeClient(clientConnection);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
//        outMessage = null;
    }

    protected void removeClient (ClientConnection clientConnection) throws IOException {
        clientConnection.getSocket().close();
        syncClientsList.remove(clientConnection);
        outMessage = clientConnection.getName().concat(" вышел из чата");
        logging.writeToLog(outMessage);
        sendingMessage(outMessage);
    }

    class Listener extends Thread{
        private ClientConnection newClientConnection;
        @Override
        public void run() {
            while (true) {
                try {
                    newClientConnection = new ClientConnection(serverSocket.accept());
                    if (newClientConnection.isCreated()){
                        syncClientsList.add(newClientConnection);
                        outMessage = newClientConnection.getName().concat(" вошел в чат");
                        sendingMessage(outMessage);
                        logging.writeToLog("Подключился новый пользователь: ".concat(newClientConnection.getName()));
//                        System.out.println("Подключился новый пользователь: ".concat(newClientConnection.name));
                    }
                    else {
                        newClientConnection = null;
                        System.out.println("Неудачная попытка подключения");
                    }

                }

                catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ConnectionListener extends Thread{
        String inMessage;
        String message;

        private String prefix;
        @Override
        public synchronized void run() {
            while(true){
                for (ClientConnection clientConnection : syncClientsList)
                {
                    try {
                        if (clientConnection.getIn().available() > 0){
                            inMessage = clientConnection.getIn().readUTF();
                            System.out.println("Получено сообшение: ".concat(inMessage));
                            prefix = inMessage.split(Variables.DELIMITER)[0];
                            switch (prefix){
                                case Variables.EXIT_ACTION:
                                    removeClient(clientConnection);
//                                    outMessage = new Message().buildMessage(clientConnection.name," выщел из чата");
                                    break;
                                case Variables.SEND_ACTION :
                                    message =inMessage.split(Variables.DELIMITER)[1];
                                    outMessage = new Message().buildMessage(clientConnection.getName(),message);
                                    sendingMessage(outMessage);
                                    break;
                            }
//                            logging.writeToLog(outMessage);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}





