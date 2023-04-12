package sd2223.trab1.clients.users;

import sd2223.trab1.api.User;
import sd2223.trab1.api.Discovery;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class CreateUserClient {

    private static Logger Log = Logger.getLogger(CreateUserClient.class.getName());
    private static Discovery discovery = Discovery.getInstance();

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 4) {
            System.err.println("Use: name pwd displayName domain");
            return;
        }

        String name = args[0];
        String pwd = args[1];
        String domain = args[2];
        String displayName = args[3];

        User u = new User(name, pwd, domain, displayName);

        String serviceName = "users." + domain;
        URI url = discovery.knownUrisOf(serviceName, 1)[0];

        Log.info("Sending request to server.");

        var result = new RestUserClient(url).createUser(u);
        System.out.println("Result: " + result);
    }
}
