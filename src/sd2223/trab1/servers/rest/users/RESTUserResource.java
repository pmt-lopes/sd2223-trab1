package sd2223.trab1.servers.rest.users;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import sd2223.trab1.api.User;
import sd2223.trab1.api.rest.UsersService;

import java.util.*;
import java.util.logging.Logger;

@Singleton
public class RESTUserResource implements UsersService {

    private final Map<String,User> users = new HashMap<>();

    private static Logger Log = Logger.getLogger(RESTUserResource.class.getName());
    @Singleton
    public RESTUserResource() {}


    @Override
    public String createUser(User user) {
        Log.info("createUser : " + user);

        // Check if user data is valid
        if(user.getName() == null || user.getPwd() == null || user.getDisplayName() == null || user.getDomain() == null) {
            Log.info("User object invalid.");
            throw new WebApplicationException( Status.BAD_REQUEST );
        }

        // Insert user, checking if name already exists
        if( users.putIfAbsent(user.getName(), user) != null ) {
            Log.info("User already exists.");
            throw new WebApplicationException( Status.CONFLICT );
        }

        return user.getName() + "@" + user.getDomain();
    }

    @Override
    public User getUser(String name, String pwd) {
        Log.info("getUser : user = " + name + "; pwd = " + pwd);

        // Check if user is valid
        if(name == null || pwd == null) {
            Log.info("Name or Password null.");
            throw new WebApplicationException( Status.BAD_REQUEST );
        }

        User user = users.get(name);
        // Check if user exists
        if( user == null ) {
            Log.info("User does not exist.");
            throw new WebApplicationException( Status.NOT_FOUND );
        }

        //Check if the password is correct
        if( !user.getPwd().equals( pwd)) {
            Log.info("Password is incorrect.");
            throw new WebApplicationException( Status.FORBIDDEN );
        }

        return user;
    }

    @Override
    public User updateUser(String name, String pwd, User user) {
        Log.info("updateUser : name = " + name + "; pwd = " + pwd + " ; user = " + user);
        // TODO Complete method

        // Check if parameters are valid
        if(!name.equals(user.getName())) {
            Log.info("Null parameter.");
            throw new WebApplicationException( Status.BAD_REQUEST );
        }

        var u = users.get(name);

        // Check if user exists
        if( u == null ) {
            Log.info("User does not exist.");
            throw new WebApplicationException( Status.NOT_FOUND );
        }

        // Check if password is correct
        if( !u.getPwd().equals(pwd)) {
            Log.info("Password is incorrect.");
            throw new WebApplicationException( Status.FORBIDDEN );
        }
        if(user.getPwd()==null)
            user.setPwd(u.getPwd());
        if(user.getDomain()==null)
            user.setDomain(u.getDomain());
        if(user.getDisplayName()==null)
            user.setDisplayName(u.getDisplayName());
        // Update user
        users.put(name, user);

        return user;
    }

    @Override
    public User deleteUser(String name, String pwd) {
        Log.info("deleteUser : name = " + name + "; pwd = " + pwd);

        // Check if user is valid
        if(name == null || pwd == null) {
            Log.info("UserId or password null.");
            throw new WebApplicationException( Status.BAD_REQUEST );
        }

        var user = users.get(name);

        // Check if user exists
        if( user == null ) {
            Log.info("User does not exist.");
            throw new WebApplicationException( Status.NOT_FOUND );
        }

        // Check if the password is correct
        if( !user.getPwd().equals(pwd)) {
            Log.info("Password is incorrect.");
            throw new WebApplicationException( Status.FORBIDDEN );
        }

        // Delete user
        users.remove(name);

        return user;
    }

    @Override
    public List<User> searchUsers(String pattern) {
        Log.info("searchUsers : pattern = " + pattern);

        String p;

        // In case of null pattern, return all users
        if(pattern == null) p = "";
        else p = pattern;

        List<User> u = new ArrayList<User>();

        // Iterate Map entries
        for (Map.Entry<String, User> entry: users.entrySet()) {

            // Check prefix
            if(entry.getKey().contains(p)) u.add(entry.getValue());
        }

        return u;
    }
}
