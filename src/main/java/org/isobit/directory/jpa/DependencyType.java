package org.isobit.directory.jpa;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
//import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "org_dep_tipo")
public class DependencyType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_dep_tipo")
    private Integer id;
    @Basic(optional = false) 
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "nombre_dep_tipo")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "abreviatura_dep_tipo")
    private String abreviatura;
    @Size(max = 200)
    @Column(name = "desc_dep_tipo")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "estado_dep_tipo")
    private String status;
    @Column(name = "position_id")
    private Integer positionId;
    
    public DependencyType() {
    }

    public DependencyType(Integer idDepTipo) {
        this.id = idDepTipo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String nombreDepTipo) {
        this.name = nombreDepTipo;
    }

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviaturaDepTipo) {
        this.abreviatura = abreviaturaDepTipo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        if (!(object instanceof DependencyType)) {
            return false;
        }
        DependencyType other = (DependencyType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.uns.ocid.jpa.OrgDepTipo[ idDepTipo=" + id + " ]";
    }

}
