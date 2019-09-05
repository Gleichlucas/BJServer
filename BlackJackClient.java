import java.io.*;
import java.net.*;

public class BlackJackClient {

    public static void main(String[] args) throws IOException {

        if (args.length != 2)
        {
            System.err.println("Usage: java BlackJackClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
            Socket kkSocket   = new Socket(hostName, portNumber);
            PrintWriter out   = new PrintWriter(kkSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                                new InputStreamReader(kkSocket.getInputStream()));
        ) {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;

            while ((fromServer = in.readLine()) != null)
            {

                if (fromServer.equals("Read user input"))
                {
                    fromUser = stdIn.readLine();
                    if (fromUser != null)
                        out.println(fromUser);
                }
                else if (fromServer.equals("Read nbr"))
                {

                    fromUser = stdIn.readLine();
                    try { int tmpNbr = Integer.parseInt(fromUser);
                    } catch(Exception e) {
                        System.out.println("Execption prasing int... closing...");
                        return ;
                      }
                    if (fromUser != null)
                        out.println(fromUser);
                }
                else if (fromServer.equals("Game Over"))
                {
                  return ;
                }
                else
                {
                    System.out.println(fromServer);
                }
            }
            System.out.println("while is over party");
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }
}
