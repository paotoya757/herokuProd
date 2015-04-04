/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerSide.Converters;

import ServerSide.Models.DTOs.CatalizadorDTO;
import ServerSide.Models.DTOs.EpisodioDolorDTO;
import ServerSide.Models.DTOs.MedicamentoDTO;
import ServerSide.Models.DTOs.SintomaDTO;
import ServerSide.Models.Entities.EpisodioDolor;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class EpisodioDolorConverter {

    public static EpisodioDolor dtoToEntity(EpisodioDolorDTO dto) throws JSONException, IOException {
        EpisodioDolor entity = new EpisodioDolor();
        entity.setFecha(dto.getFecha());
        entity.setHoursSlept(dto.getHoursSlept());
        entity.setLocalizacion(dto.getLocalizacion());
        entity.setIntensidad(dto.getIntensidad());

        ObjectMapper mapper = new ObjectMapper();

        entity.setCatalizadores(mapper.writeValueAsString(entity.getCatalizadores()));
        entity.setMedicamentos(mapper.writeValueAsString(entity.getMedicamentos()));
        entity.setSintomas(mapper.writeValueAsString(entity.getSintomas()));

        return entity;
    }

    public static EpisodioDolorDTO entityToDto(EpisodioDolor entity) {
        EpisodioDolorDTO dto = new EpisodioDolorDTO();
        dto.setFecha(entity.getFecha());
        dto.setCedulaPaciente(entity.getPaciente().getCedula());
        dto.setHoursSlept(entity.getHoursSlept());
        dto.setId(entity.getId());
        dto.setIntensidad(entity.getIntensidad());
        dto.setLocalizacion(entity.getLocalizacion());

        return dto;
    }

    public static List<EpisodioDolorDTO> entityToDtoList(List<EpisodioDolor> entities) {
        List<EpisodioDolorDTO> dtos = new ArrayList<EpisodioDolorDTO>();
        for (int i = 0; i < entities.size(); i++) {
            dtos.add(entityToDto(entities.get(i)));
        }
        return dtos;
    }

    public static EpisodioDolorDTO entityDetailToDto(EpisodioDolor entity) {

        EpisodioDolorDTO dto = entityToDto(entity);
        try {

            ObjectMapper mapper = new ObjectMapper();

            List<CatalizadorDTO> cats;
            cats = mapper.readValue(entity.getCatalizadores(), new TypeReference<List<CatalizadorDTO>>() {
            });
            dto.setCatalizadores(cats);

            List<MedicamentoDTO> meds;
            meds = mapper.readValue(entity.getMedicamentos(), new TypeReference<List<MedicamentoDTO>>() {
            });
            dto.setMedicamentos(meds);

            List<SintomaDTO> sints;
            sints = mapper.readValue(entity.getSintomas(), new TypeReference<List<SintomaDTO>>() {
            });
            dto.setSintomas(sints);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }
}
