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
        Boolean hasLeave = false;
        ServerThread(Socket socket) {
            System.out.println("start a thread");
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
            System.out.println("running");
            while (!hasLeave) {
                String inputCommand = null;
                try {
                    inputCommand = bufferedReader.readLine();       //The inputCommand should be sent with a "\n" in the end
                    if ("Login".equals(inputCommand)) {
                        System.out.println("logining");
                        while (userId == null) {
                            userId = serverExecute.login();      //if succeeded, userId will be correct, or it will return null.
                            if (userId == null) {
                                bufferedWriter.write("login failed\n");
                            } else {
                                bufferedWriter.write("login succeed\n");
                            }
                            bufferedWriter.flush();
                        }
                    } else if ("Register".equals(inputCommand)) {
                        while (userId == null || userId.equals("name problem") || userId.equals("password problem") || userId.equals("else problem")) {
                            userId = serverExecute.register();  //if succeeded, userId will be correct, or it will return error messages.
                            if (userId.equals("name problem") || userId.equals("password problem") || userId.equals("else problem")) {
                                bufferedWriter.write(userId + "\n");    //send error message to client
                            } else {
                                bufferedWriter.write("register succeed\n");
                            }
                            bufferedWriter.flush();
                        }
                    } else if ("Personal Homepage".equals(inputCommand)) {
                        serverExecute.personalHomepage(userId);
                    } else if ("Change Money".equals(inputCommand)) {
                        String returnStatus = null;
                        returnStatus = serverExecute.changeMoney(userId);
                        bufferedWriter.write(returnStatus);
                        bufferedWriter.flush();
                    } else if ("Transfer Account".equals(inputCommand)) {
                        String transferStatus = null;
                        transferStatus = serverExecute.transferAccount(userId);
                        bufferedWriter.write(transferStatus);
                        bufferedWriter.flush();
                    } else if ("Change Personal Information".equals(inputCommand)) {
                        String changeInformationStatus = null;
                        changeInformationStatus = serverExecute.changePersonalInformation(userId);
                        bufferedWriter.write(changeInformationStatus);
                        bufferedWriter.flush();
                    } else if ("Root Account".equals(inputCommand)) {
                        String loginResult = serverExecute.rootAccount(bufferedReader.readLine());
                        bufferedWriter.write(loginResult);
                        bufferedWriter.flush();
                    } else if ("import xls".equals(inputCommand)) {
                        serverExecute.importXls();


                        serverExecute.exportXls();


                        serverExecute.generatePdfReport();
                    } else if ("export xls".equals(inputCommand)) {
                        serverExecute.exportXls();


                        serverExecute.generatePdfReport();
                    } else if ("generate pdf report".equals(inputCommand)) {
                        serverExecute.generatePdfReport();
                    }


                } catch (IOException e) {
                    System.out.println(mainSocket.getInetAddress() + ": Leave");
                    hasLeave = true;
                }
            }
        }
    }
}