import java.net.*;
import java.io.*;
import java.util.*;


public class BJMultiServer {
    public static BlackJackProtocol bjp;
    public static void main(String[] args) throws IOException {

    int portNumber;
    boolean listening = true;
    List<BJMultiServerThread> clients = new ArrayList<BJMultiServerThread>();
    BJMultiServerThread tmp;

    bjp = new BlackJackProtocol();

    if (args.length != 1)
    {
        System.err.println("Usage: java BJMultiServer <port number>");
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
        {
            tmp = new BJMultiServerThread(serverSocket.accept());
            tmp.start();
            clients.add(tmp);
            if (clients.size() == 2)
            {
              new TwoPlayerBJ(clients.get(0), clients.get(1)).start();
              clients.clear();
            }
        }
	    } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}
