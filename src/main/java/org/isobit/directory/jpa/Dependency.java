package org.isobit.directory.jpa;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.isobit.util.XUtil;

@Entity
@Table(name = "org_dependencia")
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

    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }

    public Integer getBossId() {
        return bossId;
    }

    public void setBossId(Integer bossId) {
        this.bossId = bossId;
    }

    public boolean getCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public Integer getmNemonico() {
        return mNemonico;
    }

    public void setmNemonico(Integer mNemonico) {
        this.mNemonico = mNemonico;
    }

    public Dependency() {
    }

    public void setFullName(String s) {
    }

    public String getFullName() {
        DependencyType s = this.getType();
        return (s != null ? s.getName() + " " : "") + this.getName();
    }

    public Dependency(Integer idDep) {
        this.id = idDep;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer idDep) {
        this.id = idDep;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public Integer getIdOrg() {
        return idOrg;
    }

    public void setIdOrg(Integer idOrg) {
        this.idOrg = idOrg;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Dependency getParent() {
        return parent;
    }

    public void setParent(Dependency parent) {
        this.parent = parent;
    }

    public Integer getIdDtra() {
        return idDtra;
    }

    public void setIdDtra(Integer idDtra) {
        this.idDtra = idDtra;
    }

    public String getName() {
        return name;
    }

    public void setName(String nombreDep) {
        this.name = nombreDep;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String descDep) {
        this.description = descDep;
    }

    public Date getFechaReg() {
        return fechaReg;
    }

    public void setFechaReg(Date fechaReg) {
        this.fechaReg = fechaReg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public String getJerarqDep() {
        return jerarqDep;
    }

    public void setJerarqDep(String jerarqDep) {
        this.jerarqDep = jerarqDep;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono2() {
        return telefono2;
    }

    public void setTelefono2(String telefono2) {
        this.telefono2 = telefono2;
    }

    public String getAnexo() {
        return anexo;
    }

    public void setAnexo(String anexo) {
        this.anexo = anexo;
    }

    public Integer getIdUbi() {
        return idUbi;
    }

    public void setIdUbi(Integer idUbi) {
        this.idUbi = idUbi;
    }

    public String getSubdep() {
        return subdep;
    }

    public void setSubdep(String subdep) {
        this.subdep = subdep;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public DependencyType getType() {
        return type;
    }

    public void setType(DependencyType orgDepTipo) {
        this.type = orgDepTipo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Dependency)) {
            return false;
        }
        Dependency other = (Dependency) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ((type != null ? type.getName() + " " : "") + this.name + (XUtil.isEmpty(acronym) ? "" : (" - " + acronym)));
//        return (idDep+": "+(type!=null?type.getName()+" ":"")+this.name);
    }

}
