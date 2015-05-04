package ServerSide.Utils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author Personal
 */
public class Utils {
    
        public static UserTransaction loadUtx() throws NamingException {
        
            InitialContext ctx = new InitialContext();
            UserTransaction tx = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
        
        return tx;
    }
    /**
     * Metodo para imprimir un mensaje en consola en rosado
     * @param s el mensaje a imprimir
     * @param color
     */
    public static void printf( String s , String color )
    {
        String colorcode="30";
        if(color!=null){
            if(color.equals("red"))
                colorcode="31";
            else if(color.equals("green"))
                colorcode="32";
            else if(color.equals("yellow"))
                colorcode="33";
            else if(color.equals("blue"))
                colorcode="34";
            else if(color.equals("magenta"))
                colorcode="35";
        }
        
       System.out.println( (char)27 + "["+colorcode+"m"+s ) ;
    }
}
