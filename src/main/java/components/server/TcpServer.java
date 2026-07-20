package components.server;

import components.service.CommandHandler;
import components.service.RespSerializer;
import infra.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class TcpServer {

    @Autowired
    public RespSerializer respSerializer;
    @Autowired
    public CommandHandler commandHandler;

    public void startServer(int port) {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);
            // Wait for connection from client.

            int id = 0;
            while (true) {
                clientSocket = serverSocket.accept();
                id++;
                Socket finalClientSocket = clientSocket;

                InputStream inputStream = finalClientSocket.getInputStream();
                OutputStream outputStream = finalClientSocket.getOutputStream();
                Client client = new Client(finalClientSocket, inputStream, outputStream, id);

                CompletableFuture.runAsync(() -> {
                    try {
                        handleClient(client);
                    } catch (IOException e) {
                        System.out.println("error: " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                });
            }

        } catch (IOException e) {
            log.error("IOException: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                log.error("IOException: " + e.getMessage());
            }
        }
    }

    public void handleClient(Client client) throws IOException {
        while (client.socket.isConnected()) {
            byte[] buffer = new byte[client.socket.getReceiveBufferSize()];
            int bytesRead = client.inputStream.read(buffer);
            if (bytesRead > 0) {
                String receivedData = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                List<ArrayList<String>> commands = respSerializer.deserialize(receivedData);
                for (ArrayList<String> command : commands) {
                    handleCommand(client, command);
                }
            }
        }
    }

    public void handleCommand(Client client, ArrayList<String> command) throws IOException {
        String cHead = command.get(0);
        System.out.println("command: " + cHead);
        String res = "";

        if (cHead.contains("PING")) {
            res = commandHandler.ping(command);
        } else if (cHead.contains("ECHO")) {
            res = commandHandler.echo(command);
        } else if (cHead.equals("SET")) {
            res = commandHandler.set(command);
        } else if (cHead.equals("GET")) {
            res = commandHandler.get(command);
        } else if (cHead.equals("INFO")) {
            res = commandHandler.info(command);
        }
        client.outputStream.write(res.getBytes());
    }

}
