package sd2223.trab1.servers.resources;

import sd2223.trab1.api.User;
import sd2223.trab1.api.rest.UsersService;

import java.util.List;

public class RestUsersResource implements UsersService {

    @Override
    public String createUser(User user) {
        return null;
    }

    @Override
    public User getUser(String name, String pwd) {
        return null;
    }

    @Override
    public User updateUser(String name, String pwd, User user) {
        return null;
    }

    @Override
    public User deleteUser(String name, String pwd) {
        return null;
    }

    @Override
    public List<User> searchUsers(String pattern) {
        return null;
    }
}
