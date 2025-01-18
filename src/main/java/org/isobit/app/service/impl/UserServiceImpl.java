package org.isobit.app.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.isobit.app.X;
import org.isobit.app.model.Permission;
import org.isobit.app.model.Role;
import org.isobit.app.model.User;
import org.isobit.app.service.ContactFacade;
import org.isobit.app.service.SessionFacade;
import org.isobit.app.service.SystemFacade;
import org.isobit.app.service.UserService;
import org.isobit.directory.model.People;
import org.isobit.util.BeanUtils;
import org.isobit.util.Encrypter;
import org.isobit.util.RandomUtil;
import org.isobit.util.SimpleException;
import org.isobit.util.XMap;
import org.isobit.util.XUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ApplicationScoped
public class UserServiceImpl
        implements UserService{

    @PersistenceContext
    private EntityManager em;

    @Override
    public List getUserScopeList(User u) {
        return em.createQuery("SELECT ue.scope FROM UserScope ue WHERE ue.uid=:uid").setParameter("uid", u.getUid()).getResultList();
    }


    @Inject
    @Autowired
    JsonWebToken jwt;

    @Autowired
    @Inject
    private ContactFacade contactFacade;

    @Autowired
    @Inject
    private SystemFacade systemFacade;

    private static List perms_anonymous = null;
    public static final String ACCESS_CALLBACK = "access callback";
    public static User anonymous;
    public static final List connectedUser;
    private static final Role AUTHENTICATED;
    private static final String USER_MAIL_ = "USER_MAIL_";

    @Autowired
    @Inject
    private SessionFacade sessionFacade;

    private static String[] moduleNameList;

    /*private String[] getModuleNameList() {
        if (moduleNameList == null) {
            List l = X.getModuleList(UserModule.class);
            moduleNameList = (String[]) l.toArray(new String[l.size()]);
        }
        return moduleNameList;
    }*/

    /*@Override
    public String mailText(String key, String language, Map variables) {
        String langcode = "";
        Object admin_setting = admin_setting = systemFacade.getV(USER_MAIL_ + key, false);
        if (!(admin_setting instanceof Boolean)) {
            return "";
        }
        if (key.equals((Object) ((Object) S.REGISTER_NO_APPROVAL_REQUIRED) + "_subject")) {
            return UserFacadeImpl.t("Detalles de la cuenta de usuario de !" + (Object) ((Object) T.NAME) + " en !" + (Object) ((Object) T.SITE) + " (aprobada).", variables, langcode);
        }
        if (key.equals((Object) ((Object) S.REGISTER_NO_APPROVAL_REQUIRED) + "_body")) {
            return UserFacadeImpl.t("!" + (Object) ((Object) T.NAME) + ",\n\nSu cuenta en !" + (Object) ((Object) T.SITE) + " ha sido creada." + "\n\nYa puede iniciar sesi\u00f3n en !" + (Object) ((Object) T.LOGIN_URI) + " en el futuro usando:" + "\n\nCodigo de usuario: !" + (Object) ((Object) T.USERNAME), variables, langcode) + "\n\nContraseña: ingresada por usted al crear su cuenta";
        }
        if (key.equals((Object) ((Object) S.REGISTER_ADMIN_CREATED) + "_subject")) {
            return UserFacadeImpl.t("Un administrador cre\u00f3 una cuenta para usted en !" + (Object) ((Object) T.SITE) + ".", variables, langcode);
        }
        if (key.equals((Object) ((Object) S.REGISTER_ADMIN_CREATED) + "_body")) {
            return UserFacadeImpl.t("!" + (Object) ((Object) T.NAME) + "," + "\n\nUn administrador del sitio !" + (Object) ((Object) T.SITE) + " ha creado una cuenta para usted. Ahora puede iniciar una sesi\u00f3n en !" + (Object) ((Object) T.LOGIN_URI) + " utilizando el siguiente nombre de usuario y la siguiente contrase\u00f1a:" + "\n\nNombre del usuario: !" + (Object) ((Object) T.USERNAME) + "\nContrase\u00f1a: !" + (Object) ((Object) T.PASSWORD) + "\n\nTambi\u00e9n puede ingresar haciendo clic en este enlace o copi\u00e1ndolo y peg\u00e1ndolo en el navegador:" + "\n!" + (Object) ((Object) T.LOGIN_URL) + "\n\nEsta URL s\u00f3lo es v\u00e1lida para ingresar una vez, y s\u00f3lo se puede usar en una ocasi\u00f3n." + "\n\nDespu\u00e9s de iniciar sesi\u00f3n, ser\u00e1 redirigido a !" + (Object) ((Object) T.EDIT_URI) + ", donde podr\u00e1 cambiar su contrase\u00f1a." + "\n\n\n\n. El equipo de !" + (Object) ((Object) T.SITE), variables, langcode);
        }
        if (key.equals((Object) ((Object) S.REGISTER_PENDING_APPROVAL) + "_subject") || key.equals((Object) ((Object) S.REGISTER_PENDING_APPROVAL_ADMIN) + "_body")) {
            return UserFacadeImpl.t("Account details for !" + (Object) ((Object) T.NAME) + " at !" + (Object) ((Object) T.SITE) + " (pending admin approval)", variables, langcode);
        }
        if (key.equals((Object) ((Object) S.REGISTER_PENDING_APPROVAL) + "_body")) {
            return UserFacadeImpl.t("!" + (Object) ((Object) T.NAME) + "," + "\n\nThank you for registering at !" + (Object) ((Object) T.SITE) + ". Your application for an account is currently pending approval. Once it has been approved, you will receive another e-mail containing information about how to log in, set your password, and other details." + "\n\n\n--  !" + (Object) ((Object) T.SITE) + " team", variables, langcode);
        }
        if (key.equals((Object) ((Object) S.REGISTER_PENDING_APPROVAL_ADMIN) + "_body")) {
            return UserFacadeImpl.t("!" + (Object) ((Object) T.NAME) + " has applied for an account." + "\n\n!" + (Object) ((Object) T.EDIT_URI), variables, langcode);
        }
        if (key.equals((Object) ((Object) S.PASSWORD_RESET) + "_subject")) {
            return UserFacadeImpl.t("Replacement login information for !" + (Object) ((Object) T.NAME) + " at !" + (Object) ((Object) T.SITE) + "", variables, langcode);
        }
        if (key.equals((Object) ((Object) S.PASSWORD_RESET) + "_body")) {
            return UserFacadeImpl.t("Hi !" + (Object) ((Object) T.NAME) + "!," + "\n\nA request to reset the password for your account has been made at !" + (Object) ((Object) T.SITE) + "." + "\n\nYou may now log in to !" + (Object) ((Object) T.URI_BRIEF) + " by clicking on this link or copying and pasting it in your browser:" + "\n\n<a style=\"line-break: anywhere;\" href=\"!" + (Object) ((Object) T.LOGIN_URL) + "\">!" + (Object) ((Object) T.LOGIN_URL) + "</a>" + "\n\nThis is a one-time login, so it can be used only once. It expires after one day and nothing will happen if it's not used." + "\n\nAfter logging in, you will be redirected to !" + (Object) ((Object) T.EDIT_URI) + " so you can change your password.", variables, langcode);
        }
        if (key.equals((Object) ((Object) S.STATUS_ACTIVATED) + "_subject")) {
            return UserFacadeImpl.t("Account details for !" + (Object) ((Object) T.NAME) + " at !" + (Object) ((Object) T.SITE) + " (approved)", variables, langcode);
        }
        if (key.equals((Object) ((Object) S.STATUS_ACTIVATED) + "_body")) {
            return UserFacadeImpl.t("!" + (Object) ((Object) T.NAME) + "," + "\n\nYour account at !" + (Object) ((Object) T.SITE) + " has been activated." + "\n\nYou may now log in by clicking on this link or copying and pasting it in your browser:" + "\n\n!" + (Object) ((Object) T.LOGIN_URL) + "\n\nThis is a one-time login, so it can be used only once." + "\n\nAfter logging in, you will be redirected to !" + (Object) ((Object) T.EDIT_URI) + " so you can change your password." + "\n\nOnce you have set your own password, you will be able to log in to !" + (Object) ((Object) T.LOGIN_URI) + " in the future using:\n\nusername: !username\n", variables, langcode);
        }
        if (key.equals((Object) ((Object) S.STATUS_BLOCKED) + "_subject")) {
            return UserFacadeImpl.t("Account details for !" + (Object) ((Object) T.NAME) + " at !" + (Object) ((Object) T.SITE) + " (blocked)", variables, langcode);
        }
        if (key.equals((Object) ((Object) S.STATUS_BLOCKED) + "_body")) {
            return UserFacadeImpl.t("!" + (Object) ((Object) T.NAME) + ",\n\nYour account on !site has been blocked.", variables, langcode);
        }
        if (key.equals((Object) ((Object) S.STATUS_DELETED) + "_subject")) {
            return UserFacadeImpl.t("Account details for !" + (Object) ((Object) T.NAME) + " at !" + (Object) ((Object) T.SITE) + " (deleted)", variables, langcode);
        }
        if (key.equals((Object) ((Object) S.STATUS_DELETED) + "_body")) {
            return UserFacadeImpl.t("!" + (Object) ((Object) T.NAME) + ",\n\nYour account on !" + (Object) ((Object) T.SITE) + " has been deleted.", variables, langcode);
        }
        return null;
    }
*/
   /*  @Override
    public Map mailTokens(Map tokens, User account, String language, Map values) {
        if (values == null) {
            tokens.put(Enum.class, T.values());
            return tokens;
        }
        String base_url = X.url("", true);
        if (base_url.endsWith("/")) {
            base_url = base_url.substring(0, base_url.length() - 1);
        }
        base_url = "http://web.regionancash.gob.pe";
        account = (User) values.get("account");
        String name = (String) values.get((Object) T.NAME);
        String passResetUrl = UserFacadeImpl.this.passResetUrl(account);
        tokens.put(T.USERNAME, account.getName());
        tokens.put(T.NAME, name != null ? name : account.getName());
        tokens.put(T.SITE, UserFacadeImpl.this.systemFacade.getV("site_name", "_"));
        tokens.put(T.LOGIN_URL, base_url + passResetUrl);
        tokens.put(T.PASS_RESET_URL, base_url + passResetUrl);
        tokens.put(T.URI_BRIEF, base_url.replace("http://", ""));
        tokens.put(T.URI, base_url);
        tokens.put(T.MAILTO, account.getMail());
        tokens.put(T.DATE, XDate.toString(new Date(), "dd/MMM/yyyy"));
        tokens.put(T.LOGIN_URI, base_url + "/admin");
        tokens.put(T.EDIT_URI, base_url + "/admin/me");
        tokens.put(T.PASSWORD, values.get("pass"));
        return tokens;
    }

    @Override
    public List getTemplateList() {
        ArrayList<String> templateList = new ArrayList();
        for (S o : S.values()) {
            templateList.add(o.toString());
        }
        return templateList;
    }
*/
    @Override
    public void load() {
        X.getRequest().setAttribute(X.TEMPLATE, "/simpleTemplate.xhtml");
    }

    @Override
    public User load(Object id) {
        User u = em.find(User.class,XUtil.intValue(id));
        //HashMap ext = new HashMap();
        /*u.setExt(ext);*/
        //u.setDruRoleCollection(getRoles(u));
        /*if (XUtil.intValue(u.getDirectoryId()) != 0) {
            People people = em.find(People.class, (Object) u.getDirectoryId());
            em.detach(people);
            //people.setDocument(null);
            ext.put("people", people);
        }*/
        /*em.detach(u);
        u.setPass(null);
        ext.put("oldStatus", u.getStatus());*/
        return u;
    }

    @Override
    public void edit(User account) {

        Map ext =null;// (Map) account.getExt();
        boolean admin = access(Perm.ADMIN_USERS);
        System.out.println("admin=" + admin);
        Object om = systemFacade.getV(S.USER_EMAIL_VERIFICATION.toString(), true);
//        X.log("USER_EMAIL_VERIFICATION=" + om);
        boolean USER_EMAIL_VERIFICATION = XUtil.booleanValue(om);
        boolean notify = XUtil.booleanValue(systemFacade.getV("NOTIFY", true));
        int op = 1;
        if (!(ext.get("people") instanceof People) && ext.get("people") != null) {
            ext.put("people", BeanUtils.getObject(People.class, ext.get("people")));
        }

        String pass = (String) ext.get("clave");
        if (!XUtil.isEmpty(pass)) {
            if (!pass.equals(ext.get("confirm"))) {
                throw new RuntimeException("Contraseña y Confirmación no son identicos");
            }
            account.setPass(new Encrypter().encode(Encrypter.MD5, pass));
        }

        //Si no se requiere aprobaion de admin pero si user mail verificacion
        if (XUtil.isEmpty(account.getPass())) {
            System.out.println(" geerar pass " + account);
            account.setPass(new Encrypter().encode(Encrypter.MD5, pass = RandomUtil.getW(10, false)));
        }
        ext.put("pass", pass);
        EntityManager em = this.getEntityManager();
//        int max = XUtil.intValue(em.createQuery("SELECT MAX(u.uid) FROM User u").getSingleResult());
        People people = (People) ext.get("people");
        if (people != null) {
            account.setDirectoryId(people.getId());
        }

        if (account.getUid() == null /*|| account.getUid() > 0*/) {
            if (account.getUid() != null && account.getUid() == 1) {
                admin = true;
                notify = false;
            }
            if (!admin && USER_EMAIL_VERIFICATION) {
                account.setStatus((short) 1);
            }
            if (account.getCreated() == 0) {
                account.setCreated((int) (X.getServerDate().getTime() / 1000));
            }
            //debe tenerse en cuenta que puede tener varios correos

            if (existsNameXorMail(account.getName(), account.getMail())) {
                throw new RuntimeException("El Nombre o Correo Electronico ya esta registrado en el sistema");
            }

            em.persist(account);

            //System.out.println("account22="+people.getFullName());
        } else {
            em.merge(account);
            op = 0;
            //Si se realizan cambios 
        }
        if (ext.containsKey("people")) {
            people = (People) ext.get("people");
            if (people != null) {
                /*Si cuando se registra la people esta vacio es porque se esta registrando un nuevo usuario*/
                User oo = (User) sessionFacade.get(X.USER);
                boolean bb = false;
                if (oo == null || XUtil.intValue(((User) oo).getUid()) == 0) {
                    sessionFacade.put(X.USER, account);
                    bb = true;
                }
                people = em.find(People.class, people.getId());
                //comoel directorio esta en otra area no se puede cambiar el correo
                /*try {
                    getModule(UserFacadeImpl.CrudModule.class,
                            people.getClass().getSimpleName()).afterEdit(account, people);
                } catch (Exception e) {
                    System.out.println("UserFacade.edit=" + e);
                }*/
                if (bb) {
                    sessionFacade.put("people", people);
                }
            }
        }
        /*if (account.getUid() != 1 && account.getRoleCollection() != null) {
            em.createQuery("DELETE FROM UserRole u WHERE u.PK.uid=:uid").setParameter("uid", (Object) account.getUid()).executeUpdate();

            account.getRoleCollection().stream().forEach(r -> {
                UserRolPK pk = new UserRolPK(account.getUid() < 0 ? -account.getIdDir() : account.getUid(), r.getRid());
                em.merge((Object) new UserRole(pk));
            });
        }*/
//        X.log("notify=" + notify + ";admin=" + admin);

        notify = false;
        if (admin && !notify) {
            switch (op) {
                case 1:
                    ext.put(X.MSG, "Se creó una nueva cuenta de usuario para <a href='" + X.url("user/" + account.getUid()) + "'>" + account.getName() + "</a>. No se ha enviado mensaje a correo.");
                    ext.put(X.DESTINY, "user/" + account.getUid() + "/edit");
                    return;
                case 0:
                    ext.put(X.MSG, "Los cambios en la cuenta han sido guardados.");
                    ext.put(X.DESTINY, "user/" + account.getUid());
            }
        } else if (!USER_EMAIL_VERIFICATION && account.getStatus() > 0 && !admin) {
            // No e-mail verification is required, create new user account, and login
            // user immediately.
            mailNotify(ext, S.REGISTER_NO_APPROVAL_REQUIRED.toString(), account, "");
            login(account.getName(), pass, new HashMap());
            ext.put(X.MSG, "Registration successful. You are now logged in.");
            //Ir al destino en que se deseaba ir originalmente
        } else if (account.getStatus() > 0 || notify) {
            String opp = (notify ? S.REGISTER_ADMIN_CREATED : S.REGISTER_NO_APPROVAL_REQUIRED).toString();
            if (notify) {
                X.log("op=" + op);
                if (op == 0) {
                    short oldStatus = (short) XUtil.intValue(ext.get("oldStatus"));
                    X.log("op=" + op + ";x=" + (oldStatus != account.getStatus()));
                    if (oldStatus != account.getStatus()) {
                        mailNotify(ext, (account.getStatus() == 1 ? S.STATUS_ACTIVATED : S.STATUS_BLOCKED).toString(), account, "es");
                    }
                    ext.put(X.MSG, "Los cambios en la cuenta han sido guardados.");
                } else {
                    mailNotify(ext, opp, account, "es");
                    ext.put(X.MSG, "Contraseña y otras instrucciones se han enviado por correo electrónico al nuevo usuario <a href='" + X.url("user/" + account.getUid()) + "'>" + account.getName() + "</a>.");
                    ext.put(X.DESTINY, "admin");
                }
            } else {
                mailNotify(ext, opp, account, "es");
                ext.put(X.MSG, "Su contraseña y otras instrucciones han sido enviado a su dirección de correo electrónico.");
//                            redirect(request, response,"");
            }
        } else {
            // Create new user account, administrator approval required.
            mailNotify(ext, S.REGISTER_PENDING_APPROVAL.toString(), account, "es");
            ext.put(X.MSG, "Gracias por solicitar una cuenta. Su cuenta está pendiente de aprobación por parte del administrador del sitio . <br/> Mientras tanto, un mensaje de bienvenida con instrucciones adicionales ha sido enviada a su dirección de correo electrónico.");
//                        redirect(request, response,"");
        }
    }

   /*public Map mail(Map m, String key, Map message) {
        String language = (String) message.get("language");
        Map variables = mailTokens(new HashMap(), (User) m.get("user"), language, m);
        message.put("subject", message.get("subject") + mailText(new StringBuilder().append(key).append("_subject").toString(), language, variables));
        ((XMap) message.get("body")).add(mailText(key + "_body", language, variables));
        return message;
    }*/

    /*public String passResetUrl(User account) {
        long passResetTimestamp = X.getServerDate().getTime() / 1000;
        return X.url("user/reset/" + account.getUid() + "/" + passResetTimestamp + "/" + this.passRehash(account.getPass(), passResetTimestamp, account.getLogin()));
    }*/

    private String passRehash(String pass, long timestamp, long login) {
        return new Encrypter().encode(Encrypter.MD5, pass + timestamp + login);
    }

    private boolean mailNotify(Map m, String op, User account, String language) {
        boolean default_notify = !op.equals(S.STATUS_DELETED.toString()) && !op.equals(S.STATUS_BLOCKED.toString());
        boolean notify = XUtil.booleanValue(systemFacade.getV(USER_MAIL_ + op + "_notify", default_notify));
        if (notify) {
            XMap params = new XMap("account", account);
            if (XUtil.intValue(account.getDirectoryId()) != 0) {
                try {
                    m.put(T.NAME, this.getEntityManager().createQuery("SELECT p.names FROM People p WHERE p.id=:peopleId").setParameter("peopleId", (Object) account.getDirectoryId()).getSingleResult().toString().split(" ")[0]);
                } catch (NoResultException noResultException) {
                    // empty catch block
                }
            } else {
                People people = (People) m.get("people");
                if (people != null) {
                    m.put(T.NAME, ("" + people.getNames()).split(" ")[0]);
                }

            }
            m.put("account", account);
            /*contactFacade.mail(m, this, op, account.getMail(), language, params);
            if (op.equals(S.REGISTER_PENDING_APPROVAL.toString())) {
                contactFacade.mail(
                        m, this,
                        S.REGISTER_PENDING_APPROVAL_ADMIN.toString(),
                        systemFacade.getV("site_mail", "").toString(),
                        language,
                        params
                );
            }*/
        }
        return true;
    }

    @Override
    public Object passReset(int uid, long requestPassTimestamp, String hashedPass, Map m) {
        User u = (User) this.sessionFacade.get(X.USER);
        if (u != null && u.getUid() != 0) {
            throw new SimpleException("Ud. ha usado ya este enlace de inicio de sesión unico. "
                    + "No es necesario usarlo usar este link. Ud. ya ha iniciado la sesión", "/");
        }
        long timeout = 86400;
        long current = new Date().getTime() / 1000;
        EntityManager em = getEntityManager();
        User account = (User) em.find(User.class, (Object) uid);
        if (requestPassTimestamp < current && account != null) {
            if (account.getLogin() > 0 && current - requestPassTimestamp > timeout) {
                throw new SimpleException("You have tried to use a one-time login link that has expired. Please request a new one using the form below.", "/password");
            }
            if (account.getUid() != 0
                    && requestPassTimestamp > account.getLogin()
                    && current > requestPassTimestamp
                    && hashedPass.equals(this.passRehash(account.getPass(), requestPassTimestamp, account.getLogin()))) {
                if ("update".equals(m.remove("ACTION"))) {
                    u = account;
                    String newPass = (String) m.get("clave");
                    String confirmPass = (String) m.get("confirm");
                    if (XUtil.isEmpty(newPass)) {
                        throw new SimpleException("Contraseña no puede ser en blanco");
                    }
                    if (!newPass.equals(confirmPass)) {
                        throw new SimpleException("Contraseña nueva y su confirmacion deben ser iguales");
                    }
                    u.setPass(new Encrypter().encode(Encrypter.MD5, newPass));
                    authenticateFinalize(u);
                    return new SimpleException("Acabas de utilizar su enlace de inicio de sesión único. "
                            + "Ya no sera posible utilizar nuevamente este enlace para iniciar sesi\u00f3n. "
                            + "Por favor, cambie su contraseña.", "admin");
                }
            } else {
                throw new SimpleException("Has tratado de utilizar un enlace de inicio de sesión único which has either been used or is no longer valid. Please request a new one using the form below.", "/password");
            }
        }
        requestPassTimestamp = Long.parseLong(m.get("timestamp").toString());
        u = (User) account;
        //m.put("name", u.getExt() != null ? ((People) u.getExt()).getFullName() : u.getName());
        m.put("account", account);
        m.put("expirationDate", new Date((requestPassTimestamp + timeout) * 1000));
        return null;
    }

    @Override
    public int password(Map m) throws Exception {
        String name = (String) m.get("name");
        User user = null;
        try {
            user = (User) this.getEntityManager().createQuery("SELECT u FROM User u WHERE (LOWER(u.name)=:name OR LOWER(u.mail)=:name)", User.class).setParameter("name", name.toLowerCase()).getSingleResult();
        } catch (NoResultException n) {
            //Si no se encuentra registro se intenta otros componentes
            /*for (String mn : getModuleNameList()) {
                if (user != null) {
                    break;
                }
                try {
                    user = this.getModule(UserFacade.UserModule.class, mn).password(m);
                } catch (SimpleException e) {
                    //throw e;
                } catch (RuntimeException e) {
                    X.log(e.getMessage());
                }
            }*/
        }
        if (user != null && user.getStatus() > 0) {
            if (!this.mailNotify(m, S.PASSWORD_RESET.toString(), user, "es")) {
                throw new SimpleException("Ha sucedido un error al enviar informacion por correo.");
            }
        } else {
            throw new SimpleException("El codigo de usuario o correo no se encuentra registrado o esta inactivo, comuniquese con el administrador.");
        }
        return 0;
    }

    public static String t(String t, Map variables, String c) {
//        if (variables != null) {
//            for (Map.Entry entry : (Set<Map.Entry>) variables.entrySet()) {
//                if (entry.getValue() == null) {
//                    continue;
//                }
//                t = t.replace(entry.getKey().toString(), entry.getValue().toString());
//            }
//        }
        return t;
    }

    @Override
    public void send(Map m) {
        m.put("account", this.sessionFacade.get(X.USER));
        //this.contactFacade.mail(m, this, (Object) ((Object) S.REGISTER_NO_APPROVAL_REQUIRED) + "_body", (String) m.get("destiny"), "", m);
    }

    private void authenticateFinalize(User user) {
        user.setLogin(X.getServerDate().getTime() / 1000);
        EntityManager em = this.getEntityManager();
        if (user.getUid() > 0) {
            em.merge((Object) user);
        } else if (user.getUid() < 0) { //Se creo un user temporal por otro 
            user.setPass(new Encrypter().encode(Encrypter.MD5, RandomUtil.getW(10, false)));
            Long login = user.getLogin();
            user.setCreated((int) (login != null ? login.intValue() : (new Date().getTime() / 100)));
            user.setStatus((short) 1);
            em.persist(user);
        }
        //Object destiny = this.sessionFacade.get(X.DESTINY);
        //sessionFacade.invalidate();

        if (user.getUid() > 0) {
            user = (User) em.find(User.class, (Object) user.getUid());
        }
        HashMap ext = new HashMap();
        if (XUtil.intValue(user.getDirectoryId()) > 0) {
            People people = em.find(People.class, (Object) user.getDirectoryId());
            //Se genera un error por caracteres especiales en el template al parsear el json
            //sta usando un people con un usr dntro lo q cual provic un rror
            //System.out.println("finalizre " + people + " people=" + people.getExt() + " user.idDir=" + user.getIdDir());
            //people.setExt(new HashMap());
            em.detach(user);
            user.setPass(null);
            em.detach(people);
            //people.setDocument(null);
            People p = new People();
            p.setFullName(people.getFullName());
            p.setNames(people.getNames());
            p.setSex(people.getSex());
            p.setMail(people.getMail());
            p.setBirthdate(people.getBirthdate());
            //p.setApPaterno(people.getApPaterno());
            //p.setApMaterno(people.getApMaterno());
            p.setCode(people.getCode());
            p.setStatus(people.getStatus());
            p.setId(people.getId());
            ext.put(People.class.getName(), p);
            ext.put("people", p);
            sessionFacade.put("people", p);
            sessionFacade.put(People.class.getName(), p);
        }
        //user.setExt(ext);
        sessionFacade.put(X.USER, user);
        //sessionFacade.put(X.DESTINY, destiny);
        if ("".equals(X.getRequest().getContextPath())) {
            /*Login login = new Login();
            login.setUid(user.getUid());
            login.setAccessDate(new java.sql.Timestamp(X.getServerDate().getTime()));
            login.setPeopleId(XUtil.intValue(user.getDirectoryId()));
            if (login.getId() == null) {
                em.persist(login);
            } else {
                em.merge(login);
            }
            this.sessionFacade.put("login", login);
            String token = X.toText(X.getClientIpAddr(X.getRequest())).replace(".", "")
                    + "." + login.getId()
                    + "." + X.getRequest().getSession().getId();
            this.sessionFacade.put("TOKEN", token);*/
        }
        //this.sessionFacade.put("TOKEN", user.getUid());

        /*for (String mn : getModuleNameList()) {
            try {
                this.getModule(UserFacade.UserModule.class, mn).authenticateFinalize(user);
            } catch (RuntimeException e) {
                System.out.println(mn + ".authenticateFinalize>" + e.getLocalizedMessage());
                //System.out.println("userFacade.authenticateFinalize->RuntimeException");
                //e.printStackTrace();
                //throw new RuntimeException(e);
            }
        }*/
//        System.out.println("authenticateFinalize=>SE HA GUARDADO USER= "
//                + sessionFacade.get(X.USER)+" - "+ X.getRequest().getSession().getAttribute(X.USER)+" --- "
//        + X.getRequest().getSession().getId());
    }

    private static boolean ALL_ACCESS = true;

    @Override
    public User getByDni(String dni) {
        List lu;
        EntityManager em = this.getEntityManager();
        List<People> lp = em.createQuery("SELECT p FROM People p WHERE p.code LIKE :dni").setParameter("dni", (Object) dni).getResultList();
        ArrayList li = new ArrayList();
        lp.stream().forEach(pn -> {
            li.add(pn.getId());
        }
        );
        if (lp.size() > 0 && (lu = em.createQuery("SELECT u FROM User u WHERE u.idDir IN (" + XUtil.implode(li, (Object) ",") + ")").getResultList()).size() > 0) {
            return (User) lu.get(0);
        }
        return null;
    }

    @Override
    public Collection<Role> getRoles(User user) {
        ArrayList<Role> roles=null;
        /*if (user.getUid() < 0) {
            user.setDruRoleCollection(em.createQuery("SELECT r FROM Role r,UserRole u WHERE r.rid=u.PK.rid AND u.PK.uid=:uid", Role.class).setParameter("uid", (Object) (-user.getIdDir().intValue())).getResultList());
        } else if (user.getUid() > 0) {
            user.setDruRoleCollection(em.createQuery("SELECT r FROM Role r,UserRole u WHERE r.rid=u.PK.rid AND u.PK.uid=:uid", Role.class).setParameter("uid", (Object) user.getUid()).getResultList());
        } else {
            return (List) anonymous.getRoleCollection();
        }
        roles = (ArrayList<Role>) user.getRoleCollection();*/
        if (roles == null) {
            roles = new ArrayList();
        }
        if (!roles.contains(AUTHENTICATED)) {
            roles.add(AUTHENTICATED);
        }
        for (Role role : roles) {
            em.detach(role);
            //role.setPermissionCollection(null);
        }
        System.out.println("los roles de " + user + " son " + roles);
        return roles;
    }

    @Override
    public boolean access(Object perm) {
        return this.access(perm, (User) this.sessionFacade.get(X.USER), false);
    }

    @Override
    public boolean access(Object perm, User user, boolean reset) {
        if (user == null) {
            return false;
        }
        List perms = (List) sessionFacade.get("perms");
        if (reset) {
            perms = null;
        }
        switch (user.getUid()) {
            case 1:
                return true;
            case 0: {
                if (perms_anonymous == null) {
                    List<Role> roles = (List<Role>) getRoles(user);
                    perms = perms_anonymous = new ArrayList();
                    for (Role role : roles) {
                        perms.add(role.getRid());
                    }
                    List l = getEntityManager().createQuery("SELECT p.perm FROM Role r JOIN r.permissionCollection p WHERE r.rid IN (" + XUtil.toString(perms) + ")").getResultList();
                    perms.clear();
                    /*Map ext = (Map) user.getExt();
                    if (ext != null) {
                        ext.put("perms", perms);
                    }
                    sessionFacade.put("perms", perms);
                    for (Object o : l) {
                        for (String s : o.toString().split(",")) {
                            if (s.length() > 0) {
                                perms.add(s.trim());
                            }
                        }
                    }*/
                }
            }
        }
//        System.out.println("perrrrrr=" + perms);
        if (perms == null) {
//            System.out.println("recuperando roles para " + user);
            /*Map ext = (Map) user.getExt();
            List<Role> roles = (List<Role>) getRoles(user);
            perms = new ArrayList();
            for (Role role : roles) {
                em.detach(role);
                //role.setPermissionCollection(null);
                perms.add(role.getRid());
            }
            List l = em.createQuery("SELECT p.perm FROM Role r JOIN Permission p ON p.role=r WHERE r.rid IN (" + XUtil.toString(perms) + ")").getResultList();
            perms.clear();
            ext.put("perms", perms);
            sessionFacade.put("perms", perms);
            for (Object o : l) {
                for (String s : o.toString().split(",")) {
                    if (s.length() > 0) {
                        perms.add(s.trim());
                    }
                }
            }*/
            /*for (String mn : getModuleNameList()) {
                try {
                    this.getModule(UserFacade.UserModule.class, mn).loadPerm(user, perms);
                } catch (RuntimeException e) {
                    X.log(e.getMessage());
                }
            }*/
        }
        if (perm instanceof String) {
            for (String s : ((String) perm).split(",")) {
                if (perms.contains(s)) {
                    return true;
                }
            }
            return false;
        } /*else if (perm instanceof MenuRouter) {
            MenuRouter menuRouter = (MenuRouter) perm;
            String accessCallback = menuRouter.getAccessCallback();
            String accessArgument = menuRouter.getAccessArguments();
//            System.out.println("perms=" + perms);
//            System.out.println("accessArgument=" + accessArgument);
//            System.out.println("menuRouter.getPath()=" + menuRouter.getPath());
            if (accessArgument.length() > 0 && perms.contains(accessArgument)) {
                return true;
            } else {
                return perms.contains(menuRouter.getPath());
            }
        }*/
        return perms.contains("" + perm);
    }

    @Override
    public void logout() {
        User user = this.getCurrentUser();
        /*for (String mn : getModuleNameList()) {
            try {
                UserModule um = getModule(UserModule.class, mn);
                System.out.println("um=" + um);
                user = um.logout(user);
                X.log("Iniciando session usando " + mn + " resulta user=" + user);
            } catch (RuntimeException | AbstractMethodError e) {
                System.out.println("userFacade.logout->" + e);
            }
        }*/
        this.sessionFacade.logout();
    }

    @Override
    public User login(String name, String pass, Map m) {
        EntityManager em = this.getEntityManager();
        User user = null;
        try {
            name = name.trim().toLowerCase();
            user = (User) em.createQuery("SELECT u FROM User u WHERE (LOWER(u.name)=:name OR LOWER(u.mail)=:name) AND u.pass=:pass").setParameter("name", name).setParameter("pass", new Encrypter().encode(Encrypter.MD5, pass)).getSingleResult();
            if (user.getStatus() == 0) {
                return null;
            }
        } catch (NoResultException noResultException) {
            X.log("Failed attemp for " + name + " using " + pass + "=" + new Encrypter().encode(Encrypter.MD5, pass));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        X.log("userFacade.user=" + user);
        /*for (String mn : getModuleNameList()) {
            if (user != null) {
                break;
            }
            try {
                user = getModule(UserModule.class, mn).login(name, pass, m);
                X.log("Iniciando session usando " + mn + " resulta user=" + user);
            } catch (RuntimeException e) {
                X.log(e);
            }
        }*/
        if (user != null) {

            //user.setPassStrong();
            this.authenticateFinalize(user);
        }
        return user;
    }

    @Override
    public User initSession(Integer uid) {
        User user = em.find(User.class,uid);
        if (user != null) {
            this.authenticateFinalize(user);
        }
        return user;
    }

    @Override
    public User getUserByDir(int idDir) {
        try {
            List l = this.getEntityManager().createQuery("SELECT u FROM User u WHERE u.idDir=:idDir").setParameter("idDir", (Object) idDir).getResultList();
            if (!l.isEmpty()) {
                return (User) l.get(0);
            }
        } catch (Exception e) {
            System.out.println("userFacade.getUserByDir->" + e);
        }
        return null;
    }

    /*@Override
    public boolean confirm(String pass) {
        User user = (User) this.sessionFacade.get(X.USER);
        user = getEntityManager().find(User.class, user.getUid());
        return user.getPass().equals(new Encrypter().encode(Encrypter.MD5, pass));
    }*/

    @Override
    public void changePassword(String currentPass, String newPass, String confirmPass) {
        if (XUtil.isEmpty(newPass)) {
            throw new SimpleException("Contrase\u00f1a no puede ser en blanco");
        }
        if (!newPass.equals(confirmPass)) {
            throw new SimpleException("Contrase\u00f1a nueva y su confirmacion deben ser iguales");
        }
        /*if (!this.confirm(currentPass)) {
            throw new SimpleException("Contrase\u00f1a actual ingresada no es la correcta");
        }*/
        User user = (User) sessionFacade.get(X.USER);
        EntityManager em = getEntityManager();
        User u = em.find(User.class, user.getUid());

        u.setPass(new Encrypter().encode(Encrypter.MD5, newPass));
        em.merge(u);
    }

    /*@PostConstruct
    public void init() {
        add(new RoleFacade.RoleModule() {
            @Override
            public Object[] getPerms() {
                return Perm.values();
            }
        });
        add(this);
        add(this);
    }*/

    /*@Override
    public Object getBlock(HttpServletRequest request, String op, Object delta) {
        if ("list".equals(op)) {
            Map blocks = (Map) request.getAttribute("#blocks");
            blocks.put("0", new XMap("info", "User login"));
            blocks.put("1", new XMap("info", "Navigation"));
            blocks.put("2", new XMap("info", "Who's new"));
            blocks.put("3", new XMap("info", "Who's online"));
            blocks.put("4", new XMap("info", "Herramienta desarrollo"));
            return blocks;
        }
        if ("view".equals(op)) {
            User user = (User) UserFacadeImpl.this.sessionFacade.get(X.USER);
            switch (XUtil.intValue(delta)) {
                case 0: {
                    String[] q = (String[]) request.getAttribute("#q");
                    if (user != null && user.getUid() != 0 || q.length > 0 && "user".equalsIgnoreCase(q[0])) {
                        break;
                    }
                    return new XMap("src", "/user/login.xhtml");
                }
                case 1: {
                    break;
                }
                case 4: {
                    return new XMap("title", "Memoria", "content", SystemUtilities.getMemory());
                }
            }
        }
        return null;
    }*/

    static {
        connectedUser = new ArrayList();
        AUTHENTICATED = new Role(2, "");
    }

    @Override
    public boolean existsNameXorMail(String name, String mail) {
        if (mail != null) {
            if (name == null) {
                return XUtil.intValue(this.getEntityManager().createQuery("SELECT COUNT(u) FROM User u WHERE u.mail=:mail")
                        .setParameter("mail", mail).getSingleResult()) > 0;
            } else {
                return XUtil.intValue(this.getEntityManager().createQuery("SELECT COUNT(u) FROM User u WHERE u.mail=:mail OR u.name=:name")
                        .setParameter("mail", mail).setParameter("name", name).getSingleResult()) > 0;
            }
        } else {
            return XUtil.intValue(this.getEntityManager().createQuery("SELECT COUNT(u) FROM User u WHERE u.name=:name")
                    .setParameter("name", name).getSingleResult()) > 0;
        }
    }

    @Override
    public User findByName(String name) {
        List lu;
        if ((lu = em.createQuery("SELECT u FROM User u WHERE UPPER(u.name)=:name")
                .setParameter("name", name.toUpperCase())
                .getResultList()).size() > 0) {
            return (User) lu.get(0);
        }
        return null;
    }

    @Override
    public People getDrtPersonaNatural(int id) {
        return getEntityManager().find(People.class, id);
    }

    private EntityManager getEntityManager() {
        return em;
    }

    @Override
    public User getCurrentUser() {
        User user=(User) sessionFacade.get(X.USER);
        if(user==null)user=new User();
		user.setUid(XUtil.intValue(jwt.getClaim("uid")));
		if(jwt.containsClaim("directory"))
			user.setDirectoryId(XUtil.intValue(jwt.getClaim("directory")));
		return user;
    }

    
	public boolean can(User u, String string) {
		EntityManager em=getEntityManager();
		for(Permission permission:em.createQuery("SELECT pe FROM Permission pe "
				+ "inner join UserRole ur on ur.PK.rid=pe.role.rid "
				+ "where ur.PK.uid=:uid",Permission.class).setParameter("uid",u.getUid()).getResultList()){
			if(permission.getPerm().contains(string))return true;
		}
		return false;
	}
	
	public Object getRoleList(){
		EntityManager em=getEntityManager();
		User user=this.getCurrentUser();
		return em.createQuery("SELECT r FROM UserRole ur JOIN Role r ON r.rid=ur.PK.rid WHERE ur.PK.uid=:uid")
				.setParameter("uid",user.getUid())
				.getResultList();
	}

	public Object getPermList(){
		EntityManager em=getEntityManager();
		User user=this.getCurrentUser();
		return em.createQuery("SELECT r FROM UserRole ur JOIN Role r ON r.rid=ur.PK.rid WHERE ur.PK.uid=:uid")
				.setParameter("uid",user.getUid())
				.getResultList();
	}
	
    /*@Override
    public User initSessionByToken(String access_token) {
        int loginId = XUtil.intValue(access_token.split(".")[0]);
        Login login = this.getEntityManager().find(Login.class, loginId);
        return initSession(login.getUid());
    }*/

    static enum T {

        DATE,
        PASSWORD,
        URI_BRIEF,
        URI,
        LOGIN_URI,
        LOGIN_URL,
        PASS_RESET_URL,
        SITE,
        EDIT_URI,
        USERNAME,
        NAME,
        COMPLETE_NAME,
        MAILTO;

        private T() {
        }
    }

    static enum S {

        PREFIX,
        USER_EMAIL_VERIFICATION,
        REGISTER_ADMIN_CREATED,
        REGISTER_NO_APPROVAL_REQUIRED,
        REGISTER_PENDING_APPROVAL,
        REGISTER_PENDING_APPROVAL_ADMIN,
        STATUS_DELETED,
        STATUS_ACTIVATED,
        STATUS_BLOCKED,
        PASSWORD_RESET,
        USER_REGISTRATION_HELP;

        private S() {
        }
        
    }

    class UserListener implements HttpSessionBindingListener {

        User user;

        public UserListener(User u) {
            this.user = u;
        }

        @Override
        public String toString() {
            return this.user.toString();
        }

        @Override
        public void valueBound(HttpSessionBindingEvent hsbe) {
            UserServiceImpl.connectedUser.add(this);
        }

        @Override
        public void valueUnbound(HttpSessionBindingEvent hsbe) {
            UserServiceImpl.connectedUser.remove(this);
        }
        
    }

}
