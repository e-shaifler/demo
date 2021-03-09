CREATE TABLE public.test_tags
(
    uid uuid NOT NULL,
    title character varying(30) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT test_tag_pkey PRIMARY KEY (uid),
    CONSTRAINT uix_test_tag_title UNIQUE (title)
)

CREATE TABLE public.test_tasks
(
    uid uuid NOT NULL,
    name character varying(50) COLLATE pg_catalog."default" NOT NULL,
    desc_ character varying(4096) COLLATE pg_catalog."default" NOT NULL,
    date date NOT NULL,
    uid_tag uuid NOT NULL,
    CONSTRAINT test_tasks_pkey PRIMARY KEY (uid),
    CONSTRAINT fk_test_task_tag FOREIGN KEY (uid_tag)
        REFERENCES public.test_tags (uid) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)

TABLESPACE pg_default;

ALTER TABLE public.test_tasks
    OWNER to liferay;

CREATE INDEX fki_fk_test_task_tag
    ON public.test_tasks USING btree
    (uid_tag ASC NULLS LAST)
    TABLESPACE pg_default;
