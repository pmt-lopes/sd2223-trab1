package sd2223.trab1.clients;

import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.clients.rest.RestFeedsClient;

import java.net.URI;

/**
 * Class FeedsClientFactory - Generates REST and SOAP clients based on url
 *
 * @author Francisco Parrinha	58369
 * @author Martin Magdalinchev	58172
 */
public class FeedsClientFactory {

    /** Constants */
    private static final String REST = "/rest";
    private static final String SOAP = "/soap";

    public static Feeds get(URI serverURI) {
        var uriString = serverURI.toString();

        if (uriString.endsWith(REST))
            return new RestFeedsClient(serverURI);
        else if (uriString.endsWith(SOAP))
            //Soap not implemented
            return null;
        else
            throw new RuntimeException("Unknown service type..." + uriString);
    }
}
