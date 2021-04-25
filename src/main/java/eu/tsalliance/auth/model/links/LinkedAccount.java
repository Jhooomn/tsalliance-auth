package eu.tsalliance.auth.model.links;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class LinkedAccount {

    @Id
    private String id = UUID.randomUUID().toString();

    @ManyToOne
    private LinkableApp appInfo;

}
