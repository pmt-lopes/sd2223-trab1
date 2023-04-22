package sd2223.trab1.servers.java;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import sd2223.trab1.api.Discovery;
import sd2223.trab1.api.Feed;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.User;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.api.java.Result;
import sd2223.trab1.clients.FeedsClientFactory;
import sd2223.trab1.clients.UsersClientFactory;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class JavaFeeds implements Feeds {

    private static final Logger Log = Logger.getLogger(JavaFeeds.class.getName());

    private final Map<String, Feed> feeds = new ConcurrentHashMap<>();
    private int num_seq;
    private final String domain;
    private final int base;
    Discovery discovery = Discovery.getInstance();

    public JavaFeeds(String domain, int base){
        num_seq = 0;
        this.domain = domain;
        this.base = base;
    }

    // May need to synchronise message post
    @Override
    public synchronized Result<Long> postMessage(String user, String pwd, Message message) {
        Log.info("postMessage attempt by " + user);

        if(user==null || pwd == null || message == null){
            Log.info("Null parameter.");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        String[] userAux = user.split("@");
        String userName = userAux[0];

        if (!userAux[1].equals(domain)) {
            Log.info("Incorrect domain");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        if(!userExists(userName, userAux[1])){
            Log.info("User does not exist");
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        var result = getUser(userName, userAux[1], pwd);

        if(result == null){
            Log.info("Incorrect password.");
            throw new WebApplicationException(Status.FORBIDDEN);
        }

        var feed = feeds.get(userName);

        if(feed == null){
            feed = new Feed();
            feeds.put(userName, feed);
        }

        message.setId(num_seq * 256L + base);
        num_seq++;
        feed.postMessage(message);

        if(!feed.getFollowers().isEmpty())
            propagateMessage(feed.getFollowers(), user, message);

        return Result.ok(message.getId());
    }

    @Override
    public synchronized Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {
        Log.info("postMessage attempt by " + user);

        if(user == null || pwd == null){
            Log.info("Null parameter.");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        String[] userAux = user.split("@");
        String userName = userAux[0];

        if (!userAux[1].equals(domain)) {
            Log.info("Incorrect domain");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        if(!userExists(userName, userAux[1])){
            Log.info("User does not exist");
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        var result = getUser(userName, userAux[1], pwd);

        if(result == null){
            Log.info("Incorrect password.");
            throw new WebApplicationException(Status.FORBIDDEN);
        }

        Feed feed = feeds.get(userName);

        if(feed == null){
            Log.info("No user feed.");
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        if(feed.getMessage(mid) == null){
            Log.info("No message with id " + mid);
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        feed.removeMessage(mid);

        return Result.ok();
    }

    @Override
    public synchronized Result<Message> getMessage(String user, long mid) {
        Log.info("getMessage from user " + user);

        if(user == null){
            Log.info("Null user.");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        String[] userAux = user.split("@");
        String userName = userAux[0];

        // Check if user is remote
        if(!domain.equals(userAux[1])){
            URI uri = discovery.knownUrisOf(userAux[1] + ":feeds",1)[0];
            var client = FeedsClientFactory.get(uri);
            return client.getMessage(user, mid);
        }

        if(!userExists(userName, userAux[1])){
            Log.info("User does not exist");
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        Feed feed = feeds.get(userName);
        if (feed == null) {
            Log.info("Feed does not exist.");
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        Message msg = feed.getMessage(mid);
        if(msg == null) {
            Log.info("Message does not exist.");
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return Result.ok(msg);
    }

    @Override
    public synchronized Result<List<Message>> getMessages(String user, long time) {
        Log.info("getMessages from user " + user);

        if(user == null){
            Log.info("Null user.");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        String[] userAux = user.split("@");
        String userName = userAux[0];

        // Check if user is remote
        if(!domain.equals(userAux[1])){
            URI uri = discovery.knownUrisOf(userAux[1] + ":feeds",1)[0];
            var client = FeedsClientFactory.get(uri);
            return client.getMessages(user, time);
        }

        var result = userExists(userName, userAux[1]);

        if(!result){
            Log.info("User does not exist");
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        Feed feed = feeds.get(userName);
        if (feed == null) {
            feed = new Feed();
            feeds.put(userName, feed);
        }

        return Result.ok(feed.getMessages(time));
    }

    @Override
    public synchronized Result<Void> subUser(String user, String userSub, String pwd) {
        Log.info(user + " is subscribing to " + userSub + "\n");

        if(user == null || userSub == null || pwd == null){
            Log.info("Null parameter.");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        String[] userAux = user.split("@");
        String[] userSubAux = userSub.split("@");
        String userName = userAux[0];
        String userSubName = userSubAux[0];

        var result1 = userExists(userName, userAux[1]);
        var result2 = userExists(userSubName, userSubAux[1]);

        if(!result1 || !result2){
            Log.info("One of the users does not exist");
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        var resultGet = getUser(userName, userAux[1], pwd);
        if(resultGet == null){
            Log.info("Password is incorrect.");
            throw new WebApplicationException(Status.FORBIDDEN);
        }

        synchronized (this) {

            Feed userFeed = feeds.get(userName);
            if (userFeed == null) {
                userFeed = new Feed();
                feeds.put(userName, userFeed);
            }
            userFeed.subUser(userSub);

            Log.info(user + " follows: " + userFeed.getUserSubs().toString() + "\n");

            // remote userSub
            if (!domain.equals(userSubAux[1])) {
                Log.info("Sending addSubscriber request to " + userSub);
                URI uri = discovery.knownUrisOf(userSubAux[1] + ":feeds", 1)[0];
                var client = FeedsClientFactory.get(uri);
                return client.addSubscriber(userSub, user);
            }
            else {
                Feed userSubFeed = feeds.get(userSubName);
                if (userSubFeed == null) {
                    userSubFeed = new Feed();
                    feeds.put(userSubName, userSubFeed);
                }
                userSubFeed.addFollower(user);


                Log.info("Added " + user + " to " + userSub + "'s followers list\n");

                Log.info(userSub + "'s subs: " + userSubFeed.getFollowers().toString() + "\n");
            }
        }
        return Result.ok();
    }

    @Override
    public synchronized Result<Void> unsubscribeUser(String user, String userSub, String pwd) {
        Log.info(user + " is unsubscribing from " + userSub + "\n");

        if(user == null || userSub == null || pwd == null){
            Log.info("Null parameter.");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        String[] userAux = user.split("@");
        String[] userSubAux = userSub.split("@");
        String userName = userAux[0];
        String userSubName = userSubAux[0];

        var result1 = userExists(userName, userAux[1]);
        var result2 = userExists(userSubName, userSubAux[1]);

        if(!result1 || !result2){
            Log.info("One of the users does not exist");
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        var resultGet = getUser(userName, userAux[1], pwd);
        if(resultGet == null){
            Log.info("Password is incorrect.");
            throw new WebApplicationException(Status.FORBIDDEN);
        }

        Feed userFeed = feeds.get(userName);
        if (userFeed == null) {
            userFeed = new Feed();
            feeds.put(userName, userFeed);
        }
        userFeed.unsubUser(userSub);

        // remote userSub
        if(!domain.equals(userSubAux[1])){
            Log.info("Sending removeSubscriber request to " + userSub);
            URI uri = discovery.knownUrisOf(userSubAux[1] + ":feeds",1)[0];
            var client = FeedsClientFactory.get(uri);
            return client.removeSubscriber(userSub, user);
        }

        Feed userSubFeed = feeds.get(userSubName);
        if (userSubFeed == null) {
            userSubFeed = new Feed();
            feeds.put(userSubName, userSubFeed);
        }
        userSubFeed.removeFollower(user);

        Log.info("Removed " + user + " from " + userSub + "'s followers list\n");

        return Result.ok();
    }

    @Override
    public synchronized Result<List<String>> listSubs(String user) {
        Log.info("Listing subscribed users of " + user + "\n");

        if(user == null){
            Log.info("Null user.");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        String[] userAux = user.split("@");
        String userName = userAux[0];
        boolean result = userExists(userName, userAux[1]);

        if(!result){
            Log.info("User does not exist");
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        Feed feed = feeds.get(userName);
        if (feed == null){
            feed = new Feed();
            feeds.put(userName, feed);
        }

        return Result.ok(feed.getUserSubs());
    }

    // Function is called after user is deleted
    // Request from user service in the same domain
    @Override
    public synchronized Result<Void> deleteFeed(String user) {
        Log.info("Deleting feed from user " + user + "\n");

        feeds.remove(user);

        return Result.ok();
    }

    // Function is called after user posts message
    // Request from another domain
    @Override
    public synchronized Result<Void> updateFeedSubs(String user, Message msg){
        Log.info("Propagating message from user " + user + "\n");

        List<Feed> list = new ArrayList<>(feeds.values());
        for(Feed feed: list){
            if(feed.getUserSubs().contains(user))
                feed.postMessage(msg);
        }

        return Result.ok();
    }

    //Function is called after sub subscribes to user
    // Request from another domain
    @Override
    public synchronized Result<Void> addSubscriber(String user, String sub) {

        String[] userAux = user.split("@");

        synchronized (this) {
            Feed userFeed = feeds.get(userAux[0]);
            if (userFeed == null) {
                userFeed = new Feed();
                feeds.put(userAux[0], userFeed);
            }
            userFeed.addFollower(sub);

            Log.info("Added " + sub + " to " + user + "'s followers list\n");
            Log.info(user + "'s subs: " + userFeed.getFollowers().toString() + "\n");
        }

        return Result.ok();
    }

    //Function is called after sub unsubscribes to user
    // Request from another domain
    @Override
    public synchronized Result<Void> removeSubscriber(String user, String sub) {

        String[] userAux = user.split("@");

        Feed userFeed = feeds.get(userAux[0]);
        if (userFeed == null) {
            userFeed = new Feed();
            feeds.put(userAux[0], userFeed);
        }
        userFeed.removeFollower(sub);

        Log.info("Removed " + sub + " to " + user + "'s followers list\n");

        return Result.ok();
    }

    private User getUser(String name, String domain, String pwd){
        String serviceName = domain + ":users";

        URI[] uris = discovery.knownUrisOf(serviceName, 1);

        var client = UsersClientFactory.get(uris[0]);
        var result = client.getUser(name, pwd);

        if(result == null){
            return null;
        }

        return result.value();
    }

    private boolean userExists(String name, String domain){
        String serviceName = domain + ":users";

        URI[] uris = discovery.knownUrisOf(serviceName, 1);

        var client = UsersClientFactory.get(uris[0]);
        var result = client.hasUser(name);

        return result.value();
    }

    private void propagateMessage(List<String> subs, String user, Message msg){
        boolean hasLocalFollower = false;
        Set<String> domains = new HashSet<>();

        Log.info(user + "'s subs: " + subs.toString());

        // Get remote domains
        for(String sub: subs){
            String[] subAux = sub.split("@");
            if(!domain.equals(subAux[1])){
                domains.add(subAux[1]);
            }
            else{
                hasLocalFollower = true;
            }
        }

        // Send to remote domains, 1 operation per domain
        for(String domain: domains){
            URI uri = discovery.knownUrisOf(domain + ":feeds",1)[0];
            Log.info("Propagating " + user + "'s message to " + domain);
            var client = FeedsClientFactory.get(uri);
            client.updateFeedSubs(user, msg);
        }
        // send to local users
        if(hasLocalFollower)
            this.updateFeedSubs(user, msg);
    }

}
