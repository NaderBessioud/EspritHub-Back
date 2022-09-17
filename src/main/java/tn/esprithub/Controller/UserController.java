package tn.esprithub.Controller;


import lombok.AllArgsConstructor;
import tn.esprithub.Entities.User;
import tn.esprithub.Services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/user/")
@AllArgsConstructor
public class UserController {
	@Autowired
    private  UserService userService;
	
    @PutMapping("update/{id}")
    public User update(@RequestBody User user, @PathVariable("id") Long id) {
        User updatedUser = userService.update(user, id);
        return updatedUser;
    }
    @PutMapping("managment/changeRole/{id}")
    public User ChangeRole(@RequestBody User user, @PathVariable("id") Long id) {
        User changeRole = userService.changeROle(user, id);
        return changeRole;
    }


    @DeleteMapping("managment/delete/{id}")
    public void delete(@PathVariable("id") long id) throws Exception {
        userService.delete(id);
    }
    @GetMapping("managment/findById/{id}")
    public User findById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }
    @GetMapping("list")
    public List<User> findAll() {
        return userService.findAll();
    }
    @GetMapping("managment/getrole/{id}")
    public String getrole(@PathVariable("id") Long id) {
        return userService.getRole(id);
    }

    @GetMapping("managment/getbyemail/{id}")
    public String getuserbyEmail(@PathVariable("id") Long id) {
        return userService.getRole(id);
    }


    @GetMapping("findByToken")
    public User findUserByToken() {
        return userService.finduserbytoken(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PostMapping("insertImage")
    public String updateImage(@RequestParam("File") String file,@RequestBody User user) throws IOException {

        
        
    	user.setImage(file);
        userService.addUser(user);
        
        return "The File Uploaded Successfully";
    }
    @PutMapping ( "changePassowrd/{id}")
    public String changePassword(@RequestBody String password,@PathVariable("id") Long id){
        String u = password;
      return u;
    }
}
