package tn.esprithub.Services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tn.esprithub.Entities.Question;
import tn.esprithub.Entities.Tag;
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
	

	public List<UserQuestion> getQuestionByTag(String title) throws IOException{
		Tag tag=tagrepository.findByTitle(title);
		List<UserQuestion> result=new ArrayList<>();
try {
		for (Question q : tag.getQuestiontag()) {
			List<String> tags=new ArrayList<>();
			for (Tag tagg : q.getTags()) {
				tags.add(tagg.getTitle());
			}
			
			String nom=q.getUserquestions().getFirstName()+" "+q.getUserquestions().getLastName();
			result.add(new UserQuestion(q.getIdQuestion(), nom, q.getContent(), q.getDatepub(), q.getTitle(), q.getNbresp(), null, q.getUserquestions().getRole().toString(),imp.getQuestionAnswersNotApproved(q.getIdQuestion()).size(),responseServiceImp.AffectBadge(q.getUserquestions().getId()), questionRepository.downloadImage(q.getUserquestions().getImage())));
		}}
catch(NullPointerException ex) {
	
}
		return result;
		
	}
	
	public List<UserQuestion> getTeachersQuestionsByTag(String title) throws IOException{
		
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
			result.add(new UserQuestion(q.getIdQuestion(), nom, q.getContent(), q.getDatepub(), q.getTitle(), q.getNbresp(), null, q.getUserquestions().getRole().toString(),imp.getQuestionAnswersNotApproved(q.getIdQuestion()).size(),responseServiceImp.AffectBadge(q.getUserquestions().getId()),questionRepository.downloadImage(q.getUserquestions().getImage()) ));
			}}}
catch(NullPointerException ex) {
	
}
		return result;
		
	
	}
	
	 public List<UserQuestion> getSimilarQuestionByTags(String tags) throws IOException{
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

}
