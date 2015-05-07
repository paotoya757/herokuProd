/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ServerSide.Models.DTOs;

//import com.fasterxml.jackson.annotation.JsonAutoDetect;
//import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
//import java.util.Date;
//
///**
// *
// * @author Personal
// */
//@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class MedicamentoDTO {
    
    private String referencia;
       
    private String fechaDePrescripcion;
    
    
    public MedicamentoDTO(){
        
    }

    
    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getFechaDePrescripcion() {
        return fechaDePrescripcion;
    }

    public void setFechaDePrescripcion(String fechaDePrescripcion) {
        this.fechaDePrescripcion = fechaDePrescripcion;
    }
    
    
}
