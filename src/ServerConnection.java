import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnection {
    public void startListen() {
        ServerSocket socket = null;
        ServerThread thread;
        Socket mainSocket = null;
        try {
            socket = new ServerSocket(10001);
        } catch (IOException ioException) {
            System.out.println("This port has been used!");
        }
        while (true) {
            try {
                System.out.println("Listening!");
                mainSocket = socket.accept();
                System.out.println("Client's IP address:" + mainSocket.getInetAddress());
            } catch (IOException e) {
                System.out.println("Waiting for Client to connect!");
            }
            if (mainSocket != null) {
                new ServerThread(mainSocket).start();
            } else {
                continue;
            }
        }
    }

    class ServerThread extends Thread {
        Socket mainSocket;
        //        DataOutputStream dataOutputStream = null;
//        DataInputStream dataInputStream = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        String userId = null;
        ServerExecute serverExecute = null;

        ServerThread(Socket socket) {
            mainSocket = socket;
            try {
//                dataInputStream = new DataInputStream(mainSocket.getInputStream());
//                dataOutputStream = new DataOutputStream(mainSocket.getOutputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(mainSocket.getInputStream()));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(mainSocket.getOutputStream()));
                serverExecute = new ServerExecute(mainSocket.getInetAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true) {
                String inputCommand = null;
                try {
                    inputCommand = bufferedReader.readLine();       //The inputCommand should be send with a "\n" in the end
                    switch (inputCommand) {
                        case "Login":
                            while (userId == null) {
                                userId = serverExecute.login();      //if succeeded, userId will be correct, or it will return null.
                                if (userId == null) {
                                    bufferedWriter.write("login failed\n");
                                } else {
                                    bufferedWriter.write("login succeed\n");
                                }
                                bufferedWriter.flush();
                            }
                            break;
                        case "Register":
                            while (userId == null || userId.equals("name problem") || userId.equals("password problem") || userId.equals("else problem")) {
                                userId = serverExecute.register();  //if succeeded, userId will be correct, or it will return error messages.
                                if (userId.equals("name problem") || userId.equals("password problem") || userId.equals("else problem")) {
                                    bufferedWriter.write(userId + "\n");    //send error message to client
                                } else {
                                    bufferedWriter.write("register succeed\n");
                                }
                                bufferedWriter.flush();
                            }
                            break;
                        case "Personal Homepage":
                            serverExecute.personalHomepage(userId);
                            break;
                        case "Change Money":
                            String returnStatus = null;
                            returnStatus = serverExecute.changeMoney(userId);
                            bufferedWriter.write(returnStatus);
                            bufferedWriter.flush();
                            break;
                        case "Transfer Account":
                            String transferStatus = null;
                            transferStatus  = serverExecute.transferAccount(userId);
                            bufferedWriter.write(transferStatus);
                            bufferedWriter.flush();
                            break;
                        case "Change Personal Information":
                            String changeInformationStatus = null;
                            changeInformationStatus = serverExecute.changePersonalInformation();
                            bufferedWriter.write(changeInformationStatus);
                            bufferedWriter.flush();

                            break;
                        case "Root Account":

                            break;

                    }


                } catch (IOException e) {
                    System.out.println(mainSocket.getInetAddress() + ": Leave");
                }
            }
        }
    }
}