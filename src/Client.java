import javax.crypto.Cipher;
import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;

public class Client {
    private static Socket socket = null;
    private static BufferedReader bufferedReader = null;
    private static BufferedWriter bufferedWriter = null;

    public static void main(String[] args) throws Exception {
        System.out.println("Client started ....");
        socket = new Socket("localhost", 1234);
        System.out.println("Connection established");
        System.out.println("Send 'bye' to disconnect 😊");

        InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        bufferedReader = new BufferedReader(inputStreamReader);
        bufferedWriter = new BufferedWriter(outputStreamWriter);
        Scanner scanner = new Scanner(System.in);

//        getKeys();
        listenForMessage();

        while(socket.isConnected()){
            String message = scanner.nextLine();
            bufferedWriter.write(encode(message));
            bufferedWriter.newLine();
            bufferedWriter.flush();
            System.out.println("--------------------------------------------------------------------------------------");

            if(message.equalsIgnoreCase("Bye")){
                break;
            }
        }
        try{
            socket.close();
            bufferedReader.close();
            bufferedWriter.close();
        } catch(IOException exception){
            exception.printStackTrace();
        }
        System.out.println("Exited");
        System.exit(1);
    }
    public static void listenForMessage(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String message = "";
                while (socket.isConnected()) {
                    try {
                        message = decode(bufferedReader.readLine());
                        System.out.println("Server: " + message);
                        if (message.equalsIgnoreCase("bye")) {
                            try {
                                socket.close();
                                bufferedReader.close();
                                bufferedWriter.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                break;
                            }
                            break;
                        }
                    } catch (IOException e) {
                        try {
                            socket.close();
                            bufferedReader.close();
                            bufferedWriter.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            break;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("Server disconnected");
                System.exit(1);
            }
        });
        thread.start();
    }

    private static PublicKey loadPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] publicKeyBytes;

        FileInputStream fileInputStream = new FileInputStream("/Users/Jabez/IdeaProjects/Secure Chat Application/key.pub");
        publicKeyBytes = new byte[4*1024];
        fileInputStream.read(publicKeyBytes, 0, publicKeyBytes.length);

        KeyFactory publicKeyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = publicKeyFactory.generatePublic(publicKeySpec);
        return publicKey;
    }

    public static String encode(String toEncode) throws Exception {

        PublicKey publicKey = loadPublicKey();

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] bytes = cipher.doFinal(toEncode.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.getEncoder().encode(bytes));
    }
    private static PrivateKey loadPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] privateKeyBytes;

        FileInputStream fileInputStream = new FileInputStream("//Users//Jabez//IdeaProjects//Secure Chat Application//key.priv");
        privateKeyBytes = new byte[4*1024];
        fileInputStream.read(privateKeyBytes, 0, privateKeyBytes.length);

        KeyFactory privateKeyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey = privateKeyFactory.generatePrivate(privateKeySpec);
        return privateKey;
    }
    public static String decode(String toDecode) throws Exception {

        PrivateKey privateKey = loadPrivateKey();

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(toDecode));
        return new String(bytes);

    }




//    public static void getKeys() throws IOException {
//        InputStream inputStream = socket.getInputStream();
//        FileOutputStream fileOutputStream = new FileOutputStream("key.priv");
//        byte[] bytes = new byte[4*1024];
//        inputStream.read(bytes, 0, bytes.length);
//        fileOutputStream.write(bytes, 0, bytes.length);
//        System.out.println("Received private key successfully");
//
//        fileOutputStream = new FileOutputStream("key.pub");
//        bytes = new byte[4*1024];
//        inputStream.read(bytes, 0, bytes.length);
//        fileOutputStream.write(bytes, 0, bytes.length);
//        System.out.println("Received public key successfully");
//    }
}