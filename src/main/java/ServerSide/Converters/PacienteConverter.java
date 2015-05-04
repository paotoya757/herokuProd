/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ServerSide.Converters;

import ServerSide.Models.DTOs.PacienteDTO;
import ServerSide.Models.Entities.Paciente;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Personal
 */
public class PacienteConverter {

     public static Paciente dtoToEntity(PacienteDTO dto){
        
       Paciente entity = new Paciente();
       entity.setCedula( dto.getCedula() );
       entity.setName( dto.getName() );
       //entity.setPassword( dto.getPassword() );
       entity.setUsername( dto.getUsername() );
       entity.setBirthdate( dto.getBirthdate() );
       
       return entity ;
    }
    
    public static PacienteDTO entityToDto(Paciente entity ){
        PacienteDTO dto = new PacienteDTO();
        dto.setCedula(entity.getCedula());
        dto.setName( entity.getName() );
        dto.setPassword( entity.getPassword() );
        dto.setUsername( entity.getUsername() );
        dto.setBirthdate( entity.getBirthdate() );
        return dto ;
    }
    
    public static List<PacienteDTO> entityToDtoList(List<Paciente> entities) {
        List<PacienteDTO> dtos = new ArrayList<PacienteDTO>();
        for (int i = 0; i < entities.size(); i++) {
            dtos.add( entityToDto( entities.get(i) ) );
        }
        return dtos;
    }
    
    
}
