package sd2223.trab1.clients.rest;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.rest.FeedsService;

import java.net.URI;
import java.util.List;

public class RestFeedsClient extends RestClient implements FeedsService {

    final WebTarget target;

    public RestFeedsClient(URI serverURI){
        super( serverURI );
        target = client.target( serverURI ).path( FeedsService.PATH );
    }

    private long clt_postMessage(String user, String pwd, Message msg){

        Response r = target.path(user)
                .queryParam(FeedsService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(msg, MediaType.APPLICATION_JSON));

        if(r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
            return r.readEntity(Long.class);
        else
            System.out.println("Error, HTTP error status: " + r.getStatus() );

        return -1;
    }

    private void clt_removeFromPersonalFeed(String user, long mid, String pwd){

        Response r = target.path(user)
                .path(Long.toString(mid))
                .queryParam(FeedsService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        if(r.getStatus() != Status.OK.getStatusCode())
            System.out.println("Error, HTTP error status: " + r.getStatus() );
    }

    private Message clt_getMessage(String user, long mid) {

        Response r = target.path(user)
                .path(Long.toString(mid))
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if(r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
            return r.readEntity(Message.class);
        else
            System.out.println("Error, HTTP error status: " + r.getStatus() );

        return null;
    }

    private List<Message> clt_getMessages(String user, long time) {

        Response r = target.path(user)
                .queryParam(FeedsService.TIME,time)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if(r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
            return r.readEntity(new GenericType<List<Message>>() {});
        else
            System.out.println("Error, HTTP error status: " + r.getStatus() );

        return null;
    }

    private void clt_subUser(String user, String userSub, String pwd) {

        Response r = target.path(user)
                .path(userSub)
                .queryParam(FeedsService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(userSub, MediaType.APPLICATION_JSON));

        if(r.getStatus() != Status.OK.getStatusCode())
            System.out.println("Error, HTTP error status: " + r.getStatus() );
    }

    private void clt_unsubscribeUser(String user, String userSub, String pwd) {

        Response r = target.path(user)
                .path(userSub)
                .queryParam(FeedsService.PWD, pwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        if(r.getStatus() != Status.OK.getStatusCode())
            System.out.println("Error, HTTP error status: " + r.getStatus() );
    }

    private List<String> clt_listSubs(String user) {

        Response r = target.path(user)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if(r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
            return r.readEntity(new GenericType<List<String>>() {});
        else
            System.out.println("Error, HTTP error status: " + r.getStatus() );

        return null;
    }

    @Override
    public long postMessage(String user, String pwd, Message msg) {
        return super.reTry( () -> clt_postMessage(user , pwd, msg));
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {
        super.voidreTry( () -> clt_removeFromPersonalFeed(user ,mid, pwd));
    }

    @Override
    public Message getMessage(String user, long mid) {
        return super.reTry( () -> clt_getMessage(user , mid));
    }

    @Override
    public List<Message> getMessages(String user, long time) {
        return super.reTry( () -> clt_getMessages(user , time));
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {
        super.voidreTry( () -> clt_subUser(user, userSub, pwd));
    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {
        super.voidreTry( () -> clt_unsubscribeUser(user, userSub, pwd));
    }

    @Override
    public List<String> listSubs(String user) {
        return super.reTry( () -> clt_listSubs(user));
    }
}
