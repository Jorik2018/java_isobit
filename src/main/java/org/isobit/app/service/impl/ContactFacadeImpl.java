package org.isobit.app.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.isobit.app.service.ContactFacade;
import org.isobit.app.service.SystemFacade;
import org.isobit.app.service.ContactFacade.ContactModule;
import org.isobit.util.XMap;
import org.isobit.util.XUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@Service
public class ContactFacadeImpl implements ContactFacade {

    @Autowired
    @Inject
    private SystemFacade systemFacade;

    private static String site_mail;

    private static String site_mail_password;

    private static Map TEMPLATE_MAP;

    /*@Override
    public Map getDefaultTemplate(String module, String templateKey) {
        try {
            ContactModule m = getModule(ContactModule.class, module);
            return new XMap(
                    "subject", m.mailText(templateKey + "_subject", null, null),
                    "body", m.mailText(templateKey + "_body", null, null)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getTemplate(String module, String templateKey) {
        //Debe maparse todo modulo-keytemplate
        try {
            Map templateMap = getTEMPLATE_MAP();

            Map tmp = (Map) templateMap.get(templateKey);
            if (tmp == null) {
                templateMap.put(templateKey, tmp = new HashMap());
            }
            String subject = (String) tmp.get("subject");
            if (subject == null) {
                tmp.put("subject", getModule(ContactModule.class, module).mailText(templateKey + "_subject", null, null));
                systemFacade.save(CONTACT_TEMPLATE_MAP, templateMap);
            }
            subject = (String) tmp.get("body");
            if (subject == null) {
                tmp.put("body", getModule(ContactModule.class, module).mailText(templateKey + "_body", null, null));
                systemFacade.save(CONTACT_TEMPLATE_MAP, templateMap);
            }
            Map tokens = getModule(ContactModule.class, module)
                    .mailTokens(new HashMap(), null, "es", null);
            return new Object[]{tmp, tokens.get(Enum.class)};
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Format DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss Z");
*/
    @Override
    public Map getTEMPLATE_MAP() {
        if (TEMPLATE_MAP == null) {
            TEMPLATE_MAP = systemFacade.getConfig(CONTACT_TEMPLATE_MAP);
            if (TEMPLATE_MAP == null) {
                TEMPLATE_MAP = new HashMap();
            }
        }
        return TEMPLATE_MAP;
    }
/* 
    public static void main(String[] args) {
        for (String d : "dadada".split(" ")) {
            System.out.println(d);
        }
    }
*/
    @Override
    public Object mail(Map m, ContactModule module, String templateKey, String destiny, String language, Map message) {
        //X.log("templateKey=" + templateKey);
        String subject = (String) message.get("subject");
        String content = (String) message.get("content");
        String[] frame = null;
        /*try {
            frame = XFile.loadFile(new File(systemFacade.getUploadFile() + "/templates/mailFrame.htm")).split("!CONTENT");
        } catch (IOException ex) {
            X.log("No se pudo cargar la plantilla de correo en /templates/mailFrame.htm");
        }*/
        if (frame == null) {
            frame = new String[]{"", ""};
        }
//        Properties p = System.getProperties();
//         p.setProperty("proxySet","true");
//         p.setProperty("socksProxyHost","192.168.203.1");
//         p.setProperty("socksProxyPort","8080");
        System.out.println("modele mail=" + module);
        if (module != null) {
            templateKey = templateKey != null ? templateKey : "";
            Map tokens = new HashMap();
            tokens = module.mailTokens(tokens, null, language, m);
            Map tokens2 = new HashMap();
            //Se cambian todos los valores a tipo cadena pero deberia proponerse manejarlos como objetos
            //cambiando el replacede stringutil para usar objeto y no solo cadena
            for (Map.Entry e : (Set<Map.Entry>) tokens.entrySet()) {
                Object o = e.getValue();
                tokens2.put("!" + e.getKey(), o != null ? o.toString() : null);
            }
            Map templateMap = this.getTEMPLATE_MAP();
            Map template = (Map) templateMap.get(templateKey);
            if (template == null) {
                templateMap.put(templateKey, template = new HashMap());
            }
            subject = (String) template.get("subject");
            /*if (subject == null) {
                template.put("subject", subject = X.toText(module.mailText(templateKey + "_subject", language, tokens2)));
                systemFacade.save(CONTACT_TEMPLATE_MAP, templateMap);
            }*/
            /*subject = StringUtils.replace(subject, tokens2);
            content = (String) template.get("body");
            if (content == null) {
                template.put("body", content = module.mailText(templateKey + "_body", language, tokens2));
                systemFacade.save(CONTACT_TEMPLATE_MAP, templateMap);
            }
            content = StringUtils.replace(content, tokens2);*/

        }
        content = content.replace("\n", "<BR/>");
        content = frame[0] + "<div>" + content + "</div>" + frame[1];
        if (destiny == null) {
            return content;
        }
        Properties props = System.getProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.EnableSSL.enable", "true");

        site_mail = (String) systemFacade.getV("site_mail", null);
        System.out.println("");
        Map variables = systemFacade.getConfig("variables");

        site_mail_password = (String) systemFacade.getV("site_mail_password", null);
        System.out.println("site_mail_password=" + site_mail_password);
        if (!XUtil.isEmpty(m.get("site_mail"))) {
            site_mail = (String) m.get("site_mail");
        }
        if (XUtil.isEmpty(site_mail)) {
            System.out.println("Mail service is OFF, site_mail is empty");
            return null;
        }
        if (!XUtil.isEmpty(m.get("site_mail_password"))) {
            site_mail_password = (String) m.get("site_mail_password");
        }
        String host = (String) systemFacade.getV("mail.smtp.host", null);
        //No se envia correo si el host esta vacio
        if (XUtil.isEmpty(host)) {
            System.out.println("ERROR mail.smtp.host IS EMPTY");
            System.out.println("site_mail=" + site_mail);
            System.out.println("site_mail_password=" + site_mail_password);
            return null;
        }
        System.out.println("HOST=" + host);

        props.put("mail.smtp.host", host);
        props.put("mail.debug.auth", "true");
        props.put("mail.debug", "true");

        //Esta fijo pero es para probar la cuenta
        props.put("mail.smtp.host", "smtp-mail.outlook.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable", "true");
        site_mail = "ealarconp@regionancash.gob.pe";
        site_mail_password = "A1_pinedo2021";
        //A1_lookup
        //////////////

        if (!XUtil.isEmpty(m.get("ssl"))) {
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }

        boolean ok = false;

        String s = "";
        System.out.println("destiny=" + destiny);
        /*for (String d : (destiny + " ").split(" ")) {
            for (String dd : d.split(";")) {
                try {
//                            X.log("se enviara a '" + dd + "'");
                    new InternetAddress(dd).validate();
                    s += dd + " ";
                } catch (AddressException ex) {
                    X.log("Mail no valid '" + dd + "'");
                }
            }
        }*/
        destiny = s.trim();
        ok = destiny.length() > 0;

        m.put("ok", ok);
        if (ok) {
            //try {
                /*try {
                    //System.out.println("content="+content);
                    XFile.writeFile("D:\\mail.htm", content);
                } catch (IOException ex) {
                    Logger.getLogger(ContactFacadeImpl.class.getName()).log(Level.SEVERE, null, ex);
                }*/

                /*Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        System.out.println("PasswordAuthentication(" + site_mail + "," + site_mail_password + ")");
                        return new PasswordAuthentication(site_mail, site_mail_password);
                    }
                });

                MimeMessage mimeMessage = new MimeMessage(session);
                for (String d : destiny.split(" ")) {
                    System.out.println("ENVIANDO A " + destiny);
                    mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(d));
                }
                mimeMessage.addHeader("date", DATE_FORMAT.format(new Date()));
                mimeMessage.setSubject(subject);
                mimeMessage.setContent(content, "text/html");
                String from = (String) m.get("from");
                if (from == null) {
                    from = site_mail;//"no-reply@uns.edu.pe";
                }
                mimeMessage.setFrom(new InternetAddress(from, (String) XUtil.isEmpty(variables.get("site_name"), "ISOBIT")));
                Transport transport = session.getTransport("smtp");
                transport.connect();
                transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
                transport.close();
                return message;*/
            //} catch (UnsupportedEncodingException | MessagingException e) {
              //  throw new RuntimeException("ERROR AL ENVIAR MENSAJE props=" + props + ":site_mail_password=" + site_mail_password + ";m=" + m + ";site_mail=" + site_mail, e);
            //}
        } else {
            System.out.println("ERROR NO HAY DESTINOS VALIDOS PARA EL MENSAJE DE CORREO");
            
        }return null;
    }

    @Override
    public void save() {
        //X.log("save " + this.getTEMPLATE_MAP());
        systemFacade.save(CONTACT_TEMPLATE_MAP, this.getTEMPLATE_MAP());
    }

    @Override
    public List getTemplateList() {
        List l = new ArrayList();
        /*List<String> modules = X.getModuleList(ContactFacadeLocal.ContactModule.class);
        for (String module : modules) {

            try {
                getModule(ContactFacadeLocal.ContactModule.class, module).getTemplateList().stream().forEach((o) -> {
                    l.add(new Object[]{module, o});
                });
            } catch (RuntimeException ex) {
                X.log("CONTACT: ERROR AL LISTAR PLANTILLAS DE MODULO '" + module + "' >> " + ex.getMessage());

       
            }
        }*/
        return l;
    }

    @Override
    public void send(Map m) {
        //this.mail(m, null, null, X.toText(m.get("destiny")), "es", m);
    }

}
