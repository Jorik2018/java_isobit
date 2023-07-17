package org.isobit.directory.jpa;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
