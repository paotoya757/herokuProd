/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ServerSide.Models.DTOs;

//import com.fasterxml.jackson.annotation.JsonAutoDetect;
//import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
////
/////**
//// *
//// * @author Personal
//// */
//@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class CatalizadorDTO {
    
    public static final String[] ESPECIFICACIONES_RECOMENDADAS = {
                                                                  "Estres" ,                                                // 0
                                                                  "Anticonceptivos" ,                                               // 1
                                                                  "Chocolate" ,                                                // 2
                                                                  "Licor",                                                     // 3
                                                                  "Endulcolorantes artificiales",                               // 4
                                                                  "Citricos",                                                  // 5
                                                                  "Queso curado",                                              // 6
                                                                  "Yogur",                                                     // 7
                                                                  "Pescado",                                                   // 8
                                                                  "Salsa de soja",                                             // 9
                                                                  "Platanos",                                                  // 10
                                                                  "Aguacate",                                                  // 11
                                                                  "Vino tinto",                                                // 12
                                                                  "Esfuerzo fisico",                                            // 13
                                                                  "Estimulo frio(Ej: helado)",            // 14
                                                                  "Luces intensas",                                         // 15
                                                                  "Tabaco",                                                    // 16
                                                                  "Olores fuertes"                                          // 17
                                                                 };
    
    // Recomendar de una lista de especificaciones  ver link : http://www.webconsultas.com/migrana/factores-de-riesgo-de-la-migrana-630
    private String especificacion;
    
    public CatalizadorDTO(){
        
    }

    public CatalizadorDTO(String especificacion){
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
        CatalizadorDTO s = (CatalizadorDTO) obj;
        if( this.especificacion.equals( s.getEspecificacion() ) )
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        return this.especificacion.hashCode(); //To change body of generated methods, choose Tools | Templates.
    }
}
