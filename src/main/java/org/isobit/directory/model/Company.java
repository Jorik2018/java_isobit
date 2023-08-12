package org.isobit.directory.model;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false,onlyExplicitlyIncluded = true)
@Entity
@Table(name = "drt_personajuridica")
public class Company implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @EqualsAndHashCode.Include()
    @Basic(optional = false)
    @Column(name = "id_dir")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "id_ubg")
    private int idUbg;
    @Basic(optional = false)
    @Column(name = "razon_social")
    private String businessName;
    @Basic(optional = false)
    @Column(name = "ruc")
    private String ruc;
    @Column(name = "direccion")
    private String address;
    @Basic(optional = false)
    @Column(name = "provclie")
    private char provclie = '0';
    @Basic(optional = false)
    @Column(name = "estado_perjur")
    private char estadoPerjur = 'A';
    @Column(name = "fecha_ing")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIng;
    @Column(name = "update_flow")
    private Integer updateFlow;
    @Transient
    private Directory directory;

}
