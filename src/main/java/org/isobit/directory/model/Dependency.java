package org.isobit.directory.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.isobit.util.XUtil;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "org_dependencia")
@Data
public class Dependency implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_dep")
    private Integer id;
    @Column(name = "id_org")
    private Integer idOrg;
    @JoinColumn(name = "id_dep_tipo", referencedColumnName = "id_dep_tipo")
    @ManyToOne(optional = false)
    private DependencyType type;
    @Column(name = "org_id_dep")
    private Integer parentId;
    @Transient
    private Dependency parent;
    @Column(name = "id_dtra")
    private Integer idDtra;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "nombre_dep")
    private String name;
    @Size(max = 200)
    @Column(name = "desc_dep")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_reg")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaReg;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "dep_estado")
    private String status;
    @Size(max = 20)
    @Column(name = "abrevia_dep")
    private String acronym;
    @Size(max = 2)
    @Column(name = "tmp")
    private String tmp;
    @Size(max = 17)
    @Column(name = "jerarq_dep")
    private String jerarqDep;
    @Column(name = "codigo_dep")
    private Integer code;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 50)
    @Column(name = "email")
    private String email;
    @Size(max = 8)
    @Column(name = "telefono1")
    private String phone;
    @Size(max = 8)
    @Column(name = "telefono2")
    private String telefono2;
    @Size(max = 8)
    @Column(name = "anexo")
    private String anexo;
    @Column(name = "id_ubi")
    private Integer idUbi;
    @Size(max = 100)
    @Column(name = "sitioweb")
    private String website;
    @Size(max = 3)
    @Column(name = "subdep")
    private String subdep;
    @Column(name = "boss_id")
    private Integer bossId;
    @Column(name = "position_id")
    private Integer positionId;
    private boolean canceled = Boolean.FALSE;
    @Column(name = "id_institucion")
    private Integer companyId;
    @Column(name = "m_nemonico")
    private Integer mNemonico;
    @Transient
    private Object ext;
    public String getFullName() {
        DependencyType s = this.getType();
        return (s != null ? s.getName() + " " : "") + this.getName();
    }
}
