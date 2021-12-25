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
            while (!hasLeave) {
                String inputCommand = null;
                try {
                    inputCommand = bufferedReader.readLine();       //The inputCommand should be sent with a "\n" in the end
                    System.out.printf("from client:%s\n",inputCommand);
                    if ("Login".equals(inputCommand)) {//while (userId == null) {
                        userId = serverExecute.login();      //if succeeded, userId will be correct, or it will return null.
                        if (userId == null) {
                            bufferedWriter.write("login failed\n");
                        } else {
                            bufferedWriter.write("login succeed\n");
                        }
                        bufferedWriter.flush();
                        System.out.printf("%s\n", userId);
                        //}
                    } else if ("Register".equals(inputCommand)) {//while (userId == null || userId.equals("name problem") || userId.equals("password problem") || userId.equals("else problem")) {
                        userId = serverExecute.register();  //if succeeded, userId will be correct, or it will return error messages.
                        if (userId.equals("name problem") || userId.equals("password problem") || userId.equals("else problem")) {
                            bufferedWriter.write(userId + "\n");    //send error message to client
                        } else {
                            bufferedWriter.write("register succeed\n");
                        }
                        bufferedWriter.flush();
                        //}
                    } else if ("Personal Homepage".equals(inputCommand)) {
                        serverExecute.personalHomepage(userId);
                    } else if ("Change Money".equals(inputCommand)) {
                        String returnStatus = null;
                        returnStatus = serverExecute.changeMoney(userId);
                        bufferedWriter.write(returnStatus + "\n");
                        bufferedWriter.flush();
                    } else if ("Transfer Account".equals(inputCommand)) {
                        String transferStatus = null;
                        transferStatus = serverExecute.transferAccount(userId);
                        System.out.println("result:"+transferStatus);
                        bufferedWriter.write(transferStatus + "\n");
                        bufferedWriter.flush();
                        System.out.println("flushed");
                    } else if ("Change Personal Information".equals(inputCommand)) {
                        String changeInformationStatus = null;
                        String columnName = bufferedReader.readLine();
                        changeInformationStatus = serverExecute.changePersonalInformation(userId,columnName);
                        bufferedWriter.write(changeInformationStatus + "\n");
                        bufferedWriter.flush();
                    } else if ("Root Account".equals(inputCommand)) {
                        String loginResult = serverExecute.rootAccount(bufferedReader.readLine());
                        bufferedWriter.write(loginResult + "\n");
                        bufferedWriter.flush();
                    } else if ("import xls".equals(inputCommand)) {
                        serverExecute.importXls();
                    } else if ("export xls".equals(inputCommand)) {
                        serverExecute.exportXls();
                    } else if ("generate pdf report".equals(inputCommand)) {
                        serverExecute.generatePdfReport();
                    } else if ("close account over 70".equals(inputCommand)) {
                        serverExecute.closeAccount();
                    }
                    else {
                        System.out.println("server received"+inputCommand);
                    }
                } catch (IOException e) {
                    System.out.println(mainSocket.getInetAddress() + ": Leave");
                    hasLeave = true;
                }
            }
        }
    }
}