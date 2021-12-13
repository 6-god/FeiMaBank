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
        try {
            ServerSocket homepageSocket = new ServerSocket(10004);
            Socket socket = homepageSocket.accept();
            DataOutputStream homepageDOS = new DataOutputStream(socket.getOutputStream());
            ObjectOutputStream homepageOOS = new ObjectOutputStream(homepageDOS);
            homepageOOS.writeObject(homepagePerson);
            homepageOOS.flush();
            homepageOOS.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    String changeMoney(String changeId) {
        Double changeAmount = 0.0;
        try {    //get the change money amount
            ServerSocket moneySocket = new ServerSocket(10005);
            Socket socket = moneySocket.accept();
            DataInputStream moneyChangeDIS = new DataInputStream(socket.getInputStream());
            changeAmount = moneyChangeDIS.readDouble();
            moneyChangeDIS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FMPerson tmpPerson = jdbcOperation.searchFromDatabase(changeId);
        Double now = tmpPerson.getMoney();
        if (now + changeAmount < 0.0) {
            return "money not enough\n";
        }
        tmpPerson.setMoney(now + changeAmount);
//        jdbcOperation.updateOnePerson(changeId,tmpPerson.getUserName(),tmpPerson.getPswd(),tmpPerson.getPhoneNumber(),tmpPerson.getGender(),tmpPerson.getBirthDate());
        jdbcOperation.updateOnePerson(tmpPerson);
        return "succeed\n";

    }

    String transferAccount(String fromId) {     //the socket transport toId firstly, then transport amount
        String toId = null;
        Double transferAmount = 0.0;
        int flag = 0;
        try {
            ServerSocket transferSocket = new ServerSocket(10006);
            Socket socket = transferSocket.accept();
            BufferedReader transferBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            toId = transferBufferedReader.readLine();
            transferAmount = Double.valueOf(transferBufferedReader.readLine());
            FMPerson fromPerson = jdbcOperation.searchFromDatabase(fromId);
            FMPerson toPerson = jdbcOperation.searchFromDatabase(toId);
            transferAmount = Math.abs(transferAmount);
            if (toPerson == null) {
                return "person not found";
            }
            if (fromPerson.getMoney() + transferAmount < 0.0) {
                return "money not enough";
            }
            fromPerson.setMoney(fromPerson.getMoney() - transferAmount);
            toPerson.setMoney(toPerson.getMoney() + transferAmount);
            jdbcOperation.updateOnePerson(fromPerson);
            jdbcOperation.updateOnePerson(toPerson);
            return "succeed";

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "other error";
    }

    String changePersonalInformation(String userId) {//socket will transport which column to change firstly, then the columnValue
        String returnValue = null;
        String changeColumn;
        String changeValueString = null;
        FMPerson personToBeChanged = jdbcOperation.searchFromDatabase(userId);
        try {
            ServerSocket changeInformationSocket = new ServerSocket(10007);
            Socket socket = changeInformationSocket.accept();
            BufferedReader changeInformationBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            changeColumn = changeInformationBufferedReader.readLine();
            if (!changeColumn.equals("birth_date")) { //when the received data is string
                changeValueString = changeInformationBufferedReader.readLine();
                changeInformationBufferedReader.close();
            }
            if (changeColumn.equals("username")) {
                personToBeChanged.setUserName(changeValueString);
            } else if (changeColumn.equals("pswd")) {
                personToBeChanged.setPswd(changeValueString);
            } else if (changeColumn.equals("phone_number")) {
                personToBeChanged.setPhoneNumber(changeValueString);
            } else if (changeColumn.equals("gender")) {
                personToBeChanged.setGender(changeValueString);
            } else if (changeColumn.equals("birth_date")) {
                Date newDate = null;
                DataInputStream dateDIS = new DataInputStream(socket.getInputStream());
                ObjectInputStream dateOIS = new ObjectInputStream(dateDIS);
                newDate = (Date) dateOIS.readObject();
                personToBeChanged.setBirthDate(newDate);
            } else {
                returnValue = "failed";
            }

            if (jdbcOperation.updateOnePerson(personToBeChanged) == 0) {
                returnValue = "succeed";
            } else {
                returnValue = "failed";
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            returnValue = "failed";
        }


        return returnValue;
    }

    String rootAccount(String rootPswd) {   //try to log in root
        String result = "failed";
        String id = null;
        id = jdbcOperation.loginByUserName("root", rootPswd);
        if (id == null) {

        } else {
            result = "succeed";
        }

        return result;

    }

    void importXls() {
        ArrayList<FMPerson> personToBeImported = null;
        try {
            ServerSocket personSocket = new ServerSocket(10008);
            Socket socket = personSocket.accept();
            DataInputStream personDIS = new DataInputStream(socket.getInputStream());
            ObjectInputStream personOIS = new ObjectInputStream(personDIS);
            personToBeImported = (ArrayList<FMPerson>) personOIS.readObject();
            jdbcOperation.insertNewUser(personToBeImported);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    void exportXls() {
        ArrayList<FMPerson> personToBeExported = jdbcOperation.exportAllUsers();
        try {
            ServerSocket personSocket = new ServerSocket(10009);
            Socket socket = personSocket.accept();
            DataOutputStream personDOS = new DataOutputStream(socket.getOutputStream());
            ObjectOutputStream personOOS = new ObjectOutputStream(personDOS);
            personOOS.writeObject(personToBeExported);
            personOOS.flush();
            personOOS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void generatePdfReport() {  //firstly send the total users, then send total money by String
        try {
            ServerSocket reportTransportSocket = new ServerSocket(10010);
            Socket socket = reportTransportSocket.accept();

            DataOutputStream reportDOS = new DataOutputStream(socket.getOutputStream());
            ObjectOutputStream reportOOS  = new ObjectOutputStream(reportDOS);
            ArrayList<Double> doubleList = jdbcOperation.tableTotalCount();

            reportOOS.writeObject(doubleList);
            reportOOS.close();

//            BufferedWriter reportDataWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//            reportDataWriter.write((int) Math.round(doubleList.get(0)));
//            reportDataWriter.write(doubleList.get(1).toString());
//            reportDataWriter.flush();
//            reportDataWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public void setClientAddress(InetAddress clientAddress) {
        this.clientAddress = clientAddress;
    }
}