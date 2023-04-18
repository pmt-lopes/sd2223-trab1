package sd2223.trab1.clients;

import java.net.URI;

import sd2223.trab1.api.java.Users;
import sd2223.trab1.clients.rest.RestUsersClient;

public class UsersClientFactory {

    private static final String REST = "/rest";
    private static final String SOAP = "/soap";

    public static Users get(URI serverURI) {
        var uriString = serverURI.toString();

        if (uriString.endsWith(REST))
            return new RestUsersClient(serverURI);
        else if (uriString.endsWith(SOAP))
            //Soap not implemented
            return null;
        else
            throw new RuntimeException("Unknown service type..." + uriString);
    }
}

