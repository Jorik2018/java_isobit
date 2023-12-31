package org.isobit.directory.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import javax.json.bind.annotation.JsonbDateFormat;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false,onlyExplicitlyIncluded = true)
@Entity
@Table(name = "drt_personanatural")
public class People implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @EqualsAndHashCode.Include()
    @Basic(optional = false)
    @Column(name = "id_dir")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "ap_materno")
    private String firstSurname = "";
    @Basic(optional = false)
    @Column(name = "ap_paterno")
    private String lastSurname = "";
    @Basic(optional = false)
    @Column(name = "nombre")
    private String names = "";
    @Basic(optional = false)
    @Column(name = "sexo")
    private char sex = ' ';
    @Column(name = "fecha_nac")
    @JsonbDateFormat(value = "yyyy-MM-dd")
    //@Temporal(TemporalType.DATE)
    private LocalDate birthdate;
    @Basic(optional = false)
    @Column(name = "estado_pernat")
    private char status = '1';
    @Basic(optional = false)
    @Column(name = "fecha_ing")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIng;
    @Column(name = "direccion")
    private String address = "";
    @Column(name = "observacion")
    private String observacion = "";

    @Column(name = "numero_pndid")
    private String code = "";
    
    @Column(name = "id_ubg_nac")
    private Integer idUbgNac;
    @Column(name = "id_ubg_pro")
    private Integer idUbgPro;
    @Column(name = "id_pnec")
    private Integer idPnec;
    @Column(name = "id_grpsng")
    private Integer idGrpsng;
    @Column(name = "nombre_completo")
    private String fullName = "";
    @Column(name = "id_colegio")
    private Integer idColegio = 0;
    @Column(name = "anio_egreso_cole")
    private Integer anioEgresoCole = 0;
    @Column(name = "update_flow")
    private Integer updateFlow;
    @Column(name = "email_prin")
    private String mail = "";
    @Column(name = "telefono_prin")
    private String telefonoPrin = "";
    @Column(name = "celular_prin")
    private String phone = "";
    @Column(name = "update_self")
    private Integer updateSelf = 0;
    @Column(name = "otro_colegio")
    private String otroColegio = "";
    @Transient
    private Directory directory;
    @JoinColumn(name = "id_pdid", referencedColumnName = "id_pdid")
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private DocumentType documentType;
}
