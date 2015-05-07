/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ServerSide.Models.DTOs;

import java.util.Date;
import java.util.List;


//import com.fasterxml.jackson.annotation.JsonAutoDetect;
//import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
//import java.util.Date;
//import java.util.List;
//
///**
// *
// * @author Personal
// */
//@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class EpisodioDolorDTO {
    
    private Long id;
    
    private Date fecha;
    
    private String localizacion;
    
    // Del 1 al 10...
    private Integer intensidad;
    
    private Integer hoursSlept;
    
    private Long cedulaPaciente;
    
    // <!-- Ojo: Estas listas se persisten como JSONArray >
    
    private List<SintomaDTO> sintomas;
    
    private List<CatalizadorDTO> catalizadores;
    
    private List<MedicamentoDTO> medicamentos;
    
    //----------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------
    public EpisodioDolorDTO(){
        
    }
    
    //----------------------------------------------------------------------
    // Getters and Setters
    //----------------------------------------------------------------------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCedulaPaciente() {
        return cedulaPaciente;
    }

    public void setCedulaPaciente(Long cedulaPaciente) {
        this.cedulaPaciente = cedulaPaciente;
    }
    
    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    public Integer getIntensidad() {
        return intensidad;
    }

    public void setIntensidad(Integer intensidad) {
        this.intensidad = intensidad;
    }

    public Integer getHoursSlept() {
        return hoursSlept;
    }

    public void setHoursSlept(Integer hoursSlept) {
        this.hoursSlept = hoursSlept;
    }


    public List<SintomaDTO> getSintomas() {
        return sintomas;
    }

    public void setSintomas(List<SintomaDTO> sintomas) {
        this.sintomas = sintomas;
    }

    public List<CatalizadorDTO> getCatalizadores() {
        return catalizadores;
    }

    public void setCatalizadores(List<CatalizadorDTO> catalizadores) {
        this.catalizadores = catalizadores;
    }

    public List<MedicamentoDTO> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<MedicamentoDTO> medicamentos) {
        this.medicamentos = medicamentos;
    }
    
    
}
