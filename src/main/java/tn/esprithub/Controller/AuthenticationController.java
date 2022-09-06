package tn.esprithub.Controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


import tn.esprithub.Entities.User;
import tn.esprithub.Security.JWTTokenProvider;
import tn.esprithub.Security.UserPrincipale;
import tn.esprithub.Services.QuestionServiceImp;
import tn.esprithub.Services.UserService;
import org.springframework.security.authentication.AuthenticationManager;




@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/auth")
public class AuthenticationController {
	
	private static final Logger logger=LogManager.getLogger(AuthenticationController.class);
	
	
	private final RestTemplate restTemplate= new RestTemplate();
	
	
	
	@Autowired
	private UserService  service;
	@Autowired
	JWTTokenProvider jwtTokenProvider;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private QuestionServiceImp imp;
	
	
	
	 
	 
	
	 
	 
	
	
	
	//@Autowired
	//MySimpleUrlAuthenticationSuccessHandler authenticationSuccessHandler=new MySimpleUrlAuthenticationSuccessHandler(authenticationManager);

	 
	 
	@GetMapping("/getUser")
	@ResponseBody
	public User getUser(@RequestParam("username") String username) {
		return service.findByUserName(username);
	}
	
	@GetMapping("/login")
	@ResponseBody
	public User Login( @RequestParam("username") String username,@RequestParam("pass") String pass) throws Exception {
		
	
		int result=0;
		
		
		result =authenticate(username,pass);
		System.out.print(result);
		if(result == 0) {
		
		User user=service.findByUserName(username);
		String token="";
		
		
		
		UserPrincipale principal;
		
		
			//authenticationSuccessHandler.loginAgentNotification(ag, request);
			 principal=new UserPrincipale(user);
			  token=jwtTokenProvider.generateToken(principal);
			  user.setToken(token);
			  
			  
		
		
		
		
		return user;
		}
		else 
			return null;
		
	}
	
	
	
	
	private int authenticate(String username, String pass) throws Exception{
		
			System.out.print(pass);
			System.out.print(SecurityContextHolder.getContext()==null);
			
		SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, pass)));
			return 0;
			


			
		
	}
	
	
	
	
	@PostMapping("/register")
	@ResponseBody
	public User register(@RequestBody User user) {
		
			
			return service.addUser(user);
		
	}
	
	 @GetMapping(value = "/download")
	 @ResponseBody
	 public String downloadfile(@RequestParam("image") String name) throws IOException{
		return  imp.downloadImage(name);
	 }
	
		
		
		
		
        
        
       
       

    
	 
	 @PostMapping("/upload")
	 @ResponseBody
	 public String uploadfile(@RequestParam(value = "imageFile", required = true) MultipartFile  uploadFile) {
		   FTPClient ftpClient = new FTPClient();
		   String name="";
		     try {
		    	
		    	 
		         ftpClient.connect("192.168.1.19", 21);
		         ftpClient.login("ftpuser", "ftpuser");
		         ftpClient.enterLocalPassiveMode();

		         ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

		         // APPROACH #1: uploads first file using an InputStream
		         File firstLocalFile = new File("D:/Test/Projects.zip");

		     
		         InputStream inputStream = uploadFile.getInputStream();
		         String extension = FilenameUtils.getExtension(uploadFile.getOriginalFilename());
		         System.out.println("Start uploading first file");
		         
		         boolean done = ftpClient.storeFile(uploadFile.getOriginalFilename(), inputStream);
		         inputStream.close();
		         if (done) {
		             System.out.println("The first file is uploaded successfully.");
		         }

		         /*
		         // APPROACH #2: uploads second file using an OutputStream
		         File secondLocalFile = new File("E:/Test/Report.doc");
		         String secondRemoteFile = "test/Report.doc";
		         inputStream = new FileInputStream(secondLocalFile);

		         System.out.println("Start uploading second file");
		         OutputStream outputStream = ftpClient.storeFileStream(secondRemoteFile);
		         byte[] bytesIn = new byte[4096];
		         int read = 0;

		         while ((read = inputStream.read(bytesIn)) != -1) {
		             outputStream.write(bytesIn, 0, read);
		         }
		         inputStream.close();
		         outputStream.close();

		         boolean completed = ftpClient.completePendingCommand();
		         if (completed) {
		             System.out.println("The second file is uploaded successfully.");
		         }*/
		         

		     } catch (IOException ex) {
		         System.out.println("Error: " + ex.getMessage());
		         ex.printStackTrace();
		     } finally {
		         try {
		             if (ftpClient.isConnected()) {
		                 ftpClient.logout();
		                 ftpClient.disconnect();
		             }
		         } catch (IOException ex) {
		             ex.printStackTrace();
		         }
		     }
		     return uploadFile.getOriginalFilename();

		}
	 
	 
	 @GetMapping("/VerrifyEmail")
	 @ResponseBody
	 public boolean verrifyEmail(@RequestParam("email") String email) {
		 return service.verrifymail(email);
	 }
	 
	 @GetMapping("/VerrifyUsername")
	 @ResponseBody
	 public boolean verrifyUsername(@RequestParam("username") String username) {
		 return service.verrifyUsername(username);
	 }
	 
	 
	 
	 @GetMapping("/getAddress")
		@ResponseBody
		public String getAddress(@RequestParam("lat") String lat,@RequestParam("lng") String lng) {
			 String url="https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat="+lat
			    		+"&lon="+lng;
			    JsonParser springParser = JsonParserFactory.getJsonParser();
			    Map< String, Object > map = springParser.parseMap(this.restTemplate.getForObject(url, String.class));
			    
			   System.out.println(map.get("display_name"));
	    	
	        return map.get("display_name").toString();
		}
	 
	
	
	
	
	
	
	
	
	
	
		
}

