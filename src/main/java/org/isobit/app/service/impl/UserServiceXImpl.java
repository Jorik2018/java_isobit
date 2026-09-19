package gob.regionancash.obresec.service;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.isobit.app.model.User;
import org.isobit.app.service.UserServiceX;

@ApplicationScoped
public class UserServiceXImpl implements UserServiceX {

    @Inject
    SecurityIdentity identity;

    @Override
    public User getCurrentUser() {
        if (identity.isAnonymous()) {
            return null;
        }

        String username = identity.getPrincipal().getName();

        User user = new User();

        // depende de tu modelo User:
        user.setName(username);

        return user;
    }

    @Override
    public boolean can(User u, String permission) {
        if (u == null || permission == null) {
            return false;
        }

        return identity.hasRole(permission);
    }
}