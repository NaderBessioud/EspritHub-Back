package tn.esprithub.Services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.stereotype.Service;

import tn.esprithub.Entities.Question;
import tn.esprithub.Entities.Ressource;
import tn.esprithub.Entities.Tag;
import tn.esprithub.Entities.TypeRessource;
import tn.esprithub.Entities.Role;
import tn.esprithub.Entities.UserQuestion;
import tn.esprithub.Repository.QuestionRepository;
import tn.esprithub.Repository.TagRepository;



@Service
public class TagService implements ITagService{

	@Autowired 
	private TagRepository tagrepository;
	
	@Autowired
	private QuestionServiceImp questionRepository;
	
	@Autowired
	private ResponseServiceImp imp;
	
	@Autowired
	private ResponseServiceImp responseServiceImp; 
	@Override
	public Tag addTag(Tag q) {
		Tag tag=tagrepository.findByTitle(q.getTitle());
		if(tag != null) 
		return tagrepository.save(q);
		else {
			tag.setNumberOfQuestion(tag.getNumberOfQuestion()+1);
			tagrepository.save(tag);
			return tag;
		}
			
	}

	@Override
	public List<Tag> retrieveTags() {
		return (List<Tag>) tagrepository.findAll();
	}

	@Override
	public void deleteTag(Long id) {
		tagrepository.deleteById(id);
		
	}

	@Override
	public Tag updateTag(Tag u) {
		return tagrepository.save(u);
	}

	@Override
	public Tag retrieveTag(Long id) {
		return tagrepository.findById(id).get();
	}
	
	
	@Transactional
	public void addTagAndAffectQuestion(String tags,Long id) {
		String[] s=tags.split(",");
		List<String> tagss =new ArrayList<>();
		for (String string : s) {
			tagss.add(string);
			}
		Question question=questionRepository.retrieveQuestion(id);
		for (String tagg : tagss) {
			Tag tag=tagrepository.findByTitle(tagg);
			if(tag != null) {
				tag.setNumberOfQuestion(tag.getNumberOfQuestion()+1);
				question.getTags().add(tag);
				tag.getQuestiontag().add(question);
				tagrepository.save(tag);
			}
			else {
				HashSet<Question> questions=new HashSet<>();
				questions.add(question);
				tag=new Tag();
				tag.setTitle(tagg);
				tag.setNumberOfQuestion(1);
				
				tag.setQuestiontag(questions);
				question.getTags().add(tag);

				tagrepository.save(tag);
				
			}
		}
		
		questionRepository.updateProduit(question);
	}
	

	public List<UserQuestion> getQuestionByTag(String title) throws IOException, SerialException, SQLException{
		Tag tag=tagrepository.findByTitle(title);
		List<UserQuestion> result=new ArrayList<>();
try {
		for (Question q : tag.getQuestiontag()) {
			List<String> tags=new ArrayList<>();
			for (Tag tagg : q.getTags()) {
				tags.add(tagg.getTitle());
			}
			
			String nom=q.getUserquestions().getFirstName()+" "+q.getUserquestions().getLastName();
			
			Ressource ressource=q.getQuestionressources().stream().findFirst().orElse(null);
			if(ressource != null) {
				if(ressource.getType()==TypeRessource.Image)
					result.add(new UserQuestion(q.getIdQuestion(), nom, q.getContent(), q.getDatepub(), q.getTitle(), q.getNbresp(), null, q.getUserquestions().getRole().toString(),imp.getQuestionAnswersNotApproved(q.getIdQuestion()).size(),responseServiceImp.AffectBadge(q.getUserquestions().getId()), questionRepository.downloadImage(q.getUserquestions().getImage()),this.downloadImage(ressource.getLibelle())));

			
			else {
				result.add(new UserQuestion(q.getIdQuestion(), nom, q.getContent(), q.getDatepub(), q.getTitle(), q.getNbresp(), null, q.getUserquestions().getRole().toString(),imp.getQuestionAnswersNotApproved(q.getIdQuestion()).size(),responseServiceImp.AffectBadge(q.getUserquestions().getId()), questionRepository.downloadImage(q.getUserquestions().getImage()),this.downloadFile(ressource.getLibelle())));

			}
			}
			else {
				result.add(new UserQuestion(q.getIdQuestion(), nom, q.getContent(), q.getDatepub(), q.getTitle(), q.getNbresp(), null, q.getUserquestions().getRole().toString(),imp.getQuestionAnswersNotApproved(q.getIdQuestion()).size(),responseServiceImp.AffectBadge(q.getUserquestions().getId()), questionRepository.downloadImage(q.getUserquestions().getImage()),""));

			}

		}
		}
catch(NullPointerException ex) {
	
}
		return result;
		
	}
	
	public List<UserQuestion> getTeachersQuestionsByTag(String title) throws IOException, SerialException, SQLException{
		
		Tag tag=tagrepository.findByTitle(title);
		List<UserQuestion> result=new ArrayList<>();
try {
		for (Question q : tag.getQuestiontag()) {
			if(q.getUserquestions().getRole()==Role.teacher) {
			List<String> tags=new ArrayList<>();
			for (Tag tagg : q.getTags()) {
				tags.add(tagg.getTitle());
			}
			
			String nom=q.getUserquestions().getFirstName()+" "+q.getUserquestions().getLastName();
			Ressource ressource=q.getQuestionressources().stream().findFirst().orElse(null);
			if(ressource != null) {
				if(ressource.getType()==TypeRessource.Image)
					result.add(new UserQuestion(q.getIdQuestion(), nom, q.getContent(), q.getDatepub(), q.getTitle(), q.getNbresp(), null, q.getUserquestions().getRole().toString(),imp.getQuestionAnswersNotApproved(q.getIdQuestion()).size(),responseServiceImp.AffectBadge(q.getUserquestions().getId()),questionRepository.downloadImage(q.getUserquestions().getImage()),this.downloadImage(ressource.getLibelle()) ));

			
			else {
				result.add(new UserQuestion(q.getIdQuestion(), nom, q.getContent(), q.getDatepub(), q.getTitle(), q.getNbresp(), null, q.getUserquestions().getRole().toString(),imp.getQuestionAnswersNotApproved(q.getIdQuestion()).size(),responseServiceImp.AffectBadge(q.getUserquestions().getId()),questionRepository.downloadImage(q.getUserquestions().getImage()),this.downloadFile(ressource.getLibelle()) ));

			}
			}
			else {
				result.add(new UserQuestion(q.getIdQuestion(), nom, q.getContent(), q.getDatepub(), q.getTitle(), q.getNbresp(), null, q.getUserquestions().getRole().toString(),imp.getQuestionAnswersNotApproved(q.getIdQuestion()).size(),responseServiceImp.AffectBadge(q.getUserquestions().getId()),questionRepository.downloadImage(q.getUserquestions().getImage()),"" ));

			}
			}}}
catch(NullPointerException ex) {
	
}
		return result;
		
	
	}
	
	 public List<UserQuestion> getSimilarQuestionByTags(String tags) throws IOException, SerialException, SQLException{
		 List<UserQuestion> result=new ArrayList<>();
		 List<String> tagss = Arrays.asList(tags.split(",", -1));
		 for (String string : tagss) {
			 List<UserQuestion> similar=new ArrayList<>();
			 similar=this.getQuestionByTag(string);
			 result.addAll(similar);
		}
		 Collections.shuffle(result);
		 result=result.stream().limit(5).collect(Collectors.toList());
		 return result;
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
