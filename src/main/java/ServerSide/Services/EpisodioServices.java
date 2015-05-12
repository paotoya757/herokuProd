/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerSide.Services;

import ServerSide.Converters.EpisodioDolorConverter;
import ServerSide.Init.PersistenceManager;
import ServerSide.Models.DTOs.CatalizadorDTO;
import ServerSide.Models.DTOs.EpisodioDolorDTO;
import ServerSide.Models.DTOs.SintomaDTO;
import ServerSide.Models.Entities.EpisodioDolor;
import ServerSide.Models.Entities.Paciente;
import ServerSide.Utils.DataSecurity;
import ServerSide.Utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.simple.JSONObject;

/**
 *
 * @author Personal
 */
@Path("/episodios")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EpisodioServices {

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
    /**
     * Registra un nuevo episodio La listas de sintomas, medicamentos y
     * catalizadores se convierten en json para ser persistidas
     *
     * @param episodio un dto con todos los atributos
     * @return La lista de sugerencias al paciente segun el analisis del
     * episodio
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registrarEpisodio(EpisodioDolorDTO episodio, @HeaderParam("data_hash") String data_hash) {

        JSONObject respuesta = new JSONObject();
        EpisodioDolor episodioEntity = new EpisodioDolor();
        if( episodio == null ) { System.err.println("JSON NO LLEGA"); } else { episodio.toString(); }
        episodioEntity.setFecha( episodio.getFecha() );
        episodioEntity.setHoursSlept(episodio.getHoursSlept());
        episodioEntity.setIntensidad(episodio.getIntensidad());
        episodioEntity.setLocalizacion(episodio.getLocalizacion());
        episodioEntity.setCatalizadores(toJson(episodio.getCatalizadores()));
        episodioEntity.setMedicamentos(toJson(episodio.getMedicamentos()));
        episodioEntity.setSintomas(toJson(episodio.getSintomas()));

        //#Jetty
        EntityTransaction tran = entityManager.getTransaction();

        //#Glassfish
        //UserTransaction tran = Utils.loadUtx();
        String json = "";
        String nHash = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(episodio);
            ResponseBuilder failure = Response.status(500).header("Access-Control-Allow-Origin", "*");
            if (data_hash != null) {
                if (DataSecurity.verificarIntegridad(json, data_hash) == false) {
                    nHash = DataSecurity.hashCryptoCode(json);
                    Utils.printf("BAD DATA ==> " + json, "red");
                    Utils.printf("nHASH => " + nHash, "red");
                    respuesta.put("Exception message", "Integridad de datos no aprobada , Resultado Operacion => No se agrego el episodio");
                    respuesta.put("json del bkend", json);
                    respuesta.put("hash del json bkend", nHash);
                    return failure.entity(respuesta).build();
                }
            } else {
                respuesta.put("Error :", "No esta mandando el header param \"data_hash\" ");
                return failure.entity(respuesta).build();
            }

            tran.begin();
            //#Glassfish
            //entityManager.joinTransaction();
            entityManager.persist(episodioEntity);
            Paciente p = entityManager.find(Paciente.class, episodio.getCedulaPaciente());
            p.getEpisodios().add(episodioEntity);
            episodioEntity.setPaciente(p);

            tran.commit();
            entityManager.refresh(episodioEntity);

            // Analisis
            String resultadoAnalisis = this.analizarEpisodio(episodio);
            respuesta.put("Mensaje", resultadoAnalisis);
        } catch (Exception t) {

            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            episodioEntity = null;
            return Response.status(500).header("Access-Control-Allow-Origin", "*").entity(respuesta).build();

        } finally {
            entityManager.clear();
            entityManager.close();
        }

        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(respuesta).build();

    }

    //--------------------------------------------------------------------------
    // GET
    //--------------------------------------------------------------------------
    /**
     * Retorna los detalles de un episodio particular
     *
     * @param id el id del episodio
     * @return un episodio de dolor dado el id
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDetalles(@PathParam("id") Long id) throws IOException {
        try {
            EpisodioDolor episodio = entityManager.find(EpisodioDolor.class, id);
            EpisodioDolorDTO dto = EpisodioDolorConverter.entityDetailToDto(episodio);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(dto);
            String data_hash = DataSecurity.hashCryptoCode(json);
            return Response.status(200).header("data_hash", data_hash).header("Access-Control-Allow-Origin", "*").entity(dto).build();
        } catch (Exception e) {
            EpisodioDolorDTO dto = new EpisodioDolorDTO();
            dto.setLocalizacion("Excepciooonnn: " + e.getMessage());
            return Response.status(500).header("Access-Control-Allow-Origin", "*").entity(dto).build();
        }

    }

    /**
     * Retorna los episodios de dolor de un paciente en particular, entre dos
     * fechas
     *
     * @param cedula
     * @param id La cedula del paciente
     * @param fecha1 La fecha inicial del intervalo, se pasa de string a date
     * para ejecutar el query
     * @param fecha2 La fecha final del intervalo, se pasa de string a date para
     * ejecutar el query
     * @return Una lista de episodios que cumplen con los parametros
     */
    @GET
    @Path("/{id}/{fecha1}/{fecha2}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBetweenFechas(@PathParam("id") Long cedula, @PathParam("fecha1") Long fecha1, @PathParam("fecha2") Long fecha2) throws IOException {
        try {
            Date f1 = new Date(fecha1);
            Date f2 = new Date(fecha2);
            Query q = entityManager.createQuery("SELECT e FROM EpisodioDolor e WHERE e.paciente.cedula=:ced AND :date1 <= e.fecha AND e.fecha <= :date2");
            q.setParameter("date1", f1);
            q.setParameter("date2", f2);
            q.setParameter("ced", cedula);
            List<EpisodioDolor> eps = q.getResultList();

            List<EpisodioDolorDTO> dtos = EpisodioDolorConverter.entityToDtoList(eps);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(dtos);
            String data_hash = DataSecurity.hashCryptoCode(json);
            return Response.status(200)
                    .header("data_hash", data_hash)
                    .header("Access-Control-Allow-Origin", "*")
                    .entity(dtos)
                    .build();
        } catch (Exception e) {
            EpisodioDolorDTO empty = new EpisodioDolorDTO();
            empty.setLocalizacion("Exepcion: " + e.getMessage());
            return Response.status(500)
                    .header("Access-Control-Allow-Origin", "*")
                    .entity(empty)
                    .build();
        }

    }

    //--------------------------------------------------------------------------
    // Metodos Complementarios
    //--------------------------------------------------------------------------
    /**
     * Analiza un episodio de dolor
     *
     * @param episodio - Episodio a analizar
     * @return Un mensaje con las recomendaciones.
     */
    private String analizarEpisodio(EpisodioDolorDTO episodio) {

        String[] recomendaciones = {
            "Utilice una toalla pequeña empapada con agua caliente, una almohada térmica o tome un baño de tina con agua tibia. Aplique calor sobre el área por 20 a 30 minutos cada 2 horas por los días indicado por su proveedor de salud. Alterne entre el calor y el hielo.",
            "Acuéstese en una posición cómoda y cierre sus ojos. Relaje sus músculos lentamente. Comience por los dedos de los pies y avance hacia arriba al resto de su cuerpo.",
            "Comuniquese con su Doctor inmediatamente.",
            "Dirigase a una sala de emergencias para recibir atencion inmediata"
        };

        String msj = "Se le recomienda realizar las siguientes acciones: \n %1$s \n\nEs posible que su dolor se haya aumentado al consumir,realizar, o verse expuesto a las siguientes:\n %2$s ";

        String r = "";
        String e = "";
        String[] s_specs = SintomaDTO.ESPECIFICACIONES_RECOMENDADAS;
        String[] c_specs = CatalizadorDTO.ESPECIFICACIONES_RECOMENDADAS;

        if (episodio.getIntensidad() > 3 && episodio.getSintomas().contains(new SintomaDTO("Vomito"))) {
            r = recomendaciones[2];
        } else if (episodio.getIntensidad() > 6 || (episodio.getIntensidad() > 3 && episodio.getIntensidad() < 7 && episodio.getSintomas().contains(new SintomaDTO(s_specs[8])))) {
            r = recomendaciones[3];
        } else if (episodio.getIntensidad() < 4) {
            r += recomendaciones[0] + "\n" + recomendaciones[1];
        }

        for (int i = 0; i < c_specs.length; i++) {
            String tmp = c_specs[i];
            if (episodio.getCatalizadores().contains(new CatalizadorDTO(tmp))) {
                e += " - " + tmp + "\n";
            }
        }

        return String.format(msj, r, e);
    }

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
