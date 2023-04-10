package sd2223.trab1.servers.rest.feeds;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import sd2223.trab1.api.Discovery;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.User;
import sd2223.trab1.api.rest.FeedsService;
import sd2223.trab1.api.rest.UsersService;
import sd2223.trab1.servers.rest.users.RESTUserResource;
import sd2223.trab1.servers.rest.users.RESTUsersServer;


import java.net.URI;
import java.util.*;
import java.util.logging.Logger;

public class RESTFeedResource implements FeedsService {

    private static Logger Log = Logger.getLogger(RESTFeedResource.class.getName());

    // Feeds data structure
    private final Map<String, Set<Message>> feeds = new HashMap();

    // Follow data structure
    private final Map<String, Set<User>> follows = new HashMap();
    Discovery discovery = Discovery.getInstance();
    private ClientConfig config = new ClientConfig();
    private Client client = ClientBuilder.newClient(config);
    public RESTFeedResource() {}

    @Override
    public long postMessage(String user, String pwd, Message msg) {

        // validate user
        if(user==null||pwd == null)
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        String[] userAux = user.split("@");
        URI[] uri = discovery.knownUrisOf(RESTUsersServer.SERVICE, 1);
        WebTarget target = client.target(uri[0]).path(UsersService.PATH);
        Response r = target.path(userAux[1])
                .queryParam(UsersService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
        User us =r.readEntity(User.class);
        if(!follows.containsKey(user)|| !us.getPwd().equals(pwd) )
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        if(!us.getPwd().equals(pwd))
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        // post message in personal feed
        feeds.get(user).add(msg);
        /*TODO*/
        // send to other domains?

        return msg.getId();
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {
        String[] userAux = user.split("@");
        URI[] uri = discovery.knownUrisOf(RESTUsersServer.SERVICE, 1);
        WebTarget target = client.target(uri[0]).path(UsersService.PATH);
        Response r = target.path(userAux[1])
                .queryParam(UsersService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
        User us =r.readEntity(User.class);
        if (!us.getPwd().equals(pwd))
            throw new WebApplicationException( Response.Status.NOT_FOUND);
        Set<Message> m = feeds.get(user);
        for (Message message : m) {
            if(message.getId()== mid){
               feeds.remove(message);
               return;
            }
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @Override
    public Message getMessage(String user, long mid) {

        //TODO May be a remote user
        if(!feeds.containsKey(user))
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        Set<Message> m = feeds.get(user);
        for (Message message : m) {
            if(message.getId()== mid){

                return message;
            }
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @Override
    public List<Message> getMessages(String user, long time) {
        //TODO May be a remote user
        if(!feeds.containsKey(user))
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        Set<Message> m = feeds.get(user);
        List<Message> l =new ArrayList<Message>(m);
        return l;
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {
    if(user==null|| userSub==null||pwd == null)
        throw new WebApplicationException(Response.Status.BAD_REQUEST);

    if(!follows.containsKey(user))
        throw new WebApplicationException(Response.Status.FORBIDDEN);
    follows.get(userSub).add(new User(user, pwd, "", user));
    }


    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {

    }

    @Override
    public List<String> listSubs(String user) {
        return null;
    }
}
