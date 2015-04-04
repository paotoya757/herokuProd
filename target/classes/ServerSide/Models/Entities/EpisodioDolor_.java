package ServerSide.Models.Entities;

import ServerSide.Models.Entities.Paciente;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2015-04-03T18:32:45")
@StaticMetamodel(EpisodioDolor.class)
public class EpisodioDolor_ { 

    public static volatile SingularAttribute<EpisodioDolor, Long> id;
    public static volatile SingularAttribute<EpisodioDolor, Paciente> paciente;
    public static volatile SingularAttribute<EpisodioDolor, String> sintomas;
    public static volatile SingularAttribute<EpisodioDolor, Date> fecha;
    public static volatile SingularAttribute<EpisodioDolor, String> localizacion;
    public static volatile SingularAttribute<EpisodioDolor, Integer> intensidad;
    public static volatile SingularAttribute<EpisodioDolor, Integer> hoursSlept;
    public static volatile SingularAttribute<EpisodioDolor, String> medicamentos;
    public static volatile SingularAttribute<EpisodioDolor, String> catalizadores;

}