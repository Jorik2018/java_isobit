package org.isobit.swing;

import java.util.EventObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.isobit.app.X;
import org.isobit.app.jpa.User;

public abstract class XAction{

    protected transient X servlet=null;

    public X getServlet(){
        return (this.servlet);
    }

    public void setServlet(X servlet){
        this.servlet=servlet;
        // :FIXME: Is this suppose to release resources?
    }

    private Object name;

    public Object getName(){
        return name;
    }

    public void setName(Object name){
        this.name = name;
    }
    
    public static XAction redirect(HttpServletRequest request, HttpServletResponse response, String url){
        return redirect(request, response, url, false);
    }

    public static XAction redirect(HttpServletRequest request, HttpServletResponse response, String url,boolean absolute){
        HttpSession session=request.getSession();
        session.setAttribute("#msg",request.getAttribute("#msg"));
       /* if(!absolute)url=X.url(url);
        try{
//            if(DEBUG)print("--------------------->redireccionando a "+url);
            response.sendRedirect(url);
        }catch(Exception e){
            X.alert(request,e);
        }
        request.setAttribute("#die",true);*/
        return null;
    }

    public static int iconSize=32;
    
    public static final String BUTTON="button";
    
    public Object render(Object obj){return null;}

    public void actionPerformed(EventObject e){}

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception{
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public String getComponent(HttpServletRequest request, HttpServletResponse response,User user){
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
