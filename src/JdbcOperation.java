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

    public int insertNewUser(ArrayList<FMPerson> fmPersonArrayList)  {  //new User || import from excel
        int total = fmPersonArrayList.size();
        String sql = "INSERT INTO "+tableName +" (username,pswd,number_id,phone_number,gender,birth_date,money) values (?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        try{
            ps = (PreparedStatement) connection.prepareStatement(sql);
        }catch (SQLException e){
            e.printStackTrace();
            return -1;
        }

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
                return -1;
            }



        }
        return 0;
    }



    public int insertNewUser(FMPerson FMPerson1)  {
        ArrayList<FMPerson> personArrayList = new ArrayList<FMPerson>();
        personArrayList.add(FMPerson1);
        return this.insertNewUser(personArrayList);
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

    public String loginByUserName(String userName,String passWord){
        String userId = null;
        FMPerson tmpPerson = null;
        String sql = "SELECT * FROM " + tableName + " WHERE username= "+ userName;
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

        if(tmpPerson.getPswd() == passWord){
            userId = tmpPerson.getId();
        }
        return  userId;
    }

    boolean judgeUserNameWhetherExist(String userName){
        String sql = "SELECT username FROM " + tableName + " WHERE username= "+ userName;
        PreparedStatement pS;
        ResultSet rS;
        String result = null;
        try{
            pS = (PreparedStatement) connection.prepareStatement(sql);
            rS = pS.executeQuery();
            pS.close();
            result = rS.getString("username");
            if(result != null){
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;           //false means that username has not existed yet
    }

    int updateOnePerson(FMPerson updatePerson){     //method reload and allows it change multiply arguments at the same time

        FMPerson originalPerson = searchFromDatabase(updatePerson.getId());
        int returnValue = -1;
        if(!updatePerson.getUserName().equals(originalPerson.getUserName())){
            returnValue = updateOnePerson(updatePerson.getId(),updatePerson.getUserName(),null,null,null,null);
        }
        if(!updatePerson.getPswd().equals(originalPerson.getPswd())){
            returnValue = updateOnePerson(updatePerson.getId(),null,updatePerson.getPswd(),null,null,null);
        }
        if(!updatePerson.getPhoneNumber().equals(originalPerson.getPhoneNumber())){
            returnValue = updateOnePerson(updatePerson.getId(),null,null,updatePerson.getPhoneNumber(),null,null);
        }
        if(!updatePerson.getGender().equals(originalPerson.getGender())){
            returnValue = updateOnePerson(updatePerson.getId(),null,null,null,updatePerson.getGender(),null);
        }
        if(!updatePerson.getBirthDate().equals(originalPerson.getBirthDate())){
            returnValue = updateOnePerson(updatePerson.getId(),null,null,null,null,updatePerson.getBirthDate());

        }
        return returnValue;
    }

    int updateOnePerson(String personId,String userName,String password,String phoneNumber,String gender,Date birthDate){
        //return -1 when update failed, 0 when succeed
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
            return -1;
        }


        return 0;
    }

    void translationFromFMPersonToTmp(FMPerson tmpPerson){
        tmpId = tmpPerson.getId();
        tmpUserName = tmpPerson.getUserName();
        tmpPswd = tmpPerson.getPswd();
        tmpNumberId = tmpPerson.getNumberId();
        tmpPhoneNumber = tmpPerson.getPhoneNumber();
        tmpGender = tmpPerson.getGender();
        tmpBirthDate = tmpPerson.getBirthDate();
        tmpMoney = tmpPerson.getMoney();
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