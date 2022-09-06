package tn.esprithub.Repository;

import org.springframework.data.repository.CrudRepository;

import tn.esprithub.Entities.User;



public interface UserRepository extends CrudRepository<User, Long> {
	User findByUsername(String username);
	User findByEmail(String email);

}
