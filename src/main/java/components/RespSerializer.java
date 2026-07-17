package components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class RespSerializer {
    @Autowired
    public Store store;


    public List<ArrayList<String>> deserialize(String commandStr) {
//        System.out.println("commandStr: " + commandStr);
        List<ArrayList<String>> res = new ArrayList<>();

        String[] splitCommands = commandStr.split("\\*");
        ArrayList<String> commands = new ArrayList<>();
        for (String c : splitCommands) {
            if (c.isEmpty()) continue;
            commands.add(c);
        }
//        System.out.println("commands=" + commands);

        for (int i = 0; i < commands.size(); i++) {
            String[] cc = commands.get(i).split("\\$");

            if (cc.length < 2) continue;

            ArrayList<String> commandList = new ArrayList<>();
            for (int j = 1; j < cc.length; j++) {
                String[] scc = cc[j].split("\r\n");
                commandList.add(scc[1]);
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
                client.outputStream.write("+PONG\r\n".getBytes());
            } else if (command.contains("ECHO")) {
                String resp = encodingRespString(c.get(1));
                System.out.println(resp);
                client.outputStream.write(resp.getBytes());
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

                client.outputStream.write(ok.getBytes());
            } else if (command.equals("GET")) {
                String key = c.get(1);
                System.out.println("v: " + key);

                String value = store.get(key);
                System.out.println("value: " + value);
                client.outputStream.write(value.getBytes());
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
