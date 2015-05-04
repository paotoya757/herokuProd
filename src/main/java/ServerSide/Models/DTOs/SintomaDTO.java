/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ServerSide.Models.DTOs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 *
 * @author Personal
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class SintomaDTO {
    
    public static final String[] ESPECIFICACIONES_RECOMENDADAS = {
                                                                  "Vomito" ,                                            // 0
                                                                  "Depresion" ,                                         // 1
                                                                  "Palidez y cambios de temperatura en la cabeza" ,     // 2
                                                                  "Ansiedad",                                           // 3
                                                                  "Insomnio",                                           // 4
                                                                  "Fatiga",                                             // 5
                                                                  "Palpitaciones",                                      // 6
                                                                  "Aura",                                               // 7
                                                                  "Confundido y olvidadiso"                             // 8
                                                                 };
    
    private String especificacion;
    
    public SintomaDTO(){
        
    }
    
     public SintomaDTO(String especificacion){
        this.especificacion=especificacion;
    }

    public String getEspecificacion() {
        return especificacion;
    }

    public void setEspecificacion(String especificacion) {
        this.especificacion = especificacion;
    }

    @Override
    public boolean equals(Object obj) {
        SintomaDTO s = (SintomaDTO) obj;
        if( this.especificacion.equals( s.getEspecificacion() ) )
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        return this.especificacion.hashCode(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
