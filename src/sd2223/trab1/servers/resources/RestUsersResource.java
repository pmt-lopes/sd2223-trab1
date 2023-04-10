package sd2223.trab1.servers.resources;

import jakarta.ws.rs.WebApplicationException;
import sd2223.trab1.api.User;
import sd2223.trab1.api.rest.UsersService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import jakarta.ws.rs.core.Response.Status;

public class RestUsersResource implements UsersService {

    private final Map<String,User> users = new HashMap<>();

    private static Logger Log = Logger.getLogger(RestUsersResource.class.getName());

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

        return user.getName();
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

        // Check if parameters are valid
        if(name == null || pwd == null || user == null) {
            Log.info("Null parameter.");
            throw new WebApplicationException( Status.BAD_REQUEST );
        }

        // Check if user is valid
        if(user.getName() == null || user.getPwd() == null || user.getDisplayName() == null || user.getDomain() == null) {
            Log.info("User object invalid.");
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

            // Check pattern, case-insensitive
            if(entry.getKey().toUpperCase().contains(p.toUpperCase())){
                User user = entry.getValue();

                // Pwd set to empty string
                user.setPwd("");
                u.add(user);
            }
        }

        return u;
    }
}
