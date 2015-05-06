/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerSide.Services;

import ServerSide.Converters.DoctorConverter;
import ServerSide.Init.PersistenceManager;
import ServerSide.Init.Stormpath;

import ServerSide.Models.DTOs.DoctorDTO;
import ServerSide.Models.DTOs.EpisodioDolorDTO;

import ServerSide.Models.DTOs.DoctorDTO;
import ServerSide.Models.Entities.Doctor;
import ServerSide.Utils.DataSecurity;
import com.google.gson.Gson;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.client.Client;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Personal
 */
@Path("/doctores")
@Produces(MediaType.APPLICATION_JSON)
public class DoctorServices {

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
    // POST
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // GET
    //--------------------------------------------------------------------------
    /**
     * Los detalles de un doctor
     *
     * @param id el numero de cedula del doctor
     * @return la informacion de un doctor en particular
     */
    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) throws IOException {
        Doctor doctor = entityManager.find(Doctor.class, id);
        DoctorDTO dto = DoctorConverter.entityToDto(doctor);
        
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dto);
        String data_hash = DataSecurity.hashCryptoCode(json);
        return Response.status(200)
                .header("data_hash", data_hash)
                .header("Access-Control-Allow-Origin", "*")
                .entity(dto)
                .build();
    }

    /**
     * La lista de todos los doctores registrados en la aplicacion
     *
     * @return todos los doctores registrados
     */
    @GET
    public Response findAll() throws IOException {
        Query q = entityManager.createQuery("select u from Doctor u ");
        List<Doctor> doctors = q.getResultList();
        List<DoctorDTO> dtos = DoctorConverter.entityToDtoList(doctors);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dtos);
        String data_hash = DataSecurity.hashCryptoCode(json);
        return Response.status(200)
                .header("data_hash", data_hash)
                .header("Access-Control-Allow-Origin", "*")
                .entity(dtos)
                .build();

    }

    //--------------------------------------------------------------------------
    // Metodos Complementarios
    //--------------------------------------------------------------------------
    /**
     * Convierte una lista a JSON
     *
     * @param lista una lista con objetos
     * @return un string con el json de la lista que entra por parametro
     */
    public String toJson(List lista) {
        return new Gson().toJson(lista);
    }

}
