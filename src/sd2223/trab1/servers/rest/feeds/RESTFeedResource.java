package sd2223.trab1.servers.rest.feeds;

import sd2223.trab1.api.Message;
import sd2223.trab1.api.User;
import sd2223.trab1.api.rest.FeedsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class RESTFeedResource implements FeedsService {

    private static Logger Log = Logger.getLogger(RESTFeedResource.class.getName());

    // Feeds data structure
    private final Map<String, Set<Message>> feeds = new HashMap();

    // Follow data structure
    private final Map<String, Set<User>> follows = new HashMap();

    public RESTFeedResource() {}

    @Override
    public long postMessage(String user, String pwd, Message msg) {

        // validate user

        // post message in personal feed

        // send to other domains?

        return 0;
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {

    }

    @Override
    public Message getMessage(String user, long mid) {
        return null;
    }

    @Override
    public List<Message> getMessages(String user, long time) {
        return null;
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {

    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {

    }

    @Override
    public List<String> listSubs(String user) {
        return null;
    }
}
