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
        String userAux = user.split("@")[1];
        // validate user
        if(userAux==null||pwd == null)
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        URI[] uri = discovery.knownUrisOf(RESTUsersServer.SERVICE, 1);
        WebTarget target = client.target(uri[0]).path(UsersService.PATH);
        Response r = target.path(userAux)
                .queryParam(UsersService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
        User us =r.readEntity(User.class);
        if(!follows.containsKey(userAux)|| !us.getPwd().equals(pwd) )
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        if(!us.getPwd().equals(pwd))
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        // post message in personal feed
        feeds.get(user).add(msg);
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
        List<Message> l =new ArrayList<Message>();
       for( Message m : feeds.get(user))
           if (m.getCreationTime()<time)
               l.add(m);
        return l;
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {
        String[] userAux = userSub.split("@");
        URI[] uri = discovery.knownUrisOf(RESTUsersServer.SERVICE, 1);
        WebTarget target = client.target(uri[0]).path(UsersService.PATH);
        Response r = target.path(userAux[1])
                .queryParam(UsersService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
        User us =r.readEntity(User.class);
    if(us == null|| !follows.containsKey(user))
        throw new WebApplicationException(Response.Status.NOT_FOUND);

    if(!us.getPwd().equals(pwd))
        throw new WebApplicationException(Response.Status.FORBIDDEN);
    follows.get(userSub).add(new User(us.getName(), us.getPwd(), us.getDomain(), us.getDisplayName()));
    }


    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {
        String[] userAux = userSub.split("@");
        URI[] uri = discovery.knownUrisOf(RESTUsersServer.SERVICE, 1);
        WebTarget target = client.target(uri[0]).path(UsersService.PATH);
        Response r = target.path(userAux[1])
                .queryParam(UsersService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
        User us =r.readEntity(User.class);
        if(!follows.containsKey(user)|| !follows.get(user).contains(us))
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        if (!pwd.equals(us.getPwd())){
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
            follows.get(user).remove(us);


    }

    @Override
    public List<String> listSubs(String user) {
        if(!follows.containsKey(user))
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        List<String> l = new ArrayList<>();
        for(User u: follows.get(user))
            l.add(u.getName());
       return l;
    }
}
