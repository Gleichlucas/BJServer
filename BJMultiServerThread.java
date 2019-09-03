import java.net.*;
import java.io.*;

public class BJMultiServerThread extends Thread {

  private Socket socket = null;
  public PrintWriter out;
  public BufferedReader in;
  public String userInput;
  public String alive;

public BJMultiServerThread(Socket socket) {
    super("BJMultiServerThread");
    this.socket = socket;
    out = null;
    in = null;
    alive = ("Yaas man, relax");
}

public void run() {

    try (
        PrintWriter outTmp = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader inTmp = new BufferedReader(
            new InputStreamReader(
                socket.getInputStream()));
    ) {
        String inputLine, outputLine;
        out = outTmp;
        in = inTmp;
        out.println("Looking for a Game ...");

        while ((inputLine = in.readLine()) != null) {
            if (inputLine.equals("Hey man, u stil there?"))
              alive = ("Yaas man, relax");
            else
              userInput = inputLine;
        }
        System.out.println("closing socket ...");
        socket.close();
    } catch (Exception e) {
        System.out.println("Thead exception");
        e.printStackTrace();
    }
  }
}
