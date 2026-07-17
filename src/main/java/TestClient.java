import java.io.*;
import java.net.Socket;

public class TestClient {

    public static void main(String[] args) throws IOException {
        String hostname = "127.0.0.1";
        int port = 6379;
        Socket socket = new Socket(hostname, port);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

//                String test = """
//                        *2\r\n*3\r\n$3\r\nSET\r\n$5\r\nmango\r\n$9\r\nblueberry\r\n*3\r\n$3\r\nSET\r\n$5\r\nmango\r\n$9\r\nblueberry\r\n
//                        """;
//                String test = "*2\r\n*3\r\n$3\r\nSET\r\n$5\r\nmango\r\n$9\r\nblueberry\r\n*2\r\n$3\r\nGET\r\n$5\r\nmango";
//        String test = "*2\r\n*3\r\n$3\r\nSET\r\n$5\r\nmango\r\n$9\r\nblueberry\r\n$2\r\npx\r\n$3\r\n100\r\n*2\r\n$3\r\nGET\r\n$5\r\nmango";
//        String test = "*1\r\n$4\r\nPING";
        String test = "*1\r\n$4\r\nECHO\r\n$3\r\nmax";
        out.println(test);

        String response = reader.readLine();
        System.out.println("response: " + response);

        out.close();
//        readerIn.close();
        socket.close();
    }
}