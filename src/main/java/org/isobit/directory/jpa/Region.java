package org.isobit.directory.jpa;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false,onlyExplicitlyIncluded = true)
@Entity
@Table(name = "drt_departamento")
public class Region implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    @EqualsAndHashCode.Include()
    protected RegionPK PK;
    @Basic(optional = false)
    @Column(name = "nombre_dpto")
    private String name;
    @Column(name = "abreviatura_dpto")
    private String abbreviation;
    @Column(name = "codigo_dpto")
    private String code;

}
