import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws Exception {
        System.out.println("connecting to server");
        try (Socket socket = new Socket("localhost", 1337);
             OutputStream out = socket.getOutputStream();
             DataOutputStream dataOut = new DataOutputStream(out);
             DataInputStream dataIn = new DataInputStream(socket.getInputStream());) {
            System.out.println("connected; sending data");
            int sõnumiteArv = args.length;
            dataOut.writeInt(sõnumiteArv);
            System.out.println("sent " + sõnumiteArv);
            for (String sõnum : args) {
                System.out.println("saadan sõnumi");
                dataOut.writeUTF(sõnum);
                System.out.println("sõnum saadetud");
                System.out.println("ootan vastust");
                String echoSõnum = dataIn.readUTF();
                System.out.println("sõnum oli: " + echoSõnum);
            }
        }
        System.out.println("finished");
    }
}