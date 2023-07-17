package org.isobit.directory.jpa;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "drt_docidentidad")
public class DocumentType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_pdid")
    private Short id;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String name;
    @Basic(optional = false)
    @Column(name = "abreviatura_tpdid")
    private String acronym;
    @Column(name = "descripcion_tpdid")
    private String description;
    @Basic(optional = false)
    @Column(name = "vigencia_tpdid")
    private Character vigenciaTpdid;

    public DocumentType() {
    }

    public DocumentType(short id) {
        this.id=id;
    }
    
    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String abreviaturaTpdid) {
        this.acronym = abreviaturaTpdid;
    }

    public Character getVigenciaTpdid() {
        return vigenciaTpdid;
    }

    public void setVigenciaTpdid(Character vigenciaTpdid) {
        this.vigenciaTpdid = vigenciaTpdid;
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
        if (!(object instanceof DocumentType)) {
            return false;
        }
        DocumentType other = (DocumentType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return id + ": "+this.getDescription();
    }
    
}
