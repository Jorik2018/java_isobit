package org.isobit.app.service.impl;

import java.io.Serializable;
import javax.ejb.*;
import javax.servlet.http.HttpSession;
import org.isobit.app.X;
import org.isobit.app.service.SessionFacade;
import org.springframework.stereotype.Service;

import jakarta.enterprise.context.ApplicationScoped;

@Service
@ApplicationScoped
public class SessionFacadeLocalImpl implements SessionFacade, Serializable {

    private int n;
    private static int c;

    public SessionFacadeLocalImpl() {
        this.n = (++c);
    }

    @Override
    public String toString() {
        return "SessionBean{" + "n=" + n + '}';
    }

    @Override
    public Object get(String key) {
        try {
            HttpSession t = X.getSession();
            return t != null ? t.getAttribute(key) : null;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void put(String key, Object value) {
        X.getSession().setAttribute(key, value);
    }

    @Override
    public void logout() {
        invalidate();
    }

    @Override
    public void invalidate() {
        X.getSession().invalidate();
        HttpSession session = X.getRequest().getSession(true);
        X.setSession(session);
        //System.out.println("SessionFacade.invalidate. " + session.getId());
    }

}
