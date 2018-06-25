import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

class SocketUtil {
    private ObjectOutputStream myOutputStream;
    private ObjectInputStream myInputStream;
    private DataObject myObject;

    SocketUtil(Socket socket) {
        try {
            myOutputStream = new ObjectOutputStream(socket.getOutputStream());
            myInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String msg)  {
        myObject = new DataObject();
        myObject.setMessage(msg);
        try {
            myOutputStream.writeObject(myObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read() {
        try {
            myObject = (DataObject) myInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myObject.getMessage();
    }

}
