import java.net.*;
import java.io.*;


public class BJMultiServer {
    public static BlackJackProtocol bjp;
    public static void main(String[] args) throws IOException {

    int portNumber;
    boolean listening = true;
    bjp = new BlackJackProtocol();

    if (args.length != 1)
    {
        System.err.println("Usage: java KKMultiServer <port number>");
        System.exit(1);
    }
    try { portNumber = Integer.parseInt(args[0]);
    } catch(Exception e) {
        System.out.println("Stuff failed");
        return ;
    }
    System.out.println("Server runing on port " + portNumber);
    try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
        while (listening)
            new BJMultiServerThread(serverSocket.accept()).start();
	    } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}
