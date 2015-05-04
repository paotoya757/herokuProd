/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerSide.Services;

import ServerSide.Utils.Utils;
import ServerSide.Init.PersistenceManager;
import ServerSide.Init.Stormpath;
import ServerSide.Models.DTOs.CatalizadorDTO;
import ServerSide.Models.DTOs.EpisodioDolorDTO;
import ServerSide.Models.DTOs.MedicamentoDTO;
import ServerSide.Models.DTOs.SintomaDTO;
import ServerSide.Models.Entities.Doctor;
import ServerSide.Models.Entities.EpisodioDolor;
import ServerSide.Models.Entities.Paciente;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.map.ObjectMapper;
import org.fluttercode.datafactory.impl.DataFactory;

/**
 *
 * @author Personal
 */
@Path("/poblar")
@Produces(MediaType.APPLICATION_JSON)
public class PoblarBD {

    // Numero de datos
    static final int num_pacientes = 60;
    static final int num_doctores = 5;
    static final int num_episodios = 300;

    @PersistenceContext(unitName = "myPU")
    EntityManager entityManager;

    DataFactory df;

    private static Client client = Stormpath.createStormPathClient();
    private static Application app = Stormpath.getStormPathApp();

    @PostConstruct
    public void init() {
        try {
            df = new DataFactory();
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
        }
    }

    @GET
    public String poblar() {

        try {
            entityManager.getTransaction().begin();
            poblarPacientes();
            poblarEpisodios();
            poblarDoctores();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            deleteAccounts();
            return "No se poblo la bd";

        }
        return "La bd se poblo con exito";
    }

    @Path("/deleteUsers")
    @GET
    public String deleteAccounts() {
        Stormpath.createStormPathClient();
        AccountList accounts = Stormpath.getStormPathApp().getAccounts();
        Iterator<Account> iter = accounts.iterator();
        while (iter.hasNext()) {
            iter.next().delete();
        }

        return "Las cuentas de stormpath fueron borradas con exito";
    }

    /**
     *
     */
    void poblarDoctores() throws Exception {
        //entityManager.getTransaction().begin();
        for (int i = 0; i < num_doctores; i++) {
            Doctor d = new Doctor();
            d.setName(df.getFirstName() + " " + df.getLastName());
            d.setPassword("Changeme"+i);
            d.setUsername(df.getEmailAddress());

            Account account = client.instantiate(Account.class);
            account.setGivenName(d.getName());
            account.setSurname(d.getName());
            account.setUsername(d.getUsername()); 
            account.setEmail(d.getUsername());
            account.setPassword("Changeme"+i);
            app.createAccount(account);

            entityManager.persist(d);
            entityManager.flush();
            Utils.printf("Poblando doctores : " + i + " / " + num_doctores, "blue");
        }
        //entityManager.getTransaction().commit();
    }

    /**
     *
     */
    void poblarPacientes() throws Exception {
        for (int i = 0; i < num_pacientes; i++) {
            //entityManager.getTransaction().begin();

            Paciente p = new Paciente();
            Integer id = 10000000 + (int) (Math.random() * ((999999999 - 10000000) + 1));
            p.setCedula(id.longValue());
            p.setBirthdate(df.getBirthDate());
            p.setName(df.getFirstName() + " " + df.getLastName());         
            p.setPassword("Changeme"+id);
            p.setUsername(df.getEmailAddress());

            Account account = client.instantiate(Account.class);
            account.setGivenName(p.getName());
            account.setSurname(p.getName());
            account.setUsername(p.getUsername()); 
            account.setEmail(p.getUsername());
            account.setPassword(p.getPassword());
            app.createAccount(account);
            
            entityManager.persist(p);

            //entityManager.getTransaction().commit();
            if (i%10 == 0 ) {
                entityManager.flush();
            }
            Utils.printf("Poblando pacientes : " + i + " / " + num_pacientes, "green");

        }
        System.out.println("");
    }

    /**
     *
     */
    void poblarEpisodios() throws Exception {
        Integer[] maxRows = {0, 1, 2, 3, 4};
        String localizaciones[] = {"frente", "cabeza", "cuello", "ojos", "oidos"};
        String ref[] = {"ibuprofeno", "corticoide", "acetaminofen", "eutirox", "hidroclorotiazida"};

        List<String> c_specs = new ArrayList(Arrays.asList(CatalizadorDTO.ESPECIFICACIONES_RECOMENDADAS));
        List<String> s_specs = new ArrayList(Arrays.asList(SintomaDTO.ESPECIFICACIONES_RECOMENDADAS));

        Query q = entityManager.createQuery("SELECT p from Paciente p");
        List<Paciente> pacs = q.getResultList();
        ObjectMapper mapper = new ObjectMapper();

        for (int i = 0; i < num_episodios; i++) {

            Collections.shuffle(s_specs);
            Collections.shuffle(c_specs);

            //entityManager.getTransaction().begin();
            EpisodioDolor e = new EpisodioDolor();
            e.setFecha(df.getDateBetween(df.getDate(2014, 11, 1), df.getDate(2015, 9, 1)));
            e.setHoursSlept(df.getNumberBetween(3, 10));
            e.setIntensidad(df.getNumberBetween(0, 10));
            e.setLocalizacion(df.getItem(localizaciones, 100));

            int L = df.getItem(maxRows, 100);
            List<CatalizadorDTO> cats = new ArrayList<CatalizadorDTO>();
            for (int j = 0; j < L; j++) {
                CatalizadorDTO c = new CatalizadorDTO();
                c.setEspecificacion(c_specs.get(j));
                cats.add(c);
            }

            L = df.getItem(maxRows, 100);
            List<SintomaDTO> sints = new ArrayList<SintomaDTO>();

            for (int j = 0; j < L; j++) {
                SintomaDTO s = new SintomaDTO(s_specs.get(j));
                sints.add(s);
            }

            List<MedicamentoDTO> meds = new ArrayList<MedicamentoDTO>();
            Integer[] ints = {0, 1};
            L = df.getItem(ints, 100);
            for (int j = 0; j < L; j++) {
                MedicamentoDTO m = new MedicamentoDTO();
                m.setReferencia(df.getItem(ref, 100));
                Date d = new Date(e.getFecha().getTime() - 2 * 365 * 24 * 3600 * 1000);
                String fechaPrescripcion = (new SimpleDateFormat("yyyy-mm-dd")).format(df.getDateBetween(d, e.getFecha()));
                m.setFechaDePrescripcion(fechaPrescripcion);
                meds.add(m);
            }

            e.setCatalizadores(mapper.writeValueAsString(cats));
            e.setSintomas(mapper.writeValueAsString(sints));
            e.setMedicamentos(mapper.writeValueAsString(meds));

            entityManager.persist(e);

            Paciente p = pacs.get(df.getNumberBetween(0, pacs.size() - 1));
            e.setPaciente(p);
            p.getEpisodios().add(e);

            //entityManager.getTransaction().commit();
            if (i%10 == 0) {
                entityManager.flush();
            }
            Utils.printf("Poblando episodios : " + i + " / " + num_episodios, "red");

        }
        System.out.println("");
    }

}
