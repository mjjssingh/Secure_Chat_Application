import javax.crypto.Cipher;
import java.io.*;
import java.net.*;
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

public class Server {

    private static Socket socket;
    private static BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;
    private static Scanner scanner;
    private static OutputStreamWriter outputStreamWriter;

    public static void main(String[] args) throws IOException {
        System.out.println("Server started .....");

        Server client = new Server();
        ServerSocket serverSocket = new ServerSocket(1234);
        socket = serverSocket.accept();
        System.out.println("Connection established with " + socket.getInetAddress());
        System.out.println("Send 'bye' to disconnect 😊");

        InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
        outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        bufferedReader = new BufferedReader(inputStreamReader);
        bufferedWriter = new BufferedWriter(outputStreamWriter);
        Scanner scanner = new Scanner(System.in);

//        sendKeys();

        client.listenForMessage();

        while(socket.isConnected()){
            String message = scanner.nextLine();
            try {
                bufferedWriter.write(encode(message));
                bufferedWriter.newLine();
                bufferedWriter.flush();
                System.out.println("--------------------------------------------------------------------------------------");            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                e.printStackTrace();
            }

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

    public void listenForMessage(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String message = "";
                while (socket.isConnected()) {
                    try {
                        message = decode(bufferedReader.readLine());
                        System.out.println("Server: " + (message));
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
                        e.printStackTrace();
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

        FileInputStream fileInputStream = new FileInputStream("//Users//Jabez//IdeaProjects//Secure Chat Application//key.pub");
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




//    public static void sendKeys() throws IOException {
//        OutputStream outputStream = socket.getOutputStream();
//        FileInputStream fileInputStream = new FileInputStream("E:\\Java Apps\\temp\\src\\keys\\key.priv");
//        byte[] bytes = new byte[4*1024];
//        fileInputStream.read(bytes, 0, bytes.length);
//        outputStream.write(bytes, 0, bytes.length);
//        System.out.println("Private Key sent successfully!!");
//
//        fileInputStream = new FileInputStream("E:\\Java Apps\\temp\\src\\keys\\key.pub");
//        bytes = new byte[4*1024];
//        fileInputStream.read(bytes, 0, bytes.length);
//        outputStream.write(bytes, 0, bytes.length);
//        System.out.println("Public Key sent successfully!!");
//
//    }
}