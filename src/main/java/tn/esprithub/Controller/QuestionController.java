package tn.esprithub.Controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import tn.esprithub.Entities.Question;
import tn.esprithub.Entities.Response;
import tn.esprithub.Entities.Ressource;
import tn.esprithub.Entities.UserQuestion;
import tn.esprithub.Services.QuestionServiceImp;
import tn.esprithub.Services.ResponseServiceImp;



@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/question")

public class QuestionController {
	
	@Autowired
	private QuestionServiceImp questionservice;
	
	@Autowired
	private ResponseServiceImp responseServiceImp;
	
	
	
	
	@PostMapping("/addQuestion")
	@ResponseBody
	public Question addQuestion(@RequestBody Question question,@RequestParam("ress") Long ress) {
		return questionservice.addQuestion(question,ress);
	}
	
	@PutMapping("/updateQuestion")
	@ResponseBody
	public Question updateQuestion(@RequestBody Question question) {
		return questionservice.updateProduit(question);
	}
	
	@GetMapping("/Question")
	@ResponseBody
	public Question retrieveQuestion(@RequestParam("id") Long id) {
		return questionservice.retrieveQuestion(id);
	}
	
	@GetMapping("/Questions")
	@ResponseBody
	public List<Question> retrieveQuestions(){
		return questionservice.retrieveQuestions();
	}
	
	@GetMapping("/Userquestions")
	@ResponseBody
	public List<UserQuestion> getAllUserQuestions() throws IOException{
		return questionservice.getAllUserQuestions();
	}
	
	@GetMapping("/Userquestion")
	@ResponseBody
	public UserQuestion getUserQuestion(@RequestParam("id") Long id) throws IOException {
		return questionservice.getQuestion(id);
	}
	
	@PutMapping("/ResponseNumber")
	public void updateAnswersNumber(@RequestBody Question question) {
		questionservice.updateAnswersNumber(question.getIdQuestion());
	}
	
	@PutMapping("/LikeQuestion")
	public void updateLike(@RequestBody Question question,@RequestParam("idu") Long idu) {
		questionservice.LikeQuestion(question.getIdQuestion(),idu);
	}
	
	@PutMapping("/DislikeQuestion")
	public void updateDislike(@RequestBody Question question,@RequestParam("idu") Long idu) {
		questionservice.DislikeQuestion(question.getIdQuestion(),idu);
	}
	
	@PutMapping("/CloseQuestion")
	public void closeQuestion(@RequestBody Question question) {
		questionservice.closeQuestion(question.getIdQuestion());
	}
	
	
	@PostMapping("/UploadFile")
	@ResponseBody
	public String uploadfile(@RequestParam("file") MultipartFile file) {
		return questionservice.uploadfile(file);
	}
	
	@GetMapping("/downloadFile")
	@ResponseBody
	public ResponseEntity downloadFile(@RequestParam("name") String name) {
		return questionservice.downloadFile(name);
	}
	
	@PostMapping("/addRessource")
	@ResponseBody
	public Long addRessource(@RequestBody Ressource ressource) {
		return questionservice.addRessource(ressource);
	}
	
	@GetMapping("/TeachersQuestion")
	@ResponseBody
	public List<UserQuestion> getTeachersQuestions() throws IOException{
		return questionservice.getTeachersQuestion();
	}
	
	
	

}
