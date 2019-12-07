package demo.shiro.realm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RealmApplication05 implements ApplicationRunner {

	@Autowired
	private BaseTest baseTest;

	public static void main(String[] args) {
		SpringApplication.run(RealmApplication05.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
//		BaseTest baseTest = new BaseTest(); // 如果是自己new的对象，那么就不是spring管理的bean，则不会自动注入依赖，即@Autowired无效
		baseTest.setUp();
	}
}
