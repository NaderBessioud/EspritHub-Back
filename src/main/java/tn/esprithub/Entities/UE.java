package tn.esprithub.Entities;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;


import lombok.Data;

@Entity
public class UE {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUE")
    private Long idUE;
    @Column(name = "libelle")
    private String libelle;
    
    @OneToMany(mappedBy = "ue")
    private List<Question> uequestions;
    
    @JsonIgnore
    @OneToMany(mappedBy = "uecours")
    private List<Cours> cours;
    
	public Long getIdUE() {
		return idUE;
	}

	public void setIdUE(Long idUE) {
		this.idUE = idUE;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public List<Question> getUequestions() {
		return uequestions;
	}

	public void setUequestions(List<Question> uequestions) {
		this.uequestions = uequestions;
	}

	public List<Cours> getCours() {
		return cours;
	}

	public void setCours(List<Cours> cours) {
		this.cours = cours;
	}


}
