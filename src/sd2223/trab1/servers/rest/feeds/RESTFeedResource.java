package sd2223.trab1.servers.rest.feeds;

import jakarta.inject.Singleton;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.api.rest.FeedsService;
import sd2223.trab1.servers.java.JavaFeeds;
import sd2223.trab1.servers.rest.RestResource;

import java.util.List;

@Singleton
public class RESTFeedResource extends RestResource implements FeedsService {

    final Feeds impl;

    public RESTFeedResource(String domain, int base) {
        this.impl = new JavaFeeds(domain, base);
    }

    @Override
    public long postMessage(String user, String pwd, Message msg) {
        return super.fromJavaResult(impl.postMessage( user, pwd, msg));
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {
        super.fromJavaResult(impl.removeFromPersonalFeed(user, mid, pwd));
    }

    @Override
    public Message getMessage(String user, long mid) {
        return super.fromJavaResult(impl.getMessage(user, mid));
    }

    @Override
    public List<Message> getMessages(String user, long time) {
        return super.fromJavaResult(impl.getMessages(user, time));
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {
        super.fromJavaResult(impl.subUser(user, userSub, pwd));
    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {
        super.fromJavaResult(impl.unsubscribeUser(user, userSub, pwd));
    }

    @Override
    public List<String> listSubs(String user) {
        return super.fromJavaResult(impl.listSubs(user));
    }

    @Override
    public void deleteFeed(String name, String pwd) {
        super.fromJavaResult(impl.deleteFeed(name, pwd));
    }

    @Override
    public void updateFeedSubs(String user, Message msg) {
        super.fromJavaResult(impl.updateFeedSubs(user, msg));
    }
}
