import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.util.ArrayList;

public class ServerExecute {
    private JdbcOperation jdbcOperation = new JdbcOperation();
    //    String clientAddress;
    private InetAddress clientAddress;

    ServerExecute(InetAddress ipAddress) {
        clientAddress = ipAddress;
    }

    String login() {        //try to login
        String userId = null;
        String userName;
        String password;
        try {
            ServerSocket loginSocket = new ServerSocket(10002);
            Socket socket = loginSocket.accept();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            userName = bufferedReader.readLine();
            password = bufferedReader.readLine();
            userId = jdbcOperation.loginByUserName(userName, password);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return userId;
    }

    String register() {
        String userId = null;
        String userName = null, pswd = null;
        int flag = -2;
        try {       //try to register
            ServerSocket loginSocket = new ServerSocket(10003);
            Socket socket = loginSocket.accept();
            DataInputStream registerDIS = new DataInputStream(socket.getInputStream());
            ObjectInputStream registerOIS = new ObjectInputStream(registerDIS);
            FMPerson registerPerson = (FMPerson) registerOIS.readObject();
            registerOIS.close();
            flag = jdbcOperation.insertNewUser(registerPerson);
            userName = registerPerson.getUserName();
            pswd = registerPerson.getPswd();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException classNotFoundException) {
            System.out.println("Input Problem");
            classNotFoundException.printStackTrace();
        }

        //return error messages
        if (flag == -1) {
            if (pswd.length() >= 20) {
                userId = "password problem";
            } else {
                if (jdbcOperation.judgeUserNameWhetherExist(userName)) {
                    userId = "name problem";
                } else {
                    userId = "else problem";
                }
            }
        } else if (flag == 0) {
            userId = jdbcOperation.loginByUserName(userName, pswd);
        }
        return userId;
    }

    void personalHomepage(String userId) {  //send the whole person class back using socket, in this method
        FMPerson homepagePerson = jdbcOperation.searchFromDatabase(userId);
        try{
            ServerSocket homepageSocket = new ServerSocket(10004);
            Socket socket = homepageSocket.accept();
            DataOutputStream homepageDOS = new DataOutputStream(socket.getOutputStream());
            ObjectOutputStream homepageOOS = new ObjectOutputStream(homepageDOS);
            homepageOOS.writeObject(homepagePerson);
            homepageOOS.flush();
            homepageOOS.close();
        }catch (IOException ioException){
            ioException.printStackTrace();
        }

    }

    void changeMoney() {
        Double changeAmount = 0.0;
        try{    //get the 
            ServerSocket moneySocket = new ServerSocket(10005);
            Socket socket = moneySocket.accept();
            DataInputStream moneyChangeDIS = new DataInputStream(socket.getInputStream());
            changeAmount = moneyChangeDIS.readDouble();
            moneyChangeDIS.close();
        }catch (IOException e){
            e.printStackTrace();
        }


    }

    void transferAccount() {

    }

    void changePersonalInformation() {

    }

    void rootAccount() {

    }


    public void setClientAddress(InetAddress clientAddress) {
        this.clientAddress = clientAddress;
    }
}