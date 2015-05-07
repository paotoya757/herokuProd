
package ServerSide.Init;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;


@ApplicationPath("/webresources")
public class WebApp extends ResourceConfig {
    public WebApp(){
        register(JacksonFeature.class);    
        packages("ServerSide.Services");
        packages("ServerSide.User");
    }
}
