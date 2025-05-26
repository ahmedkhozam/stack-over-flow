package com.example.demo;

import com.example.demo.entity.Badge;
import com.example.demo.repository.BadgeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner seedBadges(BadgeRepository badgeRepository) {
		return args -> {
			if (badgeRepository.count() == 0) {

				Badge niceAnswer = Badge.builder()
						.name("Nice Answer")
						.description("Get 10 upvotes on a single answer")
						.level("BRONZE")
						.build();

				Badge goodQuestion = Badge.builder()
						.name("Good Question")
						.description("Get 10 upvotes on a question")
						.level("BRONZE")
						.build();

				Badge enlightened = Badge.builder()
						.name("Enlightened")
						.description("Have an accepted answer with the highest votes")
						.level("SILVER")
						.build();

				badgeRepository.save(niceAnswer);
				badgeRepository.save(goodQuestion);
				badgeRepository.save(enlightened);

				System.out.println("✅ Badges seeded into the database!");
			}
		};
	}


}
