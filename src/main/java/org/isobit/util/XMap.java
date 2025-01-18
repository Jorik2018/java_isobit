package org.isobit.util;

//import com.google.gson.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.isobit.app.X;

public class XMap<K,V> extends HashMap<K,V>{

    public static final Map EMPTY_MAP=new HashMap(){
        @Override
        public Object put(Object key, Object value){return null;}
    };

    /*public static Object get(JsonObject json, String string) {
        for(Entry<String,JsonElement> entry:json.entrySet()){
            if(entry.getKey().equalsIgnoreCase(string))
                return entry.getValue();
        }
        return null;
    }
    */
    public XMap(Object... o){
        for(int i=0;i<o.length;i++)
            put((K)o[i++],(V)o[i]);
    }
    
    /*public XMap(JsonElement json){
        add((JsonObject)json);
    }*/

    public XMap add(HashMap other){
        this.putAll(other);
        return this;
    }

   
    /*public static HashMap getMap(JsonObject object){
        HashMap p = new HashMap();
        for(Entry<String,JsonElement> ee:object.entrySet()){
            if(ee.getValue().isJsonObject()){
                JsonObject o=ee.getValue().getAsJsonObject();
                if(o.has("class")){
                    try{
                        Object key=ee.getKey();
                        try{
                            key=Class.forName(ee.getKey());
                        }catch(Exception ee2){}
                        Object oe=X.gson.fromJson(o,Class.forName(o.remove("class").getAsString()));
                        p.put(key,oe);
                    }catch(Exception eee){

                    }
                }else{
                    p.put(ee.getKey(),ee.getValue().getAsString());
                }
            }else if(!ee.getValue().isJsonNull())
                p.put(ee.getKey(),ee.getValue().getAsString());
        }
        return p;
    }*/

    int n=0;
    public void add(V value) {
        throw new RuntimeException("add in xmap");
        //put(n++,value);
    }

    public Map set(K k, V v) {
        put(k,v);
        return this;
    }
    
}
