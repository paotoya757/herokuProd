package ServerSide.Models.Entities;

import ServerSide.Models.Entities.Paciente;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2015-04-03T18:32:45")
@StaticMetamodel(Doctor.class)
public class Doctor_ { 

    public static volatile SingularAttribute<Doctor, Long> id;
    public static volatile SingularAttribute<Doctor, String> username;
    public static volatile SingularAttribute<Doctor, String> name;
    public static volatile ListAttribute<Doctor, Paciente> pacientes;
    public static volatile SingularAttribute<Doctor, String> password;

}