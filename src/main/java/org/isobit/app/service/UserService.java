package org.isobit.app.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.isobit.app.model.Role;
import org.isobit.app.model.User;
import org.isobit.directory.model.People;

public interface UserService  {

    //public List<User> load(int first, int pageSize, String sortField, Map<String, Object> filters);

    public boolean existsNameXorMail(String name, String mail);

    public User findByName(String username);

    public People getDrtPersonaNatural(int idDir);

    public User getCurrentUser();

    //public User initSessionByToken(String access_token);

    public List getUserScopeList(User u);
    

    public interface CrudModule<T> {

        public void afterEdit(T u, Object refer);

        public void afterDelete(T u);

    }

    public interface UserModule {

        public User login(String user, String pass, Map m);

        public User authenticateFinalize(User user);

        public User password(Map m);

        public void loadPerm(User user, List perms);

        public User logout(User user);

    }

    public int password(Map m) throws Exception;

    public Object passReset(int uid, long timestamp, String hashedPass, Map m);

    public void send(Map m);

    public User load(Object u);

    public void load();
    
    public static enum Perm {
        ADMIN_PERMISSIONS,
        ACCESS_USER_PROFILES,
        ADMIN_USERS,
        CHANGE_OWN_USERNAME
    };

    public void changePassword(String currentPass, String newPass, String confirmPass);

    //void create(User user);

    void edit(User user);

    //void remove(User user);

    //User find(Object id);

    public User getByDni(String dni);

    public User initSession(Integer uid);

    public User login(String user, String pass, Map m);

    public boolean access(Object perm, User user, boolean reset);

    public boolean access(Object perm);

    public void logout();

    public Collection<Role> getRoles(User user);

    public User getUserByDir(int idDir);

    //public User getUserByDir(int idDir);

    //public boolean confirm(String pass);

	public boolean can(User u, String string);
	
	public Object getRoleList();

	public Object getPermList();

}
