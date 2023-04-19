package sd2223.trab1.api;

import java.util.*;

public class Feed {

    private Map<Long,Message> Messages;
    private List<String> Followers;
    private List<String> Following;

    public Feed(){
        Messages = new HashMap<Long,Message>();
        Followers = new ArrayList<String>();
        Following = new ArrayList<String>();
    }

    public void postMessage(Message m){
        Messages.put(m.getId(), m);
    }

    public void removeMessage(long mid){
        Messages.remove(mid);
    }

    public void subUser(String user){
        if(!Following.contains(user))
            Following.add(user);
    }

    public void unsubUser(String user) {
        Following.remove(user);
    }

    public void addFollower(String user) {
        if (!Followers.contains(user))
            Followers.add(user);
    }

    public void removeFollower(String user) {
        Followers.remove(user);
    }

    public Message getMessage(long mid) {
        return Messages.get(mid);
    }

    public List<Message> getMessages(long time){

        List<Message> msgs = new ArrayList<Message>();

        Messages.values().stream().forEach(m -> {
            if (m.getCreationTime() > time) {
                Message msg = new Message(m.getId(), m.getUser(), m.getDomain(), m.getText());
                msg.setCreationTime(m.getCreationTime());
                msgs.add(msg);
            }
        });

        return msgs;
    }

    public  List<String> getUserSubs() {
        return Following;
    }

    public List<String> getFollowers() {
        return Followers;
    }
}
