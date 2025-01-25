package dm.ex.springbootsheduler;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT60S")
@SpringBootApplication
public class SpringBootShedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootShedulerApplication.class, args);
    }

}
