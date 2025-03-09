import java.io.*;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws Exception {
        try (ServerSocket ss = new ServerSocket(1337)) {
            System.out.println("now listening on :1337");
            try (Socket port = ss.accept();
                 InputStream in = port.getInputStream();
                 OutputStream out = port.getOutputStream();
                 DataInputStream dataIn = new DataInputStream(in);
                 DataOutputStream dataOut = new DataOutputStream(out);) {


                System.out.println("client connected; waiting for a byte");
                System.out.println("Saada sõnumite arv");


                int sõnumiteArv = dataIn.readInt();
                System.out.println("received " + sõnumiteArv);
                for (int i = 0; i < sõnumiteArv; i++) {
                    System.out.println("valmis sõnumit vastu võtma");
                    String sõnum = dataIn.readUTF();
                    System.out.println("sõnum oli " + sõnum);
                    System.out.println("saadan tagasi");
                    dataOut.writeUTF(sõnum);
                    System.out.println("sõnum saadetud");


                }

            }
        }
        System.out.println("finished");
    }
}