package com.example.demo.app.rest;

import com.example.demo.app.model.Tag;
import com.example.demo.app.model.TagWithTasks;
import com.example.demo.app.model.TagWithoutTasks;
import com.example.demo.app.rest.exception.BaseLogicRestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
@Validated
public class TagRestController extends BaseRestController{

	@GetMapping(path="/tags", produces = MediaType.APPLICATION_JSON_VALUE)
	public Iterable<Tag> getAllTag() {
		return tagService.findAll();
	}

	@GetMapping(path="/tag/{uid}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TagWithTasks> getTagWithTasks(@PathVariable("uid") UUID uid) {
		Optional<TagWithTasks> optionalTagWithTasks = tagService.getTagWithTasksByUidTag(uid);
		if(optionalTagWithTasks.isEmpty()){
			throw new BaseLogicRestException(
					MessageFormat.format("Не существует тега с uid = {0}",
							uid.toString()),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(optionalTagWithTasks.get(), HttpStatus.OK);
	}

	@PostMapping(path="/tag" ,produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TagWithoutTasks> createOrUpdateTag(@Valid Tag tag) {
		if(log.isDebugEnabled())
			log.debug(tag.toString());
		if(tag.getUid() != null) {
			checkExistsTag(tag);
			return new ResponseEntity<>(new TagWithoutTasks(tagService.save(tag)), HttpStatus.OK);
		}
		checkNoExistsTagWithTitle(tag.getTitle());
		return new ResponseEntity<>(new TagWithoutTasks(tagService.create(tag)), HttpStatus.CREATED);
	}

	@DeleteMapping(path="/tag/{uid}")
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("uid") UUID uid) {
		tagService.deleteByUid(uid);
	}


	protected void checkNoExistsTagWithTitle(String title){
		Optional<Tag> optionalTag = tagService.findByTitle(title);
		if(optionalTag.isPresent()){
			Tag tag = optionalTag.get();
			if(log.isDebugEnabled())
				log.debug(tag.toString());
			throw new BaseLogicRestException(
					MessageFormat.format("Уже имеется тег {1} с uid = {0}",
							tag.getUid().toString(), tag.getTitle()),
					HttpStatus.CONFLICT);
		}
	}
}
