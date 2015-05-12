/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerSide.User;

import ServerSide.Converters.PacienteConverter;
import ServerSide.Init.ApiKeyEnvVariables;
import ServerSide.Init.PersistenceManager;
import ServerSide.Init.Stormpath;
import ServerSide.Models.DTOs.DoctorDTO;
import ServerSide.Models.DTOs.PacienteDTO;
import ServerSide.Models.Entities.Doctor;
import ServerSide.Models.Entities.Paciente;
import ServerSide.Utils.DataSecurity;
import ServerSide.Utils.Utils;
import co.edu.uniandes.csw.miso4204.security.jwt.api.JsonWebToken;
import co.edu.uniandes.csw.miso4204.security.jwt.api.JwtHashAlgorithm;
import co.edu.uniandes.csw.miso4204.security.logic.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.tenant.Tenant;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.simple.JSONObject;

/**
 *
 * @author estudiante
 */
@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserService {

    //--------------------------------------------------------------------------
    // Atributos
    //--------------------------------------------------------------------------
    /**
     * Atributo del entity manager Unidad de persistencia, "myPU"
     */
    @PersistenceContext(unitName = "myPU")
    EntityManager entityManager;

    //--------------------------------------------------------------------------
    // INIT
    //--------------------------------------------------------------------------
    /**
     * Inicializa el entity manager
     */
    @PostConstruct
    public void init() {
        try {
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No se incializo correctamente!!!");
        }
    }

    //--------------------------------------------------------------------------
    // Metodos
    //--------------------------------------------------------------------------
    @Path("/login")
    @POST
    public Response login(UserDTO user) {

        int status = 500; //Codigo de error en el servidor
        String token = "User and/or password wrong";
        UserDTO userStorm = new UserDTO();
        String path = "src\\main\\webapp\\WEB-INF\\apiKey.properties";//Colocar la Ubicacion de su archivo apiKey.properties
        Properties props = new ApiKeyEnvVariables();
        ApiKey apiKey = ApiKeys.builder().setProperties(props).build();
        Client client = Clients.builder().setApiKey(apiKey).build();

        try {
            AuthenticationRequest request = new UsernamePasswordRequest(user.getUsername(), user.getPassword());
            Tenant tenant = client.getCurrentTenant();
            ApplicationList applications = tenant.getApplications(Applications.where(Applications.name().eqIgnoreCase("MigraineTracking")));
            com.stormpath.sdk.application.Application application = applications.iterator().next();

            AuthenticationResult result = application.authenticateAccount(request);
            Account account = result.getAccount();
            userStorm.setEmail(account.getEmail());
            userStorm.setName(account.getFullName());
            userStorm.setUsername(account.getUsername());
            userStorm.setPassword(user.getPassword());
            userStorm.setLevelAccess(user.getLevelAccess());
            token = new Gson().toJson(JsonWebToken.encode(userStorm, "Un14nd3s2014@", JwtHashAlgorithm.HS256));
            status = 200;

        } catch (ResourceException ex) {
            Response.status(500).header("Access-Control-Allow-Origin", "*").entity(token).build();
            System.out.println(ex.getStatus() + " " + ex.getMessage());
        }

        return Response.ok().header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept").entity(token).build();
    }

    /**
     * Registra un nuevo paciente en el sistema y en STORMPATH
     *
     * @param paciente
     * @param data_hash
     * @return
     */
    @Path("/new/paciente")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registrarPaciente(PacienteDTO paciente, @HeaderParam("data_hash") String data_hash) {

        Paciente p = PacienteConverter.dtoToEntity(paciente);
        JSONObject respuesta = new JSONObject();

        //#Jetty
        EntityTransaction tran = entityManager.getTransaction();
        //#Glassfish
        //UserTransaction tran = Utils.loadUtx();
        try {

            //Integrity check
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(paciente);
            ResponseBuilder failure = Response.status(500).header("Access-Control-Allow-Origin", "*");
            if (data_hash != null) {
                if (DataSecurity.verificarIntegridad(json, data_hash) == false) {
                    String nHash = DataSecurity.hashCryptoCode(json);
                    respuesta.put("json del bkend", json);
                    respuesta.put("hash del json bkend", nHash);
                    respuesta.put("Exception Message", "Integridad de datos => no aprobada , Resultado Operacion => No se agrego el doctor");
                    return failure.entity(respuesta).build();
                } else {
                    respuesta.put("Integridad de datos", "Aprobada");
                }
            } else {
                respuesta.put("Error", "No esta mandando el header param \"data_hash\" , Resultado Operacion => no se agrego el doctor");
                return failure.entity(respuesta).build();
            }

            //#Glassfish
            //entityManager.joinTransaction();
            entityManager.persist(p);
            Doctor doc = this.entityManager.find(Doctor.class, paciente.getDoctorid());
            doc.getPacientes().add(p);

            Client client = Stormpath.createStormPathClient();
            Account account = client.instantiate(Account.class);
            tran.begin();

            account.setEmail(paciente.getUsername());
            account.setGivenName(paciente.getName());
            account.setPassword(paciente.getPassword());
            account.setUsername(paciente.getUsername());
            account.setSurname(paciente.getName());
            if (!paciente.getUsername().contains("@")) {
                throw new Exception("El username no es un correo electronico valido");
            }
            Stormpath.getStormPathApp().createAccount(account);

            tran.commit();

            entityManager.refresh(p);
            respuesta.put("added_paciente_id", p.getCedula());
        } catch (Exception t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            p = null;
            respuesta.put("Exception Message", t.getMessage());
            return Response.status(500).header("Access-Control-Allow-Origin", "*").entity(respuesta).build();
        } finally {
            entityManager.clear();
            entityManager.close();
        }

        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(respuesta).build();
    }

    /**
     * Registra un nuevo doctor en el sistema y en STORMPATH
     *
     * @param
     * @return
     * @throws JSONException
     */
    @Path("/new/doctor")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registrarDoctor(DoctorDTO doctor, @HeaderParam("data_hash") String data_hash) {

        JSONObject respuesta = new JSONObject();
        Doctor doctorEntity = new Doctor();

        //#Jetty
        EntityTransaction tran = entityManager.getTransaction();
        //#Glassfish
        //UserTransaction tran = Utils.loadUtx();

        doctorEntity.setName(doctor.getName());
        doctorEntity.setUsername(doctor.getUsername());
        doctorEntity.setPassword(DataSecurity.hashCryptoCode(doctor.getPassword()));
        try {

            //Integrity check
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(doctor);
            ResponseBuilder failure = Response.status(500).header("Access-Control-Allow-Origin", "*");
            if (data_hash != null) {
                if (DataSecurity.verificarIntegridad(json, data_hash) == false) {
                    String nHash = DataSecurity.hashCryptoCode(json);
                    respuesta.put("json del bkend", json);
                    respuesta.put("hash del json bkend", nHash);
                    respuesta.put("Exception Message", "Integridad de datos => no aprobada , Resultado Operacion => No se agrego el doctor");
                    return failure.entity(respuesta).build();
                } else {
                    respuesta.put("Integridad de datos", "Aprobada");
                }
            } else {
                respuesta.put("Error", "No esta mandando el header param \"data_hash\" , Resultado Operacion => no se agrego el doctor");
                return failure.entity(respuesta).build();
            }

            tran.begin();

            //#Glassfish
            //entityManager.joinTransaction();
            entityManager.persist(doctorEntity);

            Client client = Stormpath.createStormPathClient();
            Account account = client.instantiate(Account.class);

            account.setEmail(doctor.getUsername());
            account.setGivenName(doctor.getName());
            account.setPassword(doctor.getPassword());
            account.setUsername(doctor.getUsername());
            account.setSurname(doctor.getName());
            if (!doctor.getUsername().contains("@")) {
                throw new Exception("El username no es un correo electronico valido");
            }
            Stormpath.getStormPathApp().createAccount(account);

            tran.commit();

            entityManager.refresh(doctorEntity);
            respuesta.put("added_doctor_id", doctorEntity.getId());

        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            doctorEntity = null;
            respuesta.put("Exception message", t.getMessage());
            return Response.status(500).header("Access-Control-Allow-Origin", "*").entity(respuesta).build();
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(respuesta).build();
    }

}
