import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class FMPerson implements Serializable {     //one FM person class
    private String id, userName, pswd, numberId, phoneNumber, gender;
    private double money;
    private java.sql.Date birthDate;

    FMPerson() {
    }

    FMPerson(String newId, String newUserName, String newPswd, String newNumberId, String newPhoneNumber, String newGender, Date newBirthDate) {
        id = newId;
        userName = newUserName;
        pswd = newPswd;
        numberId = newNumberId;
        phoneNumber = newPhoneNumber;
        gender = newGender.equals("0") ? "0" : "1";
        birthDate = newBirthDate;
        money = 2000;
    }

    FMPerson(String newId, String newUserName, String newPswd, String newNumberId, String newPhoneNumber, String newGender, Date newBirthDate, Double newMoney) {
        this(newId, newUserName, newPswd, newNumberId, newPhoneNumber, newGender, newBirthDate);
        money = newMoney;
    }

    FMPerson(String newUserName, String newPswd, String newNumberId, String newPhoneNumber, String newGender, Date newBirthDate, Double newMoney) {
        this("default", newUserName, newPswd, newNumberId, newPhoneNumber, newGender, newBirthDate, newMoney);
    }

    FMPerson(String newUserName, String newPswd, String newNumberId, String newPhoneNumber, String newGender, Date newBirthDate) {
        this("default", newUserName, newPswd, newNumberId, newPhoneNumber, newGender, newBirthDate, 2000.0);
    }

    void printOnePerson() {
        System.out.println("id:" + id + "\tusername:" + userName + "\tpassword:" + pswd + "\tMoney:" + money + "\tnumberId:" + numberId + "\tPhone Number:" + phoneNumber + "\tGender:" + gender + "\tbirth date:" + birthDate);
    }

    public String getUserName() {
        return userName;
    }

    public String getPswd() {
        return pswd;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getNumberId() {
        return numberId;
    }

    public String getId() {
        return id;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public double getMoney() {
        return money;
    }

    public String getGender() {
        return gender;
    }

    public void setBirthDate(java.util.Date birthDate) {
        if(birthDate == null){
            SimpleDateFormat sDF = new SimpleDateFormat("yyyy-MM-dd");
            try{
                birthDate = new java.sql.Date( sDF.parse("2000-01-01").getTime());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        this.birthDate = new java.sql.Date(birthDate.getTime());

    }

    public void setGender(String gender) {
        this.gender = gender.equals("0")  ? "0" : "1";
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setNumberId(String numberId) {
        this.numberId = numberId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}