import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible
    // when running tests.
    System.out.println("Logs from your program will appear here!");

    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    int port = 6379;
    try {
      serverSocket = new ServerSocket(port);
      // Since the tester restarts your program quite often, setting SO_REUSEADDR
      // ensures that we don't run into 'Address already in use' errors
      serverSocket.setReuseAddress(true);
      // Wait for connection from client.

      while (true) {
        clientSocket = serverSocket.accept();
        Socket finalClientSocket = clientSocket;
        CompletableFuture.runAsync(() -> {
          handleClient(finalClientSocket);
        });

      }

    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    } finally {
      try {
        if (clientSocket != null) {
          clientSocket.close();
        }
      } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
      }
    }
  }

  public static void handleClient(Socket clientSocket) {
    try {
      InputStream inputStream = clientSocket.getInputStream();
      OutputStream outputStream = clientSocket.getOutputStream();
      Scanner sc = new Scanner(inputStream);

      while (sc.hasNextLine()) {
        String nextLine = sc.nextLine();
        if (nextLine.contains("PING")) {
          clientSocket.getOutputStream().write("+PONG\r\n".getBytes());
        }
        if (nextLine.contains("ECHO")) {
          String respHeader = sc.nextLine();
          String respBody = sc.nextLine();
          String resp = respHeader + "\r\n" + respBody + "\r\n";
          outputStream.write(resp.getBytes());
        }
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }

  public static String encodingRespString(String s) {
    int len = s.length();
    String resp = "$";
    resp += len;
    resp += "\r\n" + s + "\r\n";
    return resp;
  }

}
