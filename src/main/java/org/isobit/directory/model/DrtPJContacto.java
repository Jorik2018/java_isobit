package org.isobit.directory.model;

import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false,onlyExplicitlyIncluded = true)
@Entity
@Table(name = "drt_perjurcontacto")
public class DrtPJContacto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
@EqualsAndHashCode.Include()
    @Basic(optional = false)
    @Column(name = "id_prcl")
    private Integer idPrcl;
    @JoinColumn(name = "id_dir", referencedColumnName = "id_dir")
    @ManyToOne(optional = false)
    private People people;
    @JoinColumn(name = "id_perjur", referencedColumnName = "id_dir")
    @ManyToOne(optional = false)
    private Company company;
    @Column(name = "tratamiento")
    private String tratamiento;
    @Column(name = "orden")
    private Integer orden;
    @Column(name = "nombre")
    private String nombre;

  

    
}
