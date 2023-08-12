package org.isobit.directory.model;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false,onlyExplicitlyIncluded = true)
@Entity
@Table(name = "drt_directorio")
public class Directory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @EqualsAndHashCode.Include()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dir")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "psp_cxt")
    private short pspCxt;
    @Basic(optional = true)
    @Column(name = "psp_app")
    private int pspApp;
    @Basic(optional = true)
    @Column(name = "psp_uid")
    private Integer pspUid;
    @Basic(optional = true)
    @Column(name = "id_dclas")
    private short typeId;
    @Basic(optional = true)
    @Column(name = "dateinsert", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateinsert;

}
