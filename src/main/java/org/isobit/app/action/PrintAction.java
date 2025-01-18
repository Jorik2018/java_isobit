package org.isobit.app.action;

import java.awt.event.ActionEvent;
import java.awt.print.Printable;
import java.util.EventObject;
import org.isobit.app.X;
import org.isobit.swing.XAction;

@ActionTool(icon = "/images/fileprint.png", text = "Imprimir", position = -10001.0D)
public class PrintAction extends XAction {
  public static class PrintEvent extends ActionEvent {
    Printable printable;
    
    public Printable getPrintable() {
      return this.printable;
    }
    
    public void setPrintable(Printable printable) {
      this.printable = printable;
    }
    
    public PrintEvent(Object source, int i, String PRINT, Printable p) {
      super(source, i, PRINT);
      setPrintable(p);
    }
  }
  
  public void actionPerformed(EventObject e) {
    try {
      X.getViews().print(Integer.valueOf(PreviewAction.PRINT));
    } catch (Exception ee) {
      X.alert(ee);
    } 
  }
}
