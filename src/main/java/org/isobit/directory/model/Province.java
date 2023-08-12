package org.isobit.directory.model;

import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false,onlyExplicitlyIncluded = true)
@Entity
@Table(name = "drt_provincia")
public class Province implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    @EqualsAndHashCode.Include()
    protected ProvincePK PK;
    @Basic(optional = false)
    @Column(name = "nombre_prov")
    private String name;
    @Column(name = "abreviatura_prov")
    private String abbreviation;
    @Column(name = "government_id")
    private Long governmentId;

    @Column(name = "codigo_prov")
    private String code;
    @JoinColumns({
        @JoinColumn(name = "id_pais", referencedColumnName = "id_pais", insertable = false, updatable = false)
        ,
        @JoinColumn(name = "id_dpto", referencedColumnName = "id_dpto", insertable = false, updatable = false)
    })
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private Region region;

}
