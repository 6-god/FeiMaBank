import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Scanner;

public class JdbcOperation {
    String tmpId,tmpUserName,tmpPswd,tmpNumberId,tmpPhoneNumber,tmpGender;
    String dataBaseName = "FMBANK";
    String tableName = "Personal_Data";
    Date tmpBirthDate;
    String loginId;
    double tmpMoney;
    Connection connection;
    SimpleDateFormat sDF = new SimpleDateFormat("yyyy-MM-dd");
    JdbcOperation(){
        connectToMysql("39.99.61.152","root","Passwordsql1");
    }
    JdbcOperation(String ipAddress,String userName,String password){
        connectToMysql(ipAddress,userName,password);
    }



    public boolean connectToMysql(String ipAddress, String userName, String password){
        String url = "jdbc:mysql://"+ipAddress+"/"+dataBaseName;
        try{
            connection= DriverManager.getConnection(url,userName,password);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void insertNewUser(ArrayList<FMPerson> fmPersonArrayList) throws SQLException {  //new User || import from excel
        int total = fmPersonArrayList.size();
        String sql = "INSERT INTO "+tableName +" (username,pswd,number_id,phone_number,gender,birth_date,money) values (?,?,?,?,?,?,?)";
        PreparedStatement ps;
        ps = (PreparedStatement) connection.prepareStatement(sql);
        for(int i = 0 ; i<total;i++){
            try{
                FMPerson personToAdd = fmPersonArrayList.get(i);
                translationFromFMPersonToTmp(personToAdd);   //store the statements into the tmpXXX for this class, from the arraylist, one by one

                //ps.setString(1,tmpId);
                ps.setString(2,tmpUserName);
                ps.setString(3,tmpPswd);
                ps.setString(4,tmpNumberId);
                ps.setString(5,tmpPhoneNumber);
                ps.setString(6,tmpGender);
                ps.setDate(7, tmpBirthDate);
                ps.setDouble(8,tmpMoney);

                ps.executeUpdate();
                ps.close();

            } catch (SQLException sqlException){
                sqlException.printStackTrace();
            }


        }
    }

    public void deleteUserFromDatabase(String idToDelete){  //delete User
        String sql = "DELETE FROM " + tableName + " WHERE id='"+idToDelete+"'";
        PreparedStatement pS;
        try{
            pS = (PreparedStatement) connection.prepareStatement(sql);
            pS.executeUpdate();
            pS.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public FMPerson searchFromDatabase(String idToSearch){
        FMPerson tmpPerson = null;
        String sql = "SELECT * FROM " + tableName + " WHERE id= "+ idToSearch;
        PreparedStatement pS;
        ResultSet rS;
        try{
            pS = (PreparedStatement) connection.prepareStatement(sql);
            rS = pS.executeQuery();
            pS.close();
            tmpPerson = new FMPerson();
            while(rS.next()){
                tmpPerson.setId(rS.getString("id"));
                tmpPerson.setUserName(rS.getString("username"));
                tmpPerson.setPswd(rS.getString("pswd"));
                tmpPerson.setNumberId(rS.getString("number_id"));
                tmpPerson.setPhoneNumber(rS.getString("phone_number"));
                tmpPerson.setGender(rS.getString("gender"));
                tmpPerson.setBirthDate(rS.getDate("birth_date"));
                tmpPerson.setMoney(rS.getDouble("money"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        if(tmpPerson!=null){
            tmpPerson.printOnePerson();
        }
        return tmpPerson;   //if query failed ,return null
    }

    int updateOnePerson(String personId,String userName,String password,String phoneNumber,String gender,Date birthDate){
        //FMPerson personToChange = searchFromDatabase(personId);
        int flag = 0;
        String columnName,columnValue;
        if(userName!=null){
            columnName = "username";
            columnValue = userName;
            //personToChange.setUserName(userName);

        }else if(password!=null){
            columnName = "pswd";
            columnValue = password;
            //personToChange.setPswd(password);
        }else if(phoneNumber!=null){
            columnName = "phone_number";
            columnValue = phoneNumber;
            //personToChange.setPhoneNumber(phoneNumber);
        }else if(gender !=null){
            columnName = "gender";
            columnValue = gender;
            //personToChange.setGender(gender);
        }else if(birthDate!=null){
            columnName ="birth_date";
            columnValue = sDF.format(birthDate);
            //personToChange.setBirthDate(birthDate);
        }else{
            System.out.println("input error: all null");
            return -1;
        }

        String sql = "UPDATE "+ tableName+" SET "+columnName+"='" + columnValue + "' WHERE id='" + personId + "'";
        try{
            PreparedStatement pS = (PreparedStatement) connection.prepareStatement(sql);
            flag = pS.executeUpdate();
            pS.close();
        }catch (SQLException e){
            e.printStackTrace();
        }


        return 0;
    }

    void translationFromFMPersonToTmp(FMPerson tmpPerson){
        tmpId = tmpPerson.id;
        tmpUserName = tmpPerson.userName;
        tmpPswd = tmpPerson.pswd;
        tmpNumberId = tmpPerson.numberId;
        tmpPhoneNumber = tmpPerson.phoneNumber;
        tmpGender = tmpPerson.gender;
        tmpBirthDate = tmpPerson.birthDate;
        tmpMoney = tmpPerson.money;
    }

    FMPerson translationFromTmpToFMPerson(String id,String userName,String pswd,String numberId,String phoneNumber,String gender,Date birthDate,Double money){
        FMPerson person = new FMPerson(id,userName,pswd,numberId,phoneNumber,gender,birthDate,money);
        return person;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getLoginId() {
        return loginId;
    }

    /**
     public Connection getConnection() {
     return connection;
     }

     public String getGender() {
     return gender;
     }

     public double getMoney() {
     return money;
     }

     public String getBirthDate() {
     return birthDate;
     }

     public String getId() {
     return id;
     }

     public String getNumberId() {
     return numberId;
     }

     public String getPhoneNumber() {
     return phoneNumber;
     }

     public String getPswd() {
     return pswd;
     }

     public String getUserName() {
     return userName;
     }*/

}