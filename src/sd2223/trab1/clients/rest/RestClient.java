package sd2223.trab1.clients.rest;

import static sd2223.trab1.api.java.Result.error;
import static sd2223.trab1.api.java.Result.ErrorCode;
import static sd2223.trab1.api.java.Result.ok;

import java.net.URI;
import java.util.function.Supplier;
import java.util.logging.Logger;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import sd2223.trab1.api.java.Result;

public class RestClient {
    private static Logger Log = Logger.getLogger(RestClient.class.getName());

    protected static final int READ_TIMEOUT = 5000;
    protected static final int CONNECT_TIMEOUT = 5000;

    protected static final int RETRY_SLEEP = 3000;
    protected static final int MAX_RETRIES = 10;

    final URI serverURI;
    final Client client;
    final ClientConfig config;

    RestClient(URI serverURI) {
        this.serverURI = serverURI;
        this.config = new ClientConfig();

        config.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT);
        config.property( ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);

        this.client = ClientBuilder.newClient(config);
    }

    protected <T> T reTry(Supplier<T> func) {
        for (int i = 0; i < MAX_RETRIES; i++)
            try {
                return func.get();
            } catch (ProcessingException x) {
                System.err.println( x.getMessage() );
                Log.fine("ProcessingException: " + x.getMessage());
                sleep(RETRY_SLEEP);
            } catch (Exception x) {
                Log.fine("Exception: " + x.getMessage());
                x.printStackTrace();
                break;
            }
        return null;
    }


    protected <T> Result<T> toJavaResult(Response r, Class<T> entityType) {
        try {
            var status = r.getStatusInfo().toEnum();
            if (status == Response.Status.OK && r.hasEntity())
                return ok(r.readEntity(entityType));
            else
            if( status == Status.NO_CONTENT) return ok();

            return error(getErrorCodeFrom(status.getStatusCode()));
        } finally {
            r.close();
        }
    }

    public static Result.ErrorCode getErrorCodeFrom(int status) {
        return switch (status) {
            case 200, 209 -> ErrorCode.OK;
            case 409 -> ErrorCode.CONFLICT;
            case 403 -> ErrorCode.FORBIDDEN;
            case 404 -> ErrorCode.NOT_FOUND;
            case 400 -> ErrorCode.BAD_REQUEST;
            case 500 -> ErrorCode.INTERNAL_ERROR;
            case 501 -> ErrorCode.NOT_IMPLEMENTED;
            default -> ErrorCode.INTERNAL_ERROR;
        };
    }

    @Override
    public String toString() {
        return serverURI.toString();
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException x) { // nothing to do...
        }
    }
}
