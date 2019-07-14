/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entiteti;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author tijana
 */
@Entity
public class Dogadjaj implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
     private String opis;
    private String destinacija;
     private java.sql.Time vreme;
     private java.sql.Date datum;
     private int podseti;
     private java.sql.Time svreme;
     private java.sql.Date sdatum;

    public Time getSvreme() {
        return svreme;
    }

    public void setSvreme(Time svreme) {
        this.svreme = svreme;
    }

    public Date getSdatum() {
        return sdatum;
    }

    public void setSdatum(Date sdatum) {
        this.sdatum = sdatum;
    }
     
     

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public int getPodseti() {
        return podseti;
    }

    public void setPodseti(int podseti) {
        this.podseti = podseti;
    }
     
     

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getDestinacija() {
        return destinacija;
    }

    public void setDestinacija(String destinacija) {
        this.destinacija = destinacija;
    }

    public Time getVreme() {
        return vreme;
    }

    public void setVreme(Time vreme) {
        this.vreme = vreme;
    }
     

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof Dogadjaj)) {
            return false;
        }
        Dogadjaj other = (Dogadjaj) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entiteti.Dogadjaj[ id=" + id + " ]";
    }
    
}
