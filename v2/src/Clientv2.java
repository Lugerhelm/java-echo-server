import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Clientv2 {
    static final int ok = 2;
    static final int error = 3;
    static final int echoRequest = 4;
    static final int fileRequest = 5;

    private static void ilusWriteUTF(DataOutputStream out, String str) throws Exception {
        byte[] encoded = str.getBytes("UTF-8");
        out.writeInt(encoded.length);
        out.write(encoded);
    }
    private static String ilusReadUTF(DataInputStream in) throws Exception {
        int length = in.readInt();
        byte[] encoded = in.readNBytes(length);
        return new String(encoded, "UTF-8");
    }
    private static void echo(DataOutputStream dataOut, DataInputStream dataIn, String[] args, int i) throws Exception {
        System.out.println("saadab echo requesti");
        dataOut.writeInt(echoRequest);
        if (dataIn.readInt() != ok){
            new RuntimeException("ei saanud echo requestile vastust");
        }
        System.out.println("saadab sõnumi");
        ilusWriteUTF(dataOut, args[i+1]);
        if (dataIn.readInt() != ok){
            new RuntimeException("ei saanud sõnumi saatmisele vastust");
        }
        System.out.println("loeb sõnumi");
        ilusReadUTF(dataIn);
    }
    private static void file(DataOutputStream dataOut, DataInputStream dataIn, String[] args, int i, Path received)
            throws Exception {
        System.out.println("saadab file requesti");
        dataOut.writeInt(fileRequest);
        if (dataIn.readInt() != ok){
            new RuntimeException("ei saanud file requestile vastust");
        }
        System.out.println("Saadab faili");
        ilusWriteUTF(dataOut, args[i+1]);
        if (dataIn.readInt() != ok){
            new RuntimeException("ei saanud faili saatmisele vastust");
        }
        String failiSisu = ilusReadUTF(dataIn);

        Path uusFile = received.resolve(args[i+1]);
        Files.write(uusFile, failiSisu.getBytes());
        System.out.println("fail loodud ja sellesse kirjutatud");
    }


    public static void main(String[] args) throws Exception {
        System.out.println("connecting to server");
        try (Socket socket = new Socket("localhost", 1337);
             OutputStream out = socket.getOutputStream();
             DataOutputStream dataOut = new DataOutputStream(out);
             DataInputStream dataIn = new DataInputStream(socket.getInputStream());) {

            Path received = Paths.get("received");
            if (!Files.exists(received)) {
                Files.createDirectory(received);
            }


            System.out.println("connected; sending data");
            int sõnumiteArv = args.length;
            dataOut.writeInt(sõnumiteArv);
            System.out.println("sent " + sõnumiteArv);

            if (dataIn.readInt() == ok){
                for (int i  = 0; i < args.length; i+= 2) {
                    System.out.println("*******************");
                    System.out.println("tsükkel nr " + i / 2);

                    if (args[i].equals("echo")){
                        echo(dataOut, dataIn, args, i);
                    } else if (args[i].equals("file")){
                        file(dataOut, dataIn, args, i, received);
                    }
                }

            } else{
                new RuntimeException("Server ei saanud sõnumite arvu");
            }



        } finally {
            System.out.println("finished");
        }

    }
}

