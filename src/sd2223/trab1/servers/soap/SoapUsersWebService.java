package sd2223.trab1.servers.soap;

import jakarta.inject.Singleton;
import jakarta.jws.WebService;
import sd2223.trab1.api.User;
import sd2223.trab1.api.java.Users;
import sd2223.trab1.api.soap.FeedsService;
import sd2223.trab1.api.soap.UsersException;
import sd2223.trab1.api.soap.UsersService;
import sd2223.trab1.servers.java.JavaUsers;

import java.util.List;
import java.util.logging.Logger;

@Singleton
@WebService(serviceName= UsersService.NAME, targetNamespace=UsersService.NAMESPACE, endpointInterface=UsersService.INTERFACE)
public class SoapUsersWebService extends SoapWebService<UsersException> implements UsersService {

    final Users impl;
    private static final Logger Log = Logger.getLogger(SoapUsersWebService.class.getName());

    public SoapUsersWebService(String domain) {
        super( (result)-> new UsersException( result.error().toString()));
        this.impl = new JavaUsers(domain);
    }

    @Override
    public String createUser(User user) throws UsersException {
        Log.info("CreateUser");
        return super.fromJavaResult( impl.createUser(user));
    }

    @Override
    public User getUser(String name, String pwd) throws UsersException {
        Log.info("getUser");
        return super.fromJavaResult( impl.getUser(name, pwd));
    }

    @Override
    public User updateUser(String name, String pwd, User user) throws UsersException {
        Log.info("updateUser");
        return super.fromJavaResult( impl.updateUser(name, pwd, user));
    }

    @Override
    public User deleteUser(String name, String pwd) throws UsersException {
        Log.info("DeleteUser");
        return super.fromJavaResult( impl.deleteUser(name, pwd));
    }

    @Override
    public List<User> searchUsers(String pattern) throws UsersException {
        Log.info("searchUsers");
        return super.fromJavaResult( impl.searchUsers(pattern));
    }

    @Override
    public Boolean hasUser(String user) throws UsersException {
        return super.fromJavaResult( impl.hasUser(user) );
    }
}
