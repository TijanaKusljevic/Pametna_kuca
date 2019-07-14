package entiteti;

import entiteti.Dogadjaj;
import java.sql.Date;
import java.sql.Time;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2019-05-28T18:35:03")
@StaticMetamodel(Zvono.class)
public class Zvono_ { 

    public static volatile SingularAttribute<Zvono, Date> datum;
    public static volatile SingularAttribute<Zvono, Integer> aktivan;
    public static volatile SingularAttribute<Zvono, Time> vreme;
    public static volatile SingularAttribute<Zvono, Dogadjaj> dogadjaj;
    public static volatile SingularAttribute<Zvono, Long> id;
    public static volatile SingularAttribute<Zvono, Integer> ponavlja_se;

}