package sd2223.trab1.servers;

import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import sd2223.trab1.api.Discovery;
import sd2223.trab1.servers.resources.RestUsersResource;

public class RestUsersServer {

    private static Logger Log = Logger.getLogger(RestUsersServer.class.getName());

    static{
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static final int PORT = 8080;
    public static final String SERVICE = "Users";
    private static final String SERVER_URI_FMT = "http://%s:%s/rest";

    public static void main(String[] args) {

        // Get Discovery Instance
        Discovery discovery = Discovery.getInstance();

        try {

            if(args.length != 1){
                System.err.println("Invalid Arguments! Expected: domain");
                System.exit(0);
                return;
            }

            // Get Server Domain
            String domain = args[0];

            ResourceConfig config = new ResourceConfig();
            config.register(RestUsersResource.class);

            String ip = InetAddress.getLocalHost().getHostAddress();
            String serverURI = String.format(SERVER_URI_FMT, ip, PORT);
            JdkHttpServerFactory.createHttpServer(URI.create(serverURI.replace(ip, "0.0.0.0")), config);

            // Generate ServiceName in format
            String ServiceName = domain + ":" + SERVICE;

            // Announce
            discovery.announce(ServiceName, serverURI);

            Log.info(String.format("%s Server ready @ %s\n", ServiceName, serverURI));

            // More code can be executed here...
        } catch (Exception e) {
            Log.severe(e.getMessage());
        }
    }

}
