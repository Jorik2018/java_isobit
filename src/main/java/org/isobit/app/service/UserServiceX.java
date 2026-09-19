package org.isobit.app.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.isobit.app.model.Role;
import org.isobit.app.model.User;
import org.isobit.directory.model.People;

public interface UserServiceX  {

    //public List<User> load(int first, int pageSize, String sortField, Map<String, Object> filters);

    public User getCurrentUser();

    public boolean can(User u, String string);

}