package tn.esprithub.Services;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tn.esprithub.Entities.Options;
import tn.esprithub.Entities.Ressource;
import tn.esprithub.Entities.TypeRessource;
import tn.esprithub.Entities.User;
import tn.esprithub.Repository.UserRepository;

@Service
public class UserService {
	@Autowired
	  private  BCryptPasswordEncoder bCryptPasswordEncoder;
	  @Autowired
	   private  OptionService optionService;
	    
	@Autowired
	private UserRepository repository;
	public User findByUserName(String username) {
		return repository.findByUsername(username);
	}
	
	
	public User addUser(User user) {
		PasswordEncoder encoder=new BCryptPasswordEncoder();
		user.setPassword(encoder.encode(user.getPassword()));
		
		return repository.save(user);
	}
	public boolean verrifymail(String email) {
		User user=repository.findByEmail(email);
		return user != null;
		
	}
	
	public boolean verrifyUsername(String username) {
		User user=repository.findByUsername(username);
		return user != null;
	}
	
	 
	    public User update(User user, Long id) {
	        if (repository.findById(id).isPresent()) {
	            User u = repository.findById(id).get();
	            u.setFirstName(user.getFirstName());
	            u.setLastName(user.getLastName());
	            u.setEmail(user.getEmail());
	            u.setGender(user.getGender());
	            u.setNiveau(user.getNiveau());
	            u.setAddress(user.getAddress());
	            Options option = optionService.findById(user.getOption().getIdOption());
	            u.setOption(option);
	            u.setAboutMe(user.getAboutMe());
	            return repository.save(u);

	        }
	        return null;


	    }

	    
	    public void delete(long id) {
	    	repository.deleteById(id);
	    }

	    
	    public List<User> findAll() {
	        return (List<User>) repository.findAll();
	    }

	    
	    public User findById(Long id) {
	        return repository.findById(id).get();
	    }

	    
	    public String getRole(Long id) {
	        User user = repository.findById(id).get();
	        String role = user.getRole().name();
	        return role;
	    }

	    
	    public User changeROle(User user, Long id) {
	        if (repository.findById(id).isPresent()) {
	            User u = repository.findById(id).get();
	            u.setRole(user.getRole());
	            repository.save(u);
	            return u;
	        }
	        return null;
	    }

	    
	    public User finduserbytoken(String email) {
	        return repository.findByEmail(email);
	    }

	    
	    public String changePassword(String oldpassword, Long id) {
	        if (repository.findById(id).isPresent()) {
	            User u = repository.findById(id).get();
	            if(bCryptPasswordEncoder.matches(oldpassword.toString(), u.getPassword())){
	                return "true";
	            }else {
	                return oldpassword;
	            }


	        }
	        return "false";    }

}
