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
        if (socket==null)
            return;

        try {
            myOutputStream = new ObjectOutputStream(socket.getOutputStream());
            myInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String msg)  {
        //System.out.println("WRITING:"+msg);
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SocketException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println("READING:"+myObject.getMessage());
        return myObject.getMessage();
    }


    public void close() throws IOException {
        myOutputStream.close();
        myInputStream.close();
    }

//    public String WaitForReply() {
//        String INPUT="";
//        try {
//            while (!(INPUT=read()).equals("")){
//                System.out.println(INPUT);
//                break;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        return INPUT;
//    }
}
