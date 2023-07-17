package org.isobit.util;

import java.util.List;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import org.isobit.app.X;

public class RandomUtil {

    private static String A="aeiou";
    private static String L="bcdfghjklmnpqrstvwxyz";
    
    public static String getW(int wordLength,boolean spaces) {
    	return getW(wordLength, spaces,wordLength);

    }
    
    public static String getW(int wordLength,boolean spaces,int minWord) {
        Random r = new Random(); // Intialize a Random Number Generator with SysTime as the seed
        StringBuilder sb = new StringBuilder(wordLength);
        int s=(int)Math.ceil(8*Math.random())+4;
        int c=(int)Math.ceil(Math.random());
        if(wordLength>minWord) wordLength=(int) getNumber(minWord,wordLength,0);
        for(int i =0;i<wordLength;i++){ // For each letter in the word
            c++;
            if(c%2==1)
                sb.append(A.charAt((int)(A.length()*Math.random())));
            else
                sb.append(L.charAt((int)(L.length()*Math.random())));
            if(spaces&&c==s){
                sb.append(' ');
                s=c+(int)Math.ceil(6*Math.random())+6;
            }
        }
        return sb.toString();
    }

    public static String getCode(int i) {
        return UUID.randomUUID().toString().substring(0,i);
    }
    
    public static double getNumber(int max,int min,int decimal){
        return min+Math.random()*(max-min);
    }
    
    public static Object get(List l){
        return l.get((int)getNumber(0,l.size()-1,0));
    }
    
    public static Date getDate(){
        return new Date(1981+(int)(Math.random()*34),1+(int)(Math.random()*11),1+(int)(Math.random()*28));
    }
    
    public static void main(String[] args) {
        for(int i=0;i<100;i++)
        X.log((int)RandomUtil.getNumber(1, 5, 0));
    }
}
