package sd2223.trab1.servers.rest.users;

import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import sd2223.trab1.api.Discovery;

public class RESTUsersServer {

    private static Logger Log = Logger.getLogger(RESTUsersServer.class.getName());

    static{
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static final int PORT = 8080;
    private static final String SERVER_URI_FMT = "http://%s:%s/rest";

    public static void main(String[] args) {

        // Get Discovery Instance
        Discovery discovery = Discovery.getInstance();

        try {

            if (args.length != 1) {
                System.err.println("Invalid Argument! Expected: domain");
                System.exit(0);
                return;
            }

            // Get service name
            String serviceName = args[0] + ":users";

            ResourceConfig config = new ResourceConfig();
            config.register(RESTUserResource.class);
            // config.register(CustomLoggingFilter.class);

            String ip = InetAddress.getLocalHost().getHostAddress();
            String serverURI = String.format(SERVER_URI_FMT, ip, PORT);
            JdkHttpServerFactory.createHttpServer(URI.create(serverURI.replace(ip, "0.0.0.0")), config);

            // Announce
            discovery.announce(serviceName, serverURI);

            Log.info(String.format("%s Server ready @ %s\n", serviceName, serverURI));

            // More code can be executed here...
        } catch (Exception e) {
            Log.severe(e.getMessage());
        }
    }
}
