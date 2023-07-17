package org.isobit.util;

@javax.ejb.ApplicationException(rollback = true)
public class SimpleException extends RuntimeException{

    boolean reported=false;
    private Object extra;

    public Object getExtra() {
        return extra;
    }
    
    public SimpleException(String msg) {
        super(msg);
    }

    public SimpleException(String e,Object extra) {
        super(e);
        this.extra=extra;
    }
        
    public SimpleException(String e,boolean reported) {
        this(new RuntimeException(e),reported);
    }
        
    public SimpleException(Throwable e,boolean reported) {
        super(e);
        this.reported=reported;
    }

    public boolean isReported() {
        return reported;
    }

    @Override
    public String toString() {
        return super.toString()+(reported?"[Reported]":"");
    }
    
}
