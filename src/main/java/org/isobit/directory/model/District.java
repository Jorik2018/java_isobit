package org.isobit.directory.model;

import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "drt_distrito")
public class District implements Serializable {

    @Id
    @EqualsAndHashCode.Include()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_ubg")
    private Integer id;
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @Column(name = "id_pais")
    private int idPais;
    @Basic(optional = false)
    @Column(name = "id_dpto")
    private int idDpto;
    @Basic(optional = false)
    @Column(name = "id_prov")
    private int idProv;
    @Basic(optional = false)
    @Column(name = "id_distrito")
    private int idDist;
    @Basic(optional = false)
    @Column(name = "nombre_dist")
    private String name;
    @Column(name = "abreviatura_dist")
    private String abreviaturaDist;
    @Column(name = "codigo_dist")
    private String code;
    @JoinColumns({
        @JoinColumn(name = "id_pais", referencedColumnName = "id_pais", insertable = false, updatable = false)
        ,
        @JoinColumn(name = "id_prov", referencedColumnName = "id_prov", insertable = false, updatable = false)
        ,
        @JoinColumn(name = "id_dpto", referencedColumnName = "id_dpto", insertable = false, updatable = false)
    })
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private Province province;

}
