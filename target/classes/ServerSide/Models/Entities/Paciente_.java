package ServerSide.Models.Entities;

import ServerSide.Models.Entities.EpisodioDolor;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2015-04-03T18:32:45")
@StaticMetamodel(Paciente.class)
public class Paciente_ { 

    public static volatile SingularAttribute<Paciente, String> username;
    public static volatile ListAttribute<Paciente, EpisodioDolor> episodios;
    public static volatile SingularAttribute<Paciente, String> name;
    public static volatile SingularAttribute<Paciente, Date> birthdate;
    public static volatile SingularAttribute<Paciente, String> password;
    public static volatile SingularAttribute<Paciente, Long> cedula;

}