package tn.esprithub.Services;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.persistence.Convert;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ch.qos.logback.core.pattern.Converter;
import tn.esprithub.Entities.Question;
import tn.esprithub.Entities.Reaction;
import tn.esprithub.Entities.Response;
import tn.esprithub.Entities.Ressource;
import tn.esprithub.Entities.Role;
import tn.esprithub.Entities.Tag;
import tn.esprithub.Entities.TypeReaction;
import tn.esprithub.Entities.TypeRessource;
import tn.esprithub.Entities.UE;
import tn.esprithub.Entities.User;
import tn.esprithub.Entities.UserQuestion;
import tn.esprithub.Repository.QuestionRepository;
import tn.esprithub.Repository.ReactionRepository;
import tn.esprithub.Repository.RessourceRepository;
import tn.esprithub.Repository.TagRepository;
import tn.esprithub.Repository.UeRepository;
import tn.esprithub.Repository.UserRepository;




@Service
public class QuestionServiceImp implements QuestionService {
	
	@Autowired
	private QuestionRepository questionRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TagRepository repository;
	@Autowired
	private ReactionRepository reactionRepository;
	
	@Autowired
	private ResponseServiceImp responseServiceImp;
	
	@Autowired
	private RessourceRepository repository2;
	
	@Autowired
	private UeRepository ueRepository;
	
	@Transactional
	public Question addQuestionWithoutRessource(Question q,long idu,long idm) {
		User user=userRepository.findById(idu).get();
		UE ue=ueRepository.findById(idm).get();
		

		
		
	q.setClosed(false);
	q.setDatepub(new Date());
	q.setUserquestions(user);
	q.setUe(ue);
	
	Question question= questionRepository.save(q);
	user.getQuestions().add(q);
	userRepository.save(user);
	ue.getUequestions().add(question);
	ueRepository.save(ue);
	return question;

	}

	@Transactional
	public Question addQuestion(Question q,Long idr,long idu,long idm) {
			
			User user=userRepository.findById(idu).get();
			Ressource ressource=repository2.findById(idr).get();
			UE ue=ueRepository.findById(idm).get();

			
			
		q.setClosed(false);
		q.setDatepub(new Date());
		q.setUserquestions(user);
		q.setUe(ue);
		
		Question question= questionRepository.save(q);
		user.getQuestions().add(q);
		userRepository.save(user);
		ressource.setRessources(q);
		repository2.save(ressource);
		ue.getUequestions().add(question);
		ueRepository.save(ue);
		return question;
	}

	@Override
	public List<Question> retrieveQuestions() {
		return (List<Question>) questionRepository.findAll();
	}

	@Override
	public void deleteQuestion(Long id) {
		questionRepository.deleteById(id);
	}

	@Override
	public Question updateProduit(Question u) {
		return questionRepository.save(u);
	}

	@Override
	public Question retrieveQuestion(Long id) {
		return questionRepository.findById(id).orElse(null);
	}
	
	public List<UserQuestion> getAllUserQuestions() throws IOException, SerialException, SQLException{
		List<Question> questions =this.retrieveQuestions();
		List<UserQuestion> result=new ArrayList<>();
	
		
		for (Question q : questions) {
			User user=userRepository.findById(q.getUserquestions().getId()).get();
			String nom=user.getFirstName()+" "+user.getLastName();
			List<String> tags=new ArrayList<>();
			for (Tag tag : q.getTags()) {
				tags.add(tag.getTitle());	
			}
			
			Ressource ressource=q.getQuestionressources().stream().findFirst().orElse(null);
			if(ressource != null) {
			if(ressource.getType() ==TypeRessource.Image){
				System.out.print(ressource.getLibelle());
				result.add(new UserQuestion(q.getIdQuestion(), nom, q.getContent(), q.getDatepub(), q.getTitle(), q.getNbresp(),tags,q.getUserquestions().getRole().toString(),responseServiceImp.getQuestionAnswersNotApproved(q.getIdQuestion()).size(),responseServiceImp.AffectBadge(q.getUserquestions().getId()),this.downloadImage(q.getUserquestions().getImage()),this.downloadImage(ressource.getLibelle())));
		
			}
			else
			
			result.add(new UserQuestion(q.getIdQuestion(), nom, q.getContent(), q.getDatepub(), q.getTitle(), q.getNbresp(),tags,q.getUserquestions().getRole().toString(),responseServiceImp.getQuestionAnswersNotApproved(q.getIdQuestion()).size(),responseServiceImp.AffectBadge(q.getUserquestions().getId()),this.downloadImage(q.getUserquestions().getImage()),this.downloadFile(ressource.getLibelle())));
			
			}
			else {
				result.add(new UserQuestion(q.getIdQuestion(), nom, q.getContent(), q.getDatepub(), q.getTitle(), q.getNbresp(),tags,q.getUserquestions().getRole().toString(),responseServiceImp.getQuestionAnswersNotApproved(q.getIdQuestion()).size(),responseServiceImp.AffectBadge(q.getUserquestions().getId()),this.downloadImage(q.getUserquestions().getImage()),""));

			}
			
			
			
		}
		
		return result;
	}
	
	public UserQuestion getQuestion(Long id) throws IOException, SerialException, SQLException {
		Question question=questionRepository.findById(id).get();
		String nom=question.getUserquestions().getFirstName()+" "+question.getUserquestions().getLastName();
		List<String> tags=new ArrayList<>();
		for (Tag tag : question.getTags()) {
			tags.add(tag.getTitle());
			
		}
		Ressource ressource=question.getQuestionressources().stream().findFirst().orElse(null);
		if(ressource != null) {
		if(ressource.getType() ==TypeRessource.Image){
			return new UserQuestion(question.getIdQuestion(), nom, question.getContent(), question.getDatepub(), question.getTitle(), 0, tags, question.getUserquestions().getRole().toString(),responseServiceImp.AffectBadge(question.getUserquestions().getId()),question.isClosed(),this.downloadImage(question.getUserquestions().getImage()),this.downloadImage(ressource.getLibelle()));
	
		}
		else
		
		return new UserQuestion(question.getIdQuestion(), nom, question.getContent(), question.getDatepub(), question.getTitle(), 0, tags, question.getUserquestions().getRole().toString(),responseServiceImp.AffectBadge(question.getUserquestions().getId()),question.isClosed(),this.downloadImage(question.getUserquestions().getImage()),this.downloadFile(ressource.getLibelle()));
		
		}
		else {
			return new UserQuestion(question.getIdQuestion(), nom, question.getContent(), question.getDatepub(), question.getTitle(), 0, tags, question.getUserquestions().getRole().toString(),responseServiceImp.AffectBadge(question.getUserquestions().getId()),question.isClosed(),this.downloadImage(question.getUserquestions().getImage()),"");

		}
		}
	
	@Transactional
	public void addQuestionAndAffectTag(Question question,Long id) {
		
		
		Tag tag=repository.findById(id).get();
		
		question.getTags().add(tag);
		questionRepository.save(question);
		tag.getQuestiontag().add(question);
		repository.save(tag);
		
	}
	
	public void updateAnswersNumber(Long id) {
		Question question=questionRepository.findById(id).get();
		question.setNbresp(question.getNbresp()+1);
		questionRepository.save(question);
	}
	
	@Transactional
	public void LikeQuestion(Long id,Long idu) {
		Reaction reaction=new Reaction();
		reaction.setDate(new Date());
		reaction.setIdUser(idu);
		reaction.setTypereaction(TypeReaction.Like);
		Question question=questionRepository.findById(id).get();
		question.setLikes(question.getLikes()+1);
		reaction.setQuestionReaction(question);
		reactionRepository.save(reaction);
		question.getReactions().add(reaction);
		questionRepository.save(question);
		
		
	}
	
	@Transactional
	public void DislikeQuestion(Long id,Long idu) {
		Reaction reaction=new Reaction();
		reaction.setDate(new Date());
		reaction.setIdUser(idu);
		reaction.setTypereaction(TypeReaction.Dislike);
		Question question=questionRepository.findById(id).get();
		question.setDislike(question.getDislike()+1);
		
		reaction.setQuestionReaction(question);
		reactionRepository.save(reaction);
		question.getReactions().add(reaction);
		questionRepository.save(question);
	}
	
	public void closeQuestion(Long id) {
		Question question=questionRepository.findById(id).get();
		question.setClosed(true);
	}
	
	
	
	 public String uploadfile(MultipartFile uploadFile) {
		   FTPClient ftpClient = new FTPClient();
		     try {
		    	
		    	 
		         ftpClient.connect("192.168.1.19", 21);
		         ftpClient.login("ftpuser", "ftpuser");
		         ftpClient.enterLocalPassiveMode();

		         ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

		         // APPROACH #1: uploads first file using an InputStream
		         File firstLocalFile = new File("D:/Test/Projects.zip");

		         String firstRemoteFile = "Projects.zip";
		         InputStream inputStream = uploadFile.getInputStream();
		         String extension = FilenameUtils.getExtension(uploadFile.getOriginalFilename());
		         System.out.println("Start uploading first file");
		         System.out.println(extension);
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
		         return uploadFile.getOriginalFilename();

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
		     return null;

		}
	 
	 public String downloadFile(String name) throws SerialException, SQLException {
		 FTPClient ftpClient = new FTPClient();
		 Blob result=null;
		 String src="";
	        try {
	        		
	        	   ftpClient.connect("192.168.1.19", 21);
			         ftpClient.login("ftpuser", "ftpuser");
	            ftpClient.enterLocalPassiveMode();
	            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	 
	       /*     // APPROACH #1: using retrieveFile(String, OutputStream)
	            String remoteFile1 = "/test/video.mp4";
	            File downloadFile1 = new File("D:/Downloads/video.mp4");
	            InputStream inputStream = ftpClient.retrieveFileStream(remoteFile1);
	            byte[] content=inputStream.readAllBytes();
	            OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
	            boolean success = ftpClient.retrieveFile(remoteFile1, outputStream1);
	            outputStream1.close();
	 
	            if (success) {
	                System.out.println("File #1 has been downloaded successfully.");
	            }
	 */
	            // APPROACH #2: using InputStream retrieveFileStream(String)
	            File file=new File(name);
	            String remoteFile2 = name;
	           // File downloadFile2 = new File("D:/Downloads/song.mp3");
	            //OutputStream outputStream2 = new BufferedOutputStream(new FileOutputStream(downloadFile2));
	            InputStream inputStream = ftpClient.retrieveFileStream(remoteFile2);
	            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	            byte[] bytesArray = IOUtils.toByteArray(inputStream);
	            src="data:application/pdf;base64,"+Base64.getEncoder().encodeToString(bytesArray);
	            
	            FileUtils.writeByteArrayToFile(file,bytesArray );
	            /*int bytesRead = -1;
	            int nRead;
	            while (( bytesRead=inputStream.read(bytesArray,0,bytesArray.length)) != -1) {
	                //outputStream2.write(bytesArray, 0, bytesRead);
	            	buffer.write(bytesArray,0,bytesRead);
	            }*/
	 
	            boolean success = ftpClient.completePendingCommand();
	            if (success) {
	                System.out.println("File #2 has been downloaded successfully.");
	            }
	           // outputStream2.close();
	            inputStream.close();
	            System.out.print(FilenameUtils.getExtension(name));
	            
	            result=new SerialBlob(bytesArray);
	            
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
	        //System.out.println(src);
	        return src;
	 }
	 
	 public long addRessource(Ressource ressource) {
		
		return repository2.save(ressource).getIdRessource();
		 
	 }
	 
	 public List<UserQuestion> getTeachersQuestion() throws IOException, SerialException, SQLException{
		 List<Question> list=new ArrayList<>(); 
		 List<Question> questions =this.retrieveQuestions();
			List<UserQuestion> result=new ArrayList<>();
		for (Question question :questions) {
			if(question.getUserquestions().getRole()==Role.teacher)
				list.add(question);
		}
			
			for (Question q : list) {
				User user=userRepository.findById(q.getUserquestions().getId()).get();
				String nom=user.getFirstName()+" "+user.getLastName();
				List<String> tags=new ArrayList<>();
				for (Tag tag : q.getTags()) {
					tags.add(tag.getTitle());	
				}
			
				
				Ressource ressource=q.getQuestionressources().stream().findFirst().orElse(null);
				if(ressource != null) {
				if(ressource.getType() ==TypeRessource.Image){
					result.add(new UserQuestion(q.getIdQuestion(), nom, q.getContent(), q.getDatepub(), q.getTitle(), q.getNbresp(),tags,q.getUserquestions().getRole().toString(),responseServiceImp.getQuestionAnswersNotApproved(q.getIdQuestion()).size(),responseServiceImp.AffectBadge(q.getUserquestions().getId()),this.downloadImage(q.getUserquestions().getImage()),this.downloadImage(ressource.getLibelle())));
			
				}
				else
				
				result.add(new UserQuestion(q.getIdQuestion(), nom, q.getContent(), q.getDatepub(), q.getTitle(), q.getNbresp(),tags,q.getUserquestions().getRole().toString(),responseServiceImp.getQuestionAnswersNotApproved(q.getIdQuestion()).size(),responseServiceImp.AffectBadge(q.getUserquestions().getId()),this.downloadImage(q.getUserquestions().getImage()),this.downloadFile(ressource.getLibelle())));
				
				}
				else {
					result.add(new UserQuestion(q.getIdQuestion(), nom, q.getContent(), q.getDatepub(), q.getTitle(), q.getNbresp(),tags,q.getUserquestions().getRole().toString(),responseServiceImp.getQuestionAnswersNotApproved(q.getIdQuestion()).size(),responseServiceImp.AffectBadge(q.getUserquestions().getId()),this.downloadImage(q.getUserquestions().getImage()),""));

				}
				
			}
			
			return result;
		 
	 }
	 
	 public List<UserQuestion> getStudentsQuestion() throws IOException, SerialException, SQLException{
		 List<Question> list=new ArrayList<>(); 
		 List<Question> questions =this.retrieveQuestions();
			List<UserQuestion> result=new ArrayList<>();
		for (Question question :questions) {
			if(question.getUserquestions().getRole()==Role.user)
				list.add(question);
		}
			
			for (Question q : list) {
				User user=userRepository.findById(q.getUserquestions().getId()).get();
				String nom=user.getFirstName()+" "+user.getLastName();
				List<String> tags=new ArrayList<>();
				for (Tag tag : q.getTags()) {
					tags.add(tag.getTitle());	
				}
			
				
				Ressource ressource=q.getQuestionressources().stream().findFirst().orElse(null);
				if(ressource != null) {
				if(ressource.getType() ==TypeRessource.Image){
					result.add(new UserQuestion(q.getIdQuestion(), nom, q.getContent(), q.getDatepub(), q.getTitle(), q.getNbresp(),tags,q.getUserquestions().getRole().toString(),responseServiceImp.getQuestionAnswersNotApproved(q.getIdQuestion()).size(),responseServiceImp.AffectBadge(q.getUserquestions().getId()),this.downloadImage(q.getUserquestions().getImage()),this.downloadImage(ressource.getLibelle())));
			
				}
				else
				
				result.add(new UserQuestion(q.getIdQuestion(), nom, q.getContent(), q.getDatepub(), q.getTitle(), q.getNbresp(),tags,q.getUserquestions().getRole().toString(),responseServiceImp.getQuestionAnswersNotApproved(q.getIdQuestion()).size(),responseServiceImp.AffectBadge(q.getUserquestions().getId()),this.downloadImage(q.getUserquestions().getImage()),this.downloadFile(ressource.getLibelle())));
				
				}
				else {
					result.add(new UserQuestion(q.getIdQuestion(), nom, q.getContent(), q.getDatepub(), q.getTitle(), q.getNbresp(),tags,q.getUserquestions().getRole().toString(),responseServiceImp.getQuestionAnswersNotApproved(q.getIdQuestion()).size(),responseServiceImp.AffectBadge(q.getUserquestions().getId()),this.downloadImage(q.getUserquestions().getImage()),""));

				}
				
			}
			
			return result;
		 
	 }
	 
	 public String downloadImage( String name) throws IOException {
		 FTPClient ftpClient = new FTPClient();
		 String encodidImage="";
		 byte[] data;
		  try {
      		
       	   ftpClient.connect("192.168.1.19", 21);
		         ftpClient.login("ftpuser", "ftpuser");
           ftpClient.enterLocalPassiveMode();
           ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

           System.out.println("probleme1");
           // APPROACH #2: using InputStream retrieveFileStream(String)
           String remoteFile2 = name;
           
           System.out.println(name);
           InputStream inputStream = ftpClient.retrieveFileStream(remoteFile2);
           System.out.println("probleme2");
           ByteArrayOutputStream buffer = new ByteArrayOutputStream();
           System.out.println("probleme3");
           System.out.println(inputStream);
           data = IOUtils.toByteArray(inputStream);
           System.out.println("probleme4");
           /*int bytesRead = -1;
           int nRead;
           while (( bytesRead=inputStream.read(bytesArray,0,bytesArray.length)) != -1) {
               //outputStream2.write(bytesArray, 0, bytesRead);
           	buffer.write(bytesArray,0,bytesRead);
           }*/

           boolean success = ftpClient.completePendingCommand();
           if (success) {
               System.out.println("File #2 has been downloaded successfully.");
           }
          // outputStream2.close();
           inputStream.close();
          
           ByteArrayResource resource = new ByteArrayResource(data);
           System.out.println("probleme5");
            encodidImage=Base64.getEncoder().encodeToString(data);
           encodidImage="data:image/png;base64,"+encodidImage;
           System.out.println("hay dataaaa=>>>>>>>  "+encodidImage);

           


       } catch (IOException ex) {
           System.out.println("Error: " + ex.getMessage());
           ex.printStackTrace();
          
       }
		  
		  finally {
           try {
               if (ftpClient.isConnected()) {
                   ftpClient.logout();
                   ftpClient.disconnect();
                   
               }
           } catch (IOException ex) {
               ex.printStackTrace();
           }
           
           return encodidImage; 
          
       }
	 }
	 
	

}
