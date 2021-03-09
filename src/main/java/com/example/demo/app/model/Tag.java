package com.example.demo.app.model;

import com.example.demo.app.model.listeners.TagListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@EntityListeners(TagListener.class)
@Table(name = "test_tags",
	uniqueConstraints=@UniqueConstraint(columnNames = "title"))
@EqualsAndHashCode
@ToString
public class Tag implements TagModel {
	@Id
	@GeneratedValue(generator = "uuid32")
	@Getter @Setter private UUID uid;
	
	@NotBlank(message = "Заголовок тега должен быть заполнен")
	@Size(min=2, max=30,
		message = "Заголовок тега должен содержать от 2 до 30 символов")
	@Getter @Setter private String title;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tag", cascade = CascadeType.REMOVE)
	@Getter @Setter private Set<Task> tasks = new HashSet<>();

	public static final String FIND_BY_TITLE = "Tag.findByTitle";
	public static final String FIND_ALL = "Tag.findAll";
	public static final String FIND_WITH_TASKS_BY_UID = "Tag.findWithTasksByUid";
}
