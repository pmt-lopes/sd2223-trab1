package sd2223.trab1.clients.users;

import sd2223.trab1.api.Discovery;
import sd2223.trab1.api.User;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class UpdateUserClient {

    private static Logger Log = Logger.getLogger(CreateUserClient.class.getName());
    private static Discovery discovery = Discovery.getInstance();

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 5) {
            System.err.println("Use: name pwd pwd displayName domain");
            return;
        }

        String name = args[1];
        String pwd = args[2];
        String nPwd = args[3];
        String domain = args[4];
        String displayName = args[5];



        String serviceName = "users." + domain;
        URI url = discovery.knownUrisOf(serviceName, 1)[0];
        var user = new RestUserClient(url).getUser(name, pwd);
        if(nPwd.equals(null))
            nPwd= user.getPwd();
        if(domain==null)
            domain= user.getDomain();
        if(displayName==null)
            displayName= user.getDisplayName();

        Log.info("Sending request to server.");
        User u = new User(name, nPwd, domain, displayName);
        var result = new RestUserClient(url).createUser(u);
        System.out.println("Result: " + result);
    }
}
