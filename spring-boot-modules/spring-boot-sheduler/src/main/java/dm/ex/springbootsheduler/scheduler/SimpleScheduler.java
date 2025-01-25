package dm.ex.springbootsheduler.scheduler;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SimpleScheduler {

    @Scheduled(fixedRateString = "15000") //15 сек
    @SchedulerLock(name = "SimpleScheduler.scheduledTask", lockAtLeastFor = "PT15S", lockAtMostFor = "PT30S")
    public  void  scheduledTask () {
        System.out.println( "Планировщик открыт: " + LocalDateTime.now());
    }
}
