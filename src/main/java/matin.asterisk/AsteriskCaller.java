package matin.asterisk;

import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.OriginateAction;
import org.asteriskjava.manager.response.ManagerResponse;

import java.io.IOException;

public class AsteriskCaller {
    private final String host = "your app address";
    private final int port = 5038;
    private final String username = "admin";
    private final String password = "your password";

    private String[] convertOptToSingleNumber(int otp) {
        int param1 = otp / 100000;
        otp = otp - (param1 * 100000);

        int param2 = otp / 10000;
        otp = otp - (param2 * 10000);

        int param3 = otp / 1000;
        otp = otp - (param3 * 1000);

        int param4 = otp / 100;
        otp = otp - (param4 * 100);

        int param5 = otp / 10;
        otp = otp - (param5 * 10);

        int param6 = otp;

        return new String[]{String.valueOf(param1), String.valueOf(param2), String.valueOf(param3), String.valueOf(param4), String.valueOf(param5), String.valueOf(param6)};
    }

    private ManagerConnection initializeConnection() throws AuthenticationFailedException, IOException, TimeoutException {
        ManagerConnectionFactory factory = new ManagerConnectionFactory(host, port, username, password);
        ManagerConnection connection = factory.createManagerConnection();
        connection.login();
        return connection;
    }

    private void call(ManagerConnection connection, String[] params) throws IOException, TimeoutException {
        OriginateAction originateAction = new OriginateAction();
        originateAction.setChannel("PJSIP/7001");
        originateAction.setExten("7002");
        originateAction.setContext("internal");
        originateAction.setPriority(1);
        originateAction.setCallerId("7001");
        originateAction.setVariable("param1", params[0]);
        originateAction.setVariable("param2", params[1]);
        originateAction.setVariable("param3", params[2]);
        originateAction.setVariable("param4", params[3]);
        originateAction.setVariable("param5", params[4]);
        originateAction.setVariable("param6", params[5]);
        ManagerResponse originateResponse = connection.sendAction(originateAction);
        System.out.println("Originate Response: " + originateResponse.getResponse());
        connection.logoff();
    }

    public static void main(String[] args) {
        // AMI connection parameters
        int otp = 873214;
        AsteriskCaller caller = new AsteriskCaller();
        try {
            String[] params = caller.convertOptToSingleNumber(otp);
            ManagerConnection conn = caller.initializeConnection();
            caller.call(conn, params);

        } catch (AuthenticationFailedException | IOException e) {
            e.printStackTrace();
        } catch (TimeoutException ignored) {

        }
    }
}
