/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ServerSide.Models.Entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Personal
 */
@Entity
public class Paciente implements Serializable {
    private static final long serialVersionUID = 1L;
    //-------------------------------------------------------------------------
    // Attributes
    //-------------------------------------------------------------------------
    @Id
    private Long cedula;
    
    private String username;
    
    private String password;
    
    private String name;
    
    @Temporal(TemporalType.DATE)
    private Date birthdate;
    
    @OneToMany(mappedBy = "paciente",cascade = CascadeType.ALL)
    private List<EpisodioDolor> episodios;
    
    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------
    public Paciente(){
        
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------
    
    public Long getCedula() {
        return cedula;
    }

    public void setCedula(Long cedula) {
        this.cedula = cedula;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public List<EpisodioDolor> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<EpisodioDolor> episodios) {
        this.episodios = episodios;
    }
    
    
    
}
