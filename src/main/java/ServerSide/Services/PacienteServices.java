/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ServerSide.Services;

import ServerSide.Converters.EpisodioDolorConverter;
import ServerSide.Converters.PacienteConverter;
import ServerSide.Init.PersistenceManager;
import ServerSide.Models.DTOs.PacienteDTO;
import ServerSide.Models.Entities.Doctor;
import ServerSide.Models.Entities.EpisodioDolor;
import ServerSide.Models.Entities.Paciente;
import ServerSide.Utils.Utils;
import com.google.gson.Gson;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
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
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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
     * Atributo del entity manager
     * Unidad de persistencia, "myPU"
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
    public void init(){
        try{
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("No se incializo correctamente!!!");
        }
    }
    
    //--------------------------------------------------------------------------
    // POST
    //--------------------------------------------------------------------------
    
    /**
     * Registra el paciente con la informacion dada
     * @param paciente la informacion de paciente
     * @return 
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registrarPaciente(PacienteDTO paciente) throws JSONException{
        Paciente p = PacienteConverter.dtoToEntity(paciente);
        JSONObject respuesta = new JSONObject();
        try{
           entityManager.getTransaction().begin();
           
           entityManager.persist(p);
           Doctor doc = this.entityManager.find( Doctor.class, paciente.getDoctorid() );
           doc.getPacientes().add( p );
           
           entityManager.getTransaction().commit();
           entityManager.refresh(p);
           respuesta.put("New_paciente_id", p.getCedula() );
       }
       catch(Throwable t){
          t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
          p=null;  
          respuesta.put( "Exception message", t.getMessage() );
          return Response.status(500).header("Access-Control-Allow-Origin", "*").entity(respuesta).build();
       }
       finally{
           entityManager.clear();
           entityManager.close();
       }
       
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(respuesta).build();
    }
    
    //--------------------------------------------------------------------------
    // GET
    //--------------------------------------------------------------------------
   
    /**
     * Retorna los detalles de un paciente en particular
     * @param cedula el numero de cedula del paciente
     * @return la informacion de paciente con el numero de cedula dado
     */
    @Path("/{id}")
    @GET
    public Response findById( @PathParam("id") Long cedula ){
        Paciente paciente = entityManager.find(Paciente.class, cedula);
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity( PacienteConverter.entityToDto(paciente) ).build();
    }
    
    /**
     * La lista de pacientes todos los pacientes registrados en la aplicacion
     * @return los pacientes registrados en la aplicacion
     */
    @GET
    public Response getAll(){
       Query q = entityManager.createQuery("SELECT u FROM Paciente u");
       List<Paciente> pacientes = q.getResultList();
       return Response.status(200).header("Access-Control-Allow-Origin", "*").entity( PacienteConverter.entityToDtoList(pacientes) ).build();
        
    }
    
    /**
     * La lista de episodios de un paciente en particular
     * @param cedula el numero de cedula del paciente
     * @return la lista de episodios del paciente con numero de cedula
     */
    @Path("/episodios/{cedula}")
    @GET
    public Response getEpisodiosByPaciente(@PathParam("cedula") Long cedula){
        
        Query q = entityManager.createQuery("SELECT u FROM EpisodioDolor u WHERE u.paciente.cedula = :cedula");
        q.setParameter("cedula", cedula);
        List<EpisodioDolor> episodios = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity( EpisodioDolorConverter.entityToDtoList(episodios) ).build();
        
    }
    
    //--------------------------------------------------------------------------
    // Metodos Complementarios
    //--------------------------------------------------------------------------
    
    /**
     * Convierte una lista a JSON
     * @param lista una lista con objetos
     * @return un string con el json de la lista que entra por parametro
     */
    public String toJson(List lista){
        return new Gson().toJson(lista);
    }
}
