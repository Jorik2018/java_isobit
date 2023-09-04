package org.isobit.app.model;

import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false,onlyExplicitlyIncluded = true)
@Embeddable
public class UserRolePK implements Serializable {
    
    private static final long serialVersionUID = 2L;
    
    @Basic(optional = false)
    @NotNull
    private int uid;

    @Basic(optional = false)
    @NotNull
    private int rid;

}
