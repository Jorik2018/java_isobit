package org.isobit.directory.jpa;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
