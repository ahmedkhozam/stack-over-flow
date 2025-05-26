package com.example.demo.config;

import com.example.demo.entity.Answer;
import com.example.demo.entity.Badge;
import com.example.demo.entity.Question;
import com.example.demo.entity.StackUser;
import com.example.demo.repository.AnswerRepository;
import com.example.demo.repository.BadgeRepository;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.StackUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final StackUserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final BadgeRepository badgeRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            // 1. إنشاء مستخدمين
            StackUser user1 = StackUser.builder()
                    .username("ahmed")
                    .email("ahmed@example.com")
                    .password(passwordEncoder.encode("123"))
                   // .role(Role.MEMBER)
                    .reputation(100)
                    .build();

            StackUser user2 = StackUser.builder()
                    .username("sara")
                    .email("sara@example.com")
                    .password(passwordEncoder.encode("123"))
                  //  .role(Role.MEMBER)
                    .reputation(150)
                    .build();

            StackUser user3 = StackUser.builder()
                    .username("ali")
                    .email("ali@example.com")
                    .password(passwordEncoder.encode("123"))
                    // .role(Role.MEMBER)
                    .reputation(100)
                    .build();

            userRepository.saveAll(List.of(user1, user2,user3));

            // 2. إنشاء أسئلة
            Question q1 = Question.builder()
                    .title("How to use Spring Boot?")
                    .content("I want to understand how Spring Boot works.")
                    .author(user1)
                    .build();

            Question q2 = Question.builder()
                    .title("What is Dependency Injection?")
                    .content("Can someone explain DI in simple terms?")
                    .author(user2)
                    .build();

            questionRepository.saveAll(List.of(q1, q2));

            // 3. إنشاء إجابات
            Answer a1 = Answer.builder()
                    .content("Spring Boot simplifies application setup using autoconfiguration.")
                    .question(q1)
                    .author(user2)
                    .build();

            Answer a2 = Answer.builder()
                    .content("Dependency Injection is about providing dependencies instead of creating them.")
                    .question(q2)
                    .author(user1)
                    .build();

            answerRepository.saveAll(List.of(a1, a2));

            // 4. شارات
            Badge b1 = Badge.builder().name("Helpful").description("Received 5 upvotes").build();
            Badge b2 = Badge.builder().name("Expert").description("Accepted 3 answers").build();

            badgeRepository.saveAll(List.of(b1, b2));

            System.out.println(" Seed data inserted successfully.");
        };
    }
}
