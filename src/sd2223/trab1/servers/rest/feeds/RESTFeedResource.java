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
    /*Esta info foi adicionado ao enunciado do trabalho, ms fica aqui também,
    com um pouco mais de detalhe: a partir da versão 4.0
    foi adicionado um segundo parâmetro quando se arranca o servidor
    (args[1]) - esse parâmetro é um número inteiro, diferente para cada servidor.
    Podem usar este parâmetro para garantir que os identificadores das mensagens geradas
    em diferentes servidores é unico - e.g. sendo base esse valor
    e num_seq um número de sequência num servidor,
    num_sql * 256 + base será um identificador único*/
    @Override
    public long postMessage(String user, String pwd, Message msg) {

        // validate user
        if(user==null||pwd == null)
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        String[] userAux = user.split("@");
        URI[] uri = discovery.knownUrisOf("users.", 1);
        WebTarget target = client.target(uri[0]).path(UsersService.PATH);
        Response r = target.path(userAux[0])
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


        return msg.getId();
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {
        String[] userAux = user.split("@");
        URI[] uri = discovery.knownUrisOf("users.", 1);
        WebTarget target = client.target(uri[0]).path(UsersService.PATH);
        Response r = target.path(userAux[0])
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
        String[] userAux = user.split("@");
        if(!feeds.containsKey(userAux[0]))
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        List<Message> l = new ArrayList<>();
        for(Message m : feeds.get(user))
            if(m.getCreationTime()<time)
                l.add(m);
        return l;
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {
        String[] userAux = user.split("@");
        URI[] uri = discovery.knownUrisOf("users.", 1);
        WebTarget target = client.target(uri[0]).path(UsersService.PATH);
        Response r = target.path(userSub)
                .queryParam(UsersService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
        User us =r.readEntity(User.class);
        if(!follows.containsKey(userAux[0])||us == null)
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        if(!us.getPwd().equals(pwd))
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        follows.get(userAux[0]).add(us);
    }


    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {
        String[] userAux = user.split("@");
        URI[] uri = discovery.knownUrisOf("users.", 1);
        WebTarget target = client.target(uri[0]).path(UsersService.PATH);
        Response r = target.path(userSub)
                .queryParam(UsersService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
        User us =r.readEntity(User.class);
        if(!follows.containsKey(userAux[0])||us == null)
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        if(!pwd.equals(us.getPwd()))
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        follows.get(userAux[0]).remove(us);
    }

    @Override
    public List<String> listSubs(String user) {
        String[] userAux = user.split("@");
        List<String> s = new ArrayList<String>();
        if(!follows.containsKey(userAux[0]))
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        for(User u : follows.get(userAux[0]))
            s.add(u.getName());
        return s;
    }
}
