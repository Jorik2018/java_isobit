package org.isobit.app.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.isobit.directory.model.People;

//import javax.servlet.http.HttpSessionBindingEvent;
//import javax.servlet.http.HttpSessionBindingListener;
//import javax.xml.bind.annotation.XmlRootElement;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false,onlyExplicitlyIncluded = true)
@Entity
@Table(name = "dru_users")
public class User implements Serializable/*, HttpSessionBindingListener*/ {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "uid")
    private Integer uid;
    @Column(name = "id_dir")
    private Integer directoryId;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "signature_format")
    private short signature_format = 0;
    @Basic(optional = false)
    @Column(name = "mail")
    private String mail;
    @Basic(optional = false)
    @Column(name = "pass")
    private String pass;
    @Column(name = "pass_strong")
    private String passStrong;
    @Basic(optional = false)
    @Column(name = "sort")
    private short sort = 0;
    @Basic(optional = false)
    @Column(name = "_mode")
    private Short mode = 0;
    @Basic(optional = false)
    @Column(name = "threshold")
    private short threshold = 0;
    @Basic(optional = false)
    @Column(name = "theme")
    private String theme = "";
    @Column(name = "language")
    private String language = "";
    @Basic(optional = false)
    @Column(name = "signature")
    private String signature = "";
    @Basic(optional = false)
    @Column(name = "status")
    private short status;
    @Column(name = "timezone")
    private String timezone = "";
    @Basic(optional = false)
    @Column(name = "picture")
    private String picture = "";
    @Basic(optional = true)
    @Column(name = "init")
    private String init = "";
    @Basic(optional = true)
    @Column(name = "created")
    private int created;
    @Basic(optional = true)
    @Column(name = "dependency_id")
    private Integer dependencyId;
    @Basic(optional = true)
    @Column(name = "access")
    private Integer access;
    @Basic(optional = false)
    @Column(name = "_login")
    private long login=0;
    @Transient
    private People people;
    @Transient
    private Collection<UserRole> userRoles;
   

    @Override
    public String toString() {
        return "User:" + uid + ":" + name + ",people=" + this.directoryId;
    }

   
    /*@Override
    public void valueBound(HttpSessionBindingEvent event) {
        Set<User> logins = (Set<User>) event.getSession().getServletContext().getAttribute("logins");
        if (logins == null) {
            event.getSession().getServletContext().setAttribute("logins", logins = new HashSet<User>());
        }
        logins.add(this);
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        Set<User> logins = (Set<User>) event.getSession().getServletContext().getAttribute("logins");
        logins.remove(this);
    }*/

}
