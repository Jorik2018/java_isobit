package org.isobit.app.model;

import java.io.Serializable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Basic;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false,onlyExplicitlyIncluded = true)
@Entity
@Table(name = "dru_users_roles")
public class UserRole implements Serializable {
    
    private static final long serialVersionUID = 2L;
    
    @EmbeddedId
    @EqualsAndHashCode.Include()
    protected UserRolePK PK;

    @Basic(optional = false)
    private boolean active;

    @JoinColumn(name = "rid", referencedColumnName = "rid", insertable=false,updatable=false)
    @ManyToOne(optional = false)
    private Role role;
}
