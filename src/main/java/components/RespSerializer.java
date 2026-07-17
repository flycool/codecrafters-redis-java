package components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class RespSerializer {
    public void printWorking() {
        System.out.println("working-------------");
    }

    public String returnString() {
        return "hi";
    }

    public Store store = new Store();

    static void main() {
        RespSerializer serializer = new RespSerializer();
//        String test = """
//                *2\r\n*3\r\n$3\r\nSET\r\n$5\r\nmango\r\n$9\r\nblueberry\r\n*3\r\n$3\r\nSET\r\n$5\r\nmango\r\n$9\r\nblueberry\r\n
//                """;
//        String test = "*2\r\n*3\r\n$3\r\nSET\r\n$5\r\nmango\r\n$9\r\nblueberry\r\n*2\r\n$3\r\nGET\r\n$5\r\nmango";
        String test = "*2\r\n*3\r\n$3\r\nSET\r\n$5\r\nmango\r\n$9\r\nblueberry\r\n$2px\r\n$3100\r\n*2\r\n$3\r\nGET\r\n$5\r\nmango";
//        String test = "*1\r\n$4PING";
//        String test = "*1\r\n$4ECHO$3max";
        List<ArrayList<String>> res = serializer.deserialize(test.getBytes(StandardCharsets.UTF_8));
        System.out.println("res:" + res);

        try {
            serializer.handleCommand(null, res);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //
    public List<ArrayList<String>> deserialize(byte[] command) {
        String dataArr = new String(command, StandardCharsets.UTF_8);
        List<ArrayList<String>> res = new ArrayList<>();

        String commandStr = dataArr.replace("\r\n", "");
//        System.out.println(commandStr);
        String[] splitCommands = commandStr.split("\\*");
        ArrayList<String> commands = new ArrayList<>();
        for (String c : splitCommands) {
            if (c.isEmpty()) continue;
            commands.add(c);
        }
//        System.out.println("commands=" + commands);

        for (int i = 0; i < commands.size(); i++) {
            String[] cc = commands.get(i).split("\\$");
//            System.out.println(Arrays.toString(cc));
            if (cc.length < 2) continue;

            ArrayList<String> commandList = new ArrayList<>();
            for (int j = 1; j < cc.length; j++) {
                if(isStringDigit(cc[j])) {
                    String s = cc[j].substring(1);
                    commandList.add(s);
                    continue;
                }
                String c = removeNumFromString(cc[j]);
                commandList.add(c);
            }
            res.add(commandList);
        }

        return res;
    }

    public void handleCommand(Client client, List<ArrayList<String>> commands) throws IOException {
        for (ArrayList<String> c : commands) {
            String command = c.get(0);
            System.out.println("command: " + command);
            if (command.contains("PING")) {
                System.out.println("+PONG\r\n");
                //client.outputStream.write("+PONG\r\n".getBytes());
            } else if (command.contains("ECHO")) {
                String resp = encodingRespString(c.get(1));
                System.out.println(resp);
            } else if (command.equals("SET")) {
                long expiry = -1;
                int pxIndex = c.indexOf("px");
                if (pxIndex != -1) {
                    expiry = Long.parseLong(c.get(pxIndex + 1));
                }

                String key = c.get(1);
                String value = c.get(2);
                System.out.println("key: " + key);
                System.out.println("value: " + value);

                String ok = store.set(key, value, expiry);
                System.out.println("ok: " + ok);
            } else if (command.equals("GET")) {
                String key = c.get(1);
                System.out.println("v: " + key);

                String value = store.get(key);
                System.out.println("value: " + value);
            }

        }
    }

    // 3max, 12abcdefghijkl 3100
    private String removeNumFromString(String str) {
        int i = 0;
        while (i < str.length()) {
            char c = str.charAt(i);
            boolean digit = Character.isDigit(c);
            if (!digit) break;
            i++;
        }
        return str.substring(i);
    }

    private boolean isStringDigit(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String encodingRespString(String s) {
        int len = s.length();
        String resp = "$";
        resp += len;
        resp += "\r\n" + s + "\r\n";
        return resp;
    }

}
