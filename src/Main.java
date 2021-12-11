import java.sql.Date;

public class Main {
    public static void main(String[] args) {
//        ServerConnection serverConnection = new ServerConnection();
//        serverConnection.startListen();

//        JdbcOperation jdbcOperation = new JdbcOperation();
//        FMPerson person = new FMPerson("root","PasswordRoot","000000000000","00000000000","0",new Date(0));
//        jdbcOperation.insertNewUser(person);

        /*
        String result;
        result = jdbcOperation.loginByUserName("root","PasswordRoot");
        System.out.println(result);

         */
        ServerConnection serverConnection = new ServerConnection();
        serverConnection.startListen();

    }
}
