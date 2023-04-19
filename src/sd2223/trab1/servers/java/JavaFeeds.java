package sd2223.trab1.servers.java;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import sd2223.trab1.api.Discovery;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.User;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.api.java.Result;
import sd2223.trab1.clients.UsersClientFactory;
import sd2223.trab1.clients.rest.RestUsersClient;
import sd2223.trab1.servers.rest.feeds.RESTFeedResource;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class JavaFeeds implements Feeds {

    private static Logger Log = Logger.getLogger(JavaFeeds.class.getName());

    // Feeds data structure
    private final Map<String, Map<Long, Message>> feeds = new HashMap();

    // Follow data structure
    private final Map<String, Set<User>> follows = new HashMap();
    private int num_seq;
    private String domain;
    private int base;
    Discovery discovery = Discovery.getInstance();
    private ClientConfig config = new ClientConfig();
    private Client client = ClientBuilder.newClient(config);

    public JavaFeeds(String domain, int base){
        num_seq = 0;
        this.domain = domain;
        this.base = base;
    }

    @Override
    public Result<Long> postMessage(String user, String pwd, Message message) {
        Log.info("postMessage attempt by " + user);

        // validate user
        if(user==null || pwd == null || message == null){
            Log.info("Null parameter.");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        String[] userAux = user.split("@");

        String serviceName = userAux[1] + ":users";

        URI uri = discovery.knownUrisOf(serviceName, 1)[0];

        var client = UsersClientFactory.get(uri);
        var userResult = client.getUser(userAux[0], pwd);

        if (!userResult.isOK()){
            return Result.error(userResult.error());
        }

        var feed = feeds.get(user);

        if(feed == null){
            feed = new HashMap<>();
        }

        message.setId(num_seq * 256);


        return null;
    }

    @Override
    public Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {
        return null;
    }

    @Override
    public Result<Message> getMessage(String user, long mid) {
        return null;
    }

    @Override
    public Result<List<Message>> getMessages(String user, long time) {
        return null;
    }

    @Override
    public Result<Void> subUser(String user, String userSub, String pwd) {
        return null;
    }

    @Override
    public Result<Void> unsubscribeUser(String user, String userSub, String pwd) {
        return null;
    }

    @Override
    public Result<List<String>> listSubs(String user) {
        return null;
    }

    @Override
    public Result<Void> deleteFeed(String user, String pwd) {
        return null;
    }
}
