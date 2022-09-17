package tn.esprithub.Entities;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.Data;

@Entity
@Data
public class Options {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOption")
    private Long idOption;
	 @Column(name = "libelle")
	 private String libelle;
	 
	 @OneToMany(mappedBy = "option")
	 private Set<User> useroption;
	 
}
