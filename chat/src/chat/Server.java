package chat;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static chat.MessageType.*;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket;
        while(true) {
            ConsoleHelper.writeMessage("Введите порт");
            int port = ConsoleHelper.readInt();
            try {
                serverSocket = new ServerSocket(port);
                ConsoleHelper.writeMessage("Сервер запущен");
                break;
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Неверный порт, попробуйте еще раз");
            }
        }
        while(true) {
            try {
                Socket socket = serverSocket.accept();
                new Handler(socket).start();
            } catch (Exception e) {
                serverSocket.close();
                e.getMessage();
                break;
            }
        }

    }

    public static void sendBroadcastMessage(Message message) {
        for(Map.Entry<String, Connection> entry : connectionMap.entrySet()) {
            try {
                entry.getValue().send(message);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Не удалось отправить сообщение пользователю " + entry.getKey());
            }
        }
    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            String userName = null;
            ConsoleHelper.writeMessage("Установелнно соединение с удаленным адресом: " + socket.getRemoteSocketAddress());
            try(Connection connection = new Connection(socket)) {
                userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(USER_ADDED, userName));
                sendListOfUsers(connection, userName);
                serverMainLoop(connection, userName);
            } catch (ClassNotFoundException e) {
            ConsoleHelper.writeMessage("Произошла ошибка ClassNotFoundException при обмене данными с удаленным сервером: " + socket.getRemoteSocketAddress());
            }   catch (IOException e) {
            ConsoleHelper.writeMessage("Произошла ошибка при обмене данными с удаленным сервером: " + socket.getRemoteSocketAddress());
            } finally {
                if(userName != null) {
                    sendBroadcastMessage(new Message(USER_REMOVED, userName));
                    connectionMap.remove(userName);
                }
                ConsoleHelper.writeMessage("Соединение с удаленным сервером " + socket.getRemoteSocketAddress() + " закрыто.");
            }


        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            String result = null;
            while(true) {
                connection.send(new Message(NAME_REQUEST));
                Message nameMessage = connection.receive();
                if(nameMessage.getType() == USER_NAME) {
                    result = nameMessage.getData();
                    if (!result.isEmpty()) {
                        if(!connectionMap.containsKey(result)){
                            connectionMap.put(result, connection);
                            connection.send(new Message(NAME_ACCEPTED));
                            return result;
                        }

                    }
                }
            }
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException {
            for(Map.Entry<String, Connection> entry : connectionMap.entrySet()) {
                if(!entry.getKey().equals(userName)) {
                    Message message = new Message(USER_ADDED, entry.getKey());
                    connection.send(message);
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while(true) {
                Message message = connection.receive();
                if(message.getType() == TEXT) {
                    String text = userName + ": " + message.getData();
                    sendBroadcastMessage(new Message(TEXT, text));
                } else ConsoleHelper.writeMessage("Error");
            }
        }

    }
}
