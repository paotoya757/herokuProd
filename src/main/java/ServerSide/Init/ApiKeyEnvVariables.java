/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerSide.Init;



   import java.util.Properties;
/**
 * 
 * @author paoto
 */
   public class ApiKeyEnvVariables extends Properties {

       public ApiKeyEnvVariables() {
//#######For Heroku
           
//           super.put("apiKey.id", System.getenv("STORMPATH_API_KEY_ID"));
//           super.put("apiKey.secret", System.getenv("STORMPATH_API_KEY_SECRET"));
           
//#######For Local
           
           super.put("apiKey.id", "72GYW2GXXORJNL2E35V26AFWD");
           super.put("apiKey.secret", "WkOXOjXoi0MsOKV9A0KK+AvUrP1gSh2FjapHMWcz0c4");
       }
   }
