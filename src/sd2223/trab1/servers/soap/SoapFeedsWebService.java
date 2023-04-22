package sd2223.trab1.servers.soap;

import jakarta.inject.Singleton;
import jakarta.jws.WebService;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.api.soap.FeedsException;
import sd2223.trab1.api.soap.FeedsService;
import sd2223.trab1.servers.java.JavaFeeds;

import java.util.List;

@Singleton
@WebService(serviceName= FeedsService.NAME, targetNamespace=FeedsService.NAMESPACE, endpointInterface=FeedsService.INTERFACE)
public class SoapFeedsWebService extends SoapWebService<FeedsException> implements FeedsService {

    final Feeds impl;

    public SoapFeedsWebService(String domain, int base){
        super(result-> new FeedsException(result.error().toString()));
        this.impl = new JavaFeeds(domain, base);
    }

    @Override
    public long postMessage(String user, String pwd, Message msg) throws FeedsException {
        return super.fromJavaResult(impl.postMessage(user, pwd, msg));
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) throws FeedsException {
        super.fromJavaResult(impl.removeFromPersonalFeed(user, mid, pwd));
    }

    @Override
    public Message getMessage(String user, long mid) throws FeedsException {
        return super.fromJavaResult(impl.getMessage(user, mid));
    }

    @Override
    public List<Message> getMessages(String user, long time) throws FeedsException {
        return super.fromJavaResult(impl.getMessages(user, time));
    }

    @Override
    public void subUser(String user, String userSub, String pwd) throws FeedsException {
        super.fromJavaResult(impl.subUser(user, userSub, pwd));
    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) throws FeedsException {
        super.fromJavaResult(impl.unsubscribeUser(user, userSub, pwd));
    }

    @Override
    public List<String> listSubs(String user) throws FeedsException {
        return super.fromJavaResult(impl.listSubs(user));
    }

    @Override
    public void deleteFeed(String name) throws FeedsException{
        super.fromJavaResult(impl.deleteFeed(name));
    }

    @Override
    public void updateFeedSubs(String user, Message msg) throws FeedsException{
        super.fromJavaResult(impl.updateFeedSubs(user, msg));
    }

    @Override
    public void addSubscriber(String user, String sub) throws FeedsException{
        super.fromJavaResult(impl.addSubscriber(user, sub));
    }

    @Override
    public void removeSubscriber(String user, String sub) throws FeedsException{
        super.fromJavaResult(impl.removeSubscriber(user, sub));
    }
}
