package sd2223.trab1.servers.rest.users;

import jakarta.inject.Singleton;
import sd2223.trab1.api.User;
import sd2223.trab1.api.java.Users;
import sd2223.trab1.api.rest.UsersService;
import sd2223.trab1.servers.java.JavaUsers;
import sd2223.trab1.servers.rest.RestResource;

import java.util.List;

@Singleton
public class RESTUserResource extends RestResource implements UsersService {

    final Users impl;
    public RESTUserResource() {
        this.impl = new JavaUsers();
    }

    @Override
    public String createUser(User user) {
        return super.fromJavaResult( impl.createUser( user));
    }

    @Override
    public User getUser(String name, String pwd) {
        return super.fromJavaResult( impl.getUser(name, pwd));
    }

    @Override
    public User updateUser(String name, String pwd, User user) {
        return super.fromJavaResult(impl.updateUser(name, pwd, user));
    }

    @Override
    public User deleteUser(String name, String pwd) {
        return super.fromJavaResult(impl.deleteUser(name, pwd));
    }

    @Override
    public List<User> searchUsers(String pattern) {
        return super.fromJavaResult(impl.searchUsers(pattern));
    }
}
