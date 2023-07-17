package org.isobit.directory.jpa;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class RegionPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "id_pais")
    private int idPais;
    @Basic(optional = false)
    @Column(name = "id_dpto")
    private int idDpto;

    public RegionPK() {
    }

    public RegionPK(int idPais, int idDpto) {
        this.idPais = idPais;
        this.idDpto = idDpto;
    }

    public int getIdPais() {
        return idPais;
    }

    public void setIdPais(int idPais) {
        this.idPais = idPais;
    }

    public int getIdDpto() {
        return idDpto;
    }

    public void setIdDpto(int idDpto) {
        this.idDpto = idDpto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idPais;
        hash += (int) idDpto;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RegionPK)) {
            return false;
        }
        RegionPK other = (RegionPK) object;
        if (this.idPais != other.idPais) {
            return false;
        }
        if (this.idDpto != other.idDpto) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "m.DrtDepartamentoPK[ idPais=" + idPais + ", idDpto=" + idDpto + " ]";
    }
    
}
