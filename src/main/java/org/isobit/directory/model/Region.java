package org.isobit.directory.model;

import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
