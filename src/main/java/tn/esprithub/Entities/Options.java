package tn.esprithub.Entities;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.annotation.JsonIgnore;


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
	 
	 @JsonIgnore
	 @OneToMany(mappedBy = "option")
	 private Set<User> useroption;
	 
	 @Column(name = "discription")
		private String discription;


		
		
		public  Options( Long idOption ){
			this.idOption=idOption;
		}




		public Options() {
			super();
			// TODO Auto-generated constructor stub
		}
		
		
}
