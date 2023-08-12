package org.isobit.app.model;

import java.io.*;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false,onlyExplicitlyIncluded = true)
@Entity
@Table(name = "dru_cache")
@NamedQueries({
    @NamedQuery(name = "Cache.findAll", query = "SELECT c FROM Cache c"),
    @NamedQuery(name = "Cache.findByCid", query = "SELECT c FROM Cache c WHERE c.cid = :cid"),
    @NamedQuery(name = "Cache.findByExpire", query = "SELECT c FROM Cache c WHERE c.expire = :expire"),
    @NamedQuery(name = "Cache.findByCreated", query = "SELECT c FROM Cache c WHERE c.created = :created"),
    @NamedQuery(name = "Cache.findBySerialized", query = "SELECT c FROM Cache c WHERE c.serialized = :serialized")})
public class Cache implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @EqualsAndHashCode.Include()
    @Basic(optional = false)
    @Column(name = "cid")
    private String cid;
    @Basic(optional = false)
//    @Lob
    @Column(name = "data")
    private byte[] data;
    @Basic(optional = false)
    @Column(name = "expire")
    private int expire;
    @Basic(optional = false)
    @Column(name = "created")
    private int created;
    @Basic(optional = false)
    @Column(name = "headers")
    private String headers="";
    @Basic(optional = false)
    @Column(name = "serialized")
    private short serialized;

    /*
    public static Object serialize(ServletRequest request,String name,Object obj) throws SQLException, IOException{
        Connection cnx=_JDBC.getCnx(request);
        ResultSet rs=cnx.createStatement().executeQuery("SELECT cid FROM dru_cache WHERE cid='"+name+"'");
        PreparedStatement pstmt=rs.next()?cnx.prepareStatement("UPDATE dru_cache SET data=? WHERE cid=?"):cnx.prepareStatement("INSERT INTO dru_cache(data,cid,headers,serialized) VALUES (?,?,'',1)");
        //pstmt.setObject(1, objectToSerialize);
        pstmt.setString(2,name);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(baos);
        oout.writeObject(obj);
        oout.close();
        // This will NOT work in JDBC-ODBC bridge under JDK 1.2.2
        // as soon as the size of the byte array is bigger than 2000
        pstmt.setBytes(1, baos.toByteArray());
        pstmt.executeUpdate();
        pstmt.close();
        return obj;
    }*/
/*
    public static Object deserialize(Connection cnx,String name) throws SQLException, IOException, ClassNotFoundException{
        return deserialize(cnx,name,null);
    }

    public static Object deserialize(Connection cnx,String name,Object data) throws SQLException, IOException,ClassNotFoundException {
        PreparedStatement pstmt =cnx.prepareStatement("SELECT data FROM dru_cache WHERE cid=?");
        pstmt.setString(1,name);
        ResultSet rs = pstmt.executeQuery();
        if(rs.next()){
            byte[] buf =rs.getBytes(1);
            if(buf!=null)data=new ObjectInputStream(new ByteArrayInputStream(buf)).readObject();
            rs.close();
        }
        pstmt.close();
        return data;
    }*/

    public Object deserialize(){
        try{
//            try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
//         ObjectInput in = new ObjectInputStream(bis)) {
//        return in.readObject();
//    } 
            
            return data!=null?new ObjectInputStream(new ByteArrayInputStream(data)).readObject():null;
        }catch(Exception e){
            
            throw new java.lang.RuntimeException(e);
        }
    }

}
