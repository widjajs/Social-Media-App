import java.io.*;
import java.net.Socket;

public class DataClient {
    private String host = "localhost";
    private int port = 4242;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public DataClient() {
        try {
            this.socket = new Socket(host, port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
        }
    }

    public Object sendCommand(String command) {
        try {
            if (oos != null) {
                oos.writeObject(command);
                oos.flush();
            }
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public void close() {
        if (socket != null) {
            try {
                socket.close();
                oos.close();
                ois.close();
            } catch (IOException e) {

            }
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
