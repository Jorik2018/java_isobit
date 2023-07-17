package org.isobit.directory.jpa;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ProvincePK implements Serializable {

    @Basic(optional = false)
    @Column(name = "id_pais")
    private int idPais;
    @Basic(optional = false)
    @Column(name = "id_dpto")
    private int idDpto;
    @Basic(optional = false)
    @Column(name = "id_prov")
    private int idProv;

    public ProvincePK() {
    }

    public ProvincePK(int idPais, int idDpto, int idProv) {
        this.idPais = idPais;
        this.idDpto = idDpto;
        this.idProv = idProv;
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

    public int getIdProv() {
        return idProv;
    }

    public void setIdProv(int idProv) {
        this.idProv = idProv;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idPais;
        hash += (int) idDpto;
        hash += (int) idProv;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProvincePK)) {
            return false;
        }
        ProvincePK other = (ProvincePK) object;
        if (this.idPais != other.idPais) {
            return false;
        }
        if (this.idDpto != other.idDpto) {
            return false;
        }
        if (this.idProv != other.idProv) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "m.DrtProvinciaPK[ idPais=" + idPais + ", idDpto=" + idDpto + ", idProv=" + idProv + " ]";
    }
    
}
