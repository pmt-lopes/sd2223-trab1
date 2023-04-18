package sd2223.trab1.servers.rest.feeds;

import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import sd2223.trab1.api.Discovery;

public class RESTFeedsServer {

    private static Logger Log = Logger.getLogger(RESTFeedsServer.class.getName());
    private static int base;
    static{
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static final int PORT = 8081;
    private static final String SERVER_URI_FMT = "http://%s:%s/rest";

    public static void main(String[] args) {

        // Get Discovery Instance
        Discovery discovery = Discovery.getInstance();

        try {

            if (args.length != 3) {
                System.err.println("Invalid Argument! Expected: domain");
                System.exit(0);
                return;
            }
            // Get service name
            String serviceName = args[0] + ":feeds";
            base = Integer.parseInt(args[1]);

            ResourceConfig config = new ResourceConfig();
            config.register(RESTFeedResource.class);

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

    public static int getBase() {
        return base;
    }
}
