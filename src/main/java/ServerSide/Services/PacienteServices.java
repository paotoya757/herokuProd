/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerSide.Services;

import ServerSide.Converters.EpisodioDolorConverter;
import ServerSide.Converters.PacienteConverter;
import ServerSide.Init.PersistenceManager;
import ServerSide.Models.DTOs.EpisodioDolorDTO;
import ServerSide.Models.DTOs.PacienteDTO;
import ServerSide.Models.Entities.EpisodioDolor;
import ServerSide.Models.Entities.Paciente;
import ServerSide.Utils.DataSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Personal
 */
@Path("/pacientes")
@Produces(MediaType.APPLICATION_JSON)
public class PacienteServices {

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
     * Retorna los detalles de un paciente en particular
     *
     * @param cedula el numero de cedula del paciente
     * @return la informacion de paciente con el numero de cedula dado
     */
    @Path("/{id}")
    @GET
    public Response findById(@PathParam("id") Long cedula) throws IOException {
        try {
            Paciente paciente = entityManager.find(Paciente.class, cedula);
            PacienteDTO dto = PacienteConverter.entityToDto(paciente);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(dto);
            String data_hash = DataSecurity.hashCryptoCode(json);
            return Response.status(200)
                    .header("data_hash", data_hash)
                    .header("Access-Control-Allow-Origin", "*").entity(dto).build();
        } catch (Exception e) {
            PacienteDTO dto = new PacienteDTO();
            dto.setName("Excepcion : " + e.getMessage());
            return Response.status(500)
                    .header("Access-Control-Allow-Origin", "*").entity(dto).build();
        }

    }

    /**
     * La lista de pacientes todos los pacientes registrados en la aplicacion
     *
     * @return los pacientes registrados en la aplicacion
     */
    @GET
    public Response getAll() throws IOException {
        Query q = entityManager.createQuery("SELECT u FROM Paciente u");
        List<Paciente> pacientes = q.getResultList();
        List<PacienteDTO> dtos = PacienteConverter.entityToDtoList(pacientes);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dtos);
        String data_hash = DataSecurity.hashCryptoCode(json);
        return Response.status(200)
                .header("data_hash", data_hash)
                .header("Access-Control-Allow-Origin", "*").entity(dtos).build();

    }

    /**
     * La lista de episodios de un paciente en particular
     *
     * @param cedula el numero de cedula del paciente
     * @return la lista de episodios del paciente con numero de cedula
     */
    @Path("/episodios/{cedula}")
    @GET
    public Response getEpisodiosByPaciente(@PathParam("cedula") Long cedula) throws IOException {
        try {
            Query q = entityManager.createQuery("SELECT u FROM EpisodioDolor u WHERE u.paciente.cedula = :cedula");
            q.setParameter("cedula", cedula);
            List<EpisodioDolor> episodios = q.getResultList();
            List<EpisodioDolorDTO> dtos = EpisodioDolorConverter.entityToDtoList(episodios);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(dtos);
            String data_hash = DataSecurity.hashCryptoCode(json);
            return Response.status(200)
                    .header("data_hash", data_hash)
                    .header("Access-Control-Allow-Origin", "*").entity(dtos).build();
        } catch (Exception e) {
            EpisodioDolorDTO ep = new EpisodioDolorDTO();
            ep.setLocalizacion("Excepcion : " + e.getMessage());
            return Response.status(500)
                    .header("Access-Control-Allow-Origin", "*").entity(ep).build();
        }

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
