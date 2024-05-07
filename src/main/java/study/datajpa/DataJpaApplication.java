package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing //spring Data Jpa가 createTime, updateTime 등의 기능을 구현케 하기 위해 필요
@SpringBootApplication
//@EnableJpaRepositories(basePackages = "study.datajpa.repository") //를 해줘야 하지만 부트가 알아서 해준다
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
		
		//예를 든것이지 실제로는 이렇게 하는것이 아님
		//실제로는 세션에서 꺼내 쓰거나 하면 된다.
		return () -> Optional.of(UUID.randomUUID().toString());
	}
}
