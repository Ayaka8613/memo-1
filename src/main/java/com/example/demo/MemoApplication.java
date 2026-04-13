package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.demo.entity.Tag;
import com.example.demo.repository.TagRepository;


@SpringBootApplication
public class MemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemoApplication.class, args);
    }

    @Bean
    CommandLineRunner initTags(TagRepository tagRepository) {
        return args -> {
            if (tagRepository.count() == 0){
                tagRepository.save(new Tag("仕事"));
                tagRepository.save(new Tag("タスク"));
                tagRepository.save(new Tag("報告書"));
                tagRepository.save(new Tag("勉強法"));
                tagRepository.save(new Tag("買い物"));
                tagRepository.save(new Tag("趣味"));
                tagRepository.save(new Tag("レシピ"));
                tagRepository.save(new Tag("健康"));
                tagRepository.save(new Tag("旅行"));
                tagRepository.save(new Tag("映画"));
                tagRepository.save(new Tag("音楽"));
                tagRepository.save(new Tag("読書"));
                tagRepository.save(new Tag("スポーツ"));
                tagRepository.save(new Tag("その他"));
            }
        };
    }
}