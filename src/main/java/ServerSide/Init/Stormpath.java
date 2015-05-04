package ServerSide.Init;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.tenant.Tenant;
import java.util.Properties;

/**
 *
 * @author paotoya
 */
public class Stormpath {

    private static Client clnt;
    private static Application app;
    
    public static Client createStormPathClient() {
        if (clnt == null) {
            String path = "src\\main\\webapp\\WEB-INF\\apiKey.properties";
            Properties props = new ApiKeyEnvVariables();
            ApiKey apiKey = ApiKeys.builder().setProperties(props).build();
            clnt = Clients.builder().setApiKey(apiKey).build();
        }
        return clnt;
    }

    public static Application getStormPathApp() {
        if (app == null) {
            Tenant tenant = clnt.getCurrentTenant();
            ApplicationList applications = tenant.getApplications(
                    Applications.where(Applications.name().eqIgnoreCase("MigraineTracking"))
            );

            app = applications.iterator().next();
        }
        return app;
    }
}