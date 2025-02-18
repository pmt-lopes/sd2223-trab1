package sd2223.trab1.servers.soap;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.ws.Endpoint;
import sd2223.trab1.api.Discovery;

public class SoapFeedsServer {

    public static final int PORT = 8081;
    private static final String FEEDS = ":feeds";
    public static String SERVER_BASE_URI = "http://%s:%s/soap";
    private static Logger Log = Logger.getLogger(SoapFeedsServer.class.getName());

    public static void main(String[] args) throws Exception {

        String domain = args[0];
        int base = Integer.parseInt(args[1]);

        String serviceName = domain + FEEDS;

        Log.setLevel(Level.INFO);
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            String serverURI = String.format(SERVER_BASE_URI, ip, PORT);
            Discovery discovery = Discovery.getInstance();
            discovery.announce(serviceName, serverURI);
            Endpoint.publish(serverURI, new SoapFeedsWebService(domain, base));

            Log.info(String.format("%s Soap Server ready @ %s\n", serviceName, serverURI));

        } catch (Exception e) {
            Log.severe(e.getMessage());
        }
    }
}
