/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ServerSide.Converters;

import ServerSide.Models.DTOs.DoctorDTO;
import ServerSide.Models.Entities.Doctor;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Personal
 */
public class DoctorConverter {
    
    public static Doctor dtoToEntity(DoctorDTO dto){
        
       Doctor entity = new Doctor();
       entity.setName( dto.getName() );
       //entity.setPassword( dto.getPassword() );
       entity.setUsername( dto.getUsername() );
       
       return entity ;
    }
    
    public static DoctorDTO entityToDto(Doctor entity ){
        DoctorDTO dto = new DoctorDTO();
        dto.setId( entity.getId() );
        dto.setName( entity.getName() );
        dto.setPassword( entity.getPassword() );
        dto.setUsername( entity.getUsername() );
        return dto ;
    }
    
    public static List<DoctorDTO> entityToDtoList( List<Doctor> entities ){
        List<DoctorDTO> dtos = new ArrayList<DoctorDTO>();
        for( int i = 0 ; i < entities.size() ; i++){
            dtos.add( entityToDto( entities.get(i) ) );
        }
        return dtos;
    }
    
}
