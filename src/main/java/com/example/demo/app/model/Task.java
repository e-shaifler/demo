package com.example.demo.app.model;

import com.example.demo.app.model.listeners.TaskListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;


@Entity
@EntityListeners(TaskListener.class)
@Table(name = "test_tasks")
@ToString
@EqualsAndHashCode
public class Task implements TaskModel {

	@Id
	@GeneratedValue(generator = "uuid32")
	@Getter @Setter private UUID uid;
	
	@NotBlank(message = "Наименование задачи должно быть заполнено")
	@Size(min=3, max=50,
			message = "Наименование задачи должно содержать от 3 до 50 символов")
	@Getter @Setter private String name;
	
	@NotBlank(message = "Описание задачи должно быть заполнено")
	@Size(min=3, max=4096,
			message = "Описание задачи должно содержать от 3 до 4096 символов")
	@Column(name="desc_")
	@Getter @Setter private String desc;

	@NotNull(message = "Дата задачи должна быть заполена")
	@PastOrPresent(message = "Дата задачи не может быть в будущем времени")
	//@JsonFormat(pattern = "dd.MM.yyyy")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@Getter @Setter private LocalDate date;

	@NotNull(message = "Должен быть указан тег")
	@Access(AccessType.PROPERTY)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="uid_tag")
	@JsonIgnore
	@EqualsAndHashCode.Exclude
	@Getter private Tag tag;

	public void setTag(Tag tag){
		if(this.tag != null){
			lastUidTag = uidTag;
		}
		this.tag = tag;
		uidTag = tag == null? null : tag.getUid();
	}

	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@Getter @Setter private transient UUID lastUidTag;

	@Getter @Setter private transient UUID uidTag;

	public static final String FIND_ALL_WITHOUT_TAG = "Task.findAll";
	public static final String FIND_ALL_WITH_TAG = "Task.findAllWithTag";
	public static final String FIND_WITHOUT_TAG_BY_UID_TAG = "Task.findWithoutTagByUidTag";
}
