package org.isobit.app.action;

import java.awt.event.ActionEvent;
import java.util.EventObject;
import java.util.HashMap;
import org.isobit.app.X;
import org.isobit.swing.XAction;
import org.isobit.util.XMap;

@ActionTool(icon="/images/preview-16.png",position=-10002,text="Vista Previa")
public class PreviewAction extends XAction{
    
    public static int PREVIEW=1;
    public static int PRINT=2;
    public static int CONFIGURE=3;

    public interface ReportEngine{
        public void print(Object report) throws Exception;
        public void preview(Object report) throws Exception;
    }

    public static HashMap<String,ReportEngine> reportEngine=new XMap();

    @Override
    public void actionPerformed(EventObject e){
        try{
            //X.getViews().print(PREVIEW);
        }catch(Exception ee){
            //X.alert(ee);
        }
    }

}
