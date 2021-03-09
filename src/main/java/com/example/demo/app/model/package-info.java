@NamedQueries({
        @NamedQuery(
                name=Tag.FIND_BY_TITLE,
                query="select t from Tag t where t.title = :title"),
        @NamedQuery(
                name=Tag.FIND_ALL,
                query="select t from Tag t"),
        @NamedQuery(
                name=Tag.FIND_WITH_TASKS_BY_UID,
                query="select t from Tag t join fetch t.tasks where t.uid = :uid"),

        @NamedQuery(
                name=Task.FIND_ALL_WITHOUT_TAG,
                query="select t from Task t"),
        @NamedQuery(
                name=Task.FIND_ALL_WITH_TAG,
                query="select t from Task t join fetch t.tag"),
        @NamedQuery(
                name=Task.FIND_WITHOUT_TAG_BY_UID_TAG,
                query="select t from Task t where t.tag.uid = :uidTag")
})
package com.example.demo.app.model;

import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.NamedQueries;