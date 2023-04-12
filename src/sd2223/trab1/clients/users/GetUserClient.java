package sd2223.trab1.clients.users;

import sd2223.trab1.api.Discovery;
import sd2223.trab1.api.User;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class GetUserClient {

    private static Logger Log = Logger.getLogger(CreateUserClient.class.getName());
    private static Discovery discovery = Discovery.getInstance();

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 3) {
            System.err.println("Use: name pwd ");
            return;
        }

        String name = args[0];
        String pwd = args[1];

        String serviceName = "users.";
        URI url = discovery.knownUrisOf(serviceName, 1)[0];

        Log.info("Sending request to server.");

        var result = new RestUserClient(url).getUser(name, pwd);
        System.out.println("Result: " + result);
    }

}
