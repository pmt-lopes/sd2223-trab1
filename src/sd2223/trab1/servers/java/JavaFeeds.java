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
    Discovery discovery = Discovery.getInstance();
    private ClientConfig config = new ClientConfig();
    private Client client = ClientBuilder.newClient(config);

    public JavaFeeds(){
        num_seq = 0;
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

        RestUsersClient client = new RestUsersClient(uri);

        User us =(User) client.getUser(userAux[0], pwd);

        if(!follows.containsKey(user)|| !us.getPwd().equals(pwd) )
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        if(!us.getPwd().equals(pwd))
            throw new WebApplicationException(Response.Status.FORBIDDEN);

        // post message in personal feed
        message.setId(num_seq++*256);

        Map<Long, Message> feed = feeds.get(user);
        if(feed == null)
            feed = new HashMap<>();

        feed.put(message.getId(), message);
        feeds.put(user, feed);


        return Result.ok(message.getId());
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
    public Result<List<Message>> getMessagesFromRemote(String user, String originalDomain, long time) {
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
