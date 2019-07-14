/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entiteti;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 *
 * @author tijana
 */
@Entity
public class Zvono implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private java.sql.Time vreme;
    
    private java.sql.Date datum;
     
    private int aktivan;
    private int ponavlja_se;
    
    @OneToOne (cascade = CascadeType.REMOVE)
    @JoinColumn(name="dog", referencedColumnName = "id")
    private Dogadjaj dogadjaj;

    public Dogadjaj getDogadjaj() {
        return dogadjaj;
    }

    public void setDogadjaj(Dogadjaj dogadjaj) {
        this.dogadjaj = dogadjaj;
    }
    
    

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }
    
    

    public Time getVreme() {
        return vreme;
    }

    public void setVreme(Time vreme) {
        this.vreme = vreme;
    }

    public int getAktivan() {
        return aktivan;
    }

    public void setAktivan(int aktivan) {
        this.aktivan = aktivan;
    }

    public int getPonavlja_se() {
        return ponavlja_se;
    }

    public void setPonavlja_se(int ponavlja_se) {
        this.ponavlja_se = ponavlja_se;
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
        if (!(object instanceof Zvono)) {
            return false;
        }
        Zvono other = (Zvono) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entiteti.Zvono[ id=" + id + " ]";
    }
    
}
