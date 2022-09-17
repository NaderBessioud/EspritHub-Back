package tn.esprithub.Services;


import lombok.AllArgsConstructor;
import tn.esprithub.Entities.ConfirmationToken;
import tn.esprithub.Entities.User;
import tn.esprithub.Repository.ConfirmationTokenRepository;
import tn.esprithub.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service

public class UserServiceRegistration implements UserDetailsService {

	@Autowired
    private   UserRepository userRepository;
	
	@Autowired
    private  BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
    private  ConfirmationTokenService confirmationTokenService;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user =  userRepository.findByEmail(email);
        System.out.print(false);
        if(user ==null) {
            throw new UsernameNotFoundException("User not found ");

        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),user.getAuthorities());
    }

    // sign up
    public String signUpUser (User user ){
        boolean userExists =userRepository.findByEmail(user.getEmail()) != null;

         if  (userExists)
         {
            User userEnable=userRepository.findByEmail(user.getEmail()) ;
            if(userEnable.getEnabled() == true){

                throw new IllegalStateException( " email already taken") ;
            }else {
           /*    ConfirmationToken deleteUserToken= confirmationTokenRepository.findByUser(user).get();
                confirmationTokenRepository.delete(deleteUserToken);*/
                userRepository.delete(userEnable);
            }


         }
         String encodedPassword=   bCryptPasswordEncoder.encode( user.getPassword());
         user.setPassword(encodedPassword);
         User user2= userRepository.save(user);
         System.out.print(user2);
         String token = UUID.randomUUID().toString();
        // send confiramation token ;
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(60),
                user

        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return  token;
    }
    public int enableAppUser(String email) {
        return userRepository.enableAppUser(email);
    }
}
