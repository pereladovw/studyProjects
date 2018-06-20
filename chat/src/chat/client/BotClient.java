package chat.client;

import chat.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BotClient extends Client {

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }

    public class BotSocketThread extends SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            if(message.contains(": ")) {
                String[] commands = message.split(": ");
                if (commands.length == 2) {
                    Map<String, String> map = new HashMap<>();
                    map.put("дата", "d.MM.YYYY");
                    map.put("день", "d");
                    map.put("месяц", "MMMM");
                    map.put("год", "YYYY");
                    map.put("время", "H:mm:ss");
                    map.put("час", "H");
                    map.put("минуты", "m");
                    map.put("секунды", "s");
                    if (map.containsKey(commands[1])) {
                        SimpleDateFormat format = new SimpleDateFormat(map.get(commands[1]));
                        sendTextMessage(String.format("Информация для %s: %s", commands[0], format.format(new Date().getTime())));
                    }

                }
            }

        }
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected String getUserName() {
        int X = (int) (Math.random()*101);
        return ("date_bot_" + X);
    }
}
