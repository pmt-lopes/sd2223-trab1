package sd2223.trab1.servers.java;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import sd2223.trab1.api.Discovery;
import sd2223.trab1.api.User;
import sd2223.trab1.api.java.Result;
import sd2223.trab1.api.java.Users;
import sd2223.trab1.clients.FeedsClientFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class JavaUsers implements Users {

    private final Map<String,User> users = new ConcurrentHashMap<>();

    private static final Logger Log = Logger.getLogger(JavaUsers.class.getName());
    Discovery discovery = Discovery.getInstance();
    String domain;

    public JavaUsers(String domain){
        this.domain = domain;
    }

    @Override
    public Result<String> createUser(User user) {
        Log.info("createUser : " + user);

        // Check if user data is valid
        if(user.getName() == null || user.getPwd() == null || user.getDisplayName() == null || user.getDomain() == null) {
            Log.info("User object invalid.");
            throw new WebApplicationException( Response.Status.BAD_REQUEST );
        }

        // Insert user, checking if name already exists
        if( users.putIfAbsent(user.getName(), user) != null ) {
            Log.info("User already exists.");
            throw new WebApplicationException( Response.Status.CONFLICT );
        }

        return Result.ok(user.getName() + "@" + user.getDomain());
    }

    @Override
    public Result<User> getUser(String name, String pwd) {
        Log.info("getUser : user = " + name + "; pwd = " + pwd);

        // Check if user is valid
        if(name == null || pwd == null) {
            Log.info("Name or Password null.");
            throw new WebApplicationException( Response.Status.BAD_REQUEST );
        }

        User user = users.get(name);
        // Check if user exists
        if( user == null ) {
            Log.info("User does not exist.");
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }

        //Check if the password is correct
        if( !user.getPwd().equals( pwd)) {
            Log.info("Password is incorrect.");
            throw new WebApplicationException( Response.Status.FORBIDDEN );
        }

        return Result.ok(user);
    }

    @Override
    public Result<User> updateUser(String name, String pwd, User user) {
        Log.info("updateUser : name = " + name + "; pwd = " + pwd + " ; user = " + user);

        // Check if parameters are valid
        if(!name.equals(user.getName())) {
            Log.info("Null parameter.");
            throw new WebApplicationException( Response.Status.BAD_REQUEST );
        }

        var u = users.get(name);

        // Check if user exists
        if( u == null ) {
            Log.info("User does not exist.");
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }

        // Check if password is correct
        if( !u.getPwd().equals(pwd)) {
            Log.info("Password is incorrect.");
            throw new WebApplicationException( Response.Status.FORBIDDEN );
        }
        if(user.getPwd()==null)
            user.setPwd(u.getPwd());
        if(user.getDomain()==null)
            user.setDomain(u.getDomain());
        if(user.getDisplayName()==null)
            user.setDisplayName(u.getDisplayName());
        // Update user
        users.put(name, user);

        return Result.ok(user);
    }

    @Override
    public Result<User> deleteUser(String name, String pwd) {
        Log.info("deleteUser : name = " + name + "; pwd = " + pwd);

        // Check if user is valid
        if(name == null || pwd == null) {
            Log.info("UserId or password null.");
            throw new WebApplicationException( Response.Status.BAD_REQUEST );
        }

        var user = users.get(name);

        // Check if user exists
        if( user == null ) {
            Log.info("User does not exist.");
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }

        // Check if the password is correct
        if( !user.getPwd().equals(pwd)) {
            Log.info("Password is incorrect.");
            throw new WebApplicationException( Response.Status.FORBIDDEN );
        }

        // Delete user
        users.remove(name);

        // Delete user feed
        URI uri = discovery.knownUrisOf(domain + ":feeds", 1)[0];
        var client = FeedsClientFactory.get(uri);
        client.deleteFeed(name);

        return Result.ok(user);
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {
        Log.info("searchUsers : pattern = " + pattern);

        String p;

        // In case of null pattern, return all users
        if(pattern == null) p = "";
        else p = pattern;

        List<User> u = new ArrayList<>();

        // Iterate Map entries
        for (Map.Entry<String, User> entry: users.entrySet()) {

            // Check pattern
            if(entry.getKey().contains(p)) u.add(entry.getValue());
        }

        return Result.ok(u);
    }

    @Override
    public Result<Boolean> hasUser(String name) {
        Log.info("hasUser: " + name);

        if( name == null ) {
            Log.info("User does not exist.");
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }

        Boolean result = users.containsKey(name);

        return Result.ok(result);
    }
}
