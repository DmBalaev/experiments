#### Для чего нужно блокировка?
Для начала разберемся что такое запланированные задачи. Это процедуры которые запускаются в определенное время. В приложении с одним экземпляром не возникнет ни каких проблем, но при работе с несколькими экземплярами задачи будут запускаться более одно раза(т.е каждый экземпляр будет запускать запланированную задачу).

#### Зависимости:
```xml
<dependency>
    <groupId>net.javacrumbs.shedlock</groupId>
    <artifactId>shedlock-spring</artifactId>
    <version>6.2.0</version>
</dependency>
<dependency>
    <groupId>net.javacrumbs.shedlock</groupId>
    <artifactId>shedlock-provider-jdbc-template</artifactId>
    <version>6.2.0</version>
</dependency>
```

#### Настройка приложения
В майн классе(с `@SpringBootApplication`) нужно добавить две аннотации `@EnableScheduling` и
`@EnableSchedulerLock(defaultLockAtMostFor = "PT60S")`. Параметр _defaultLockAtMostFor_ задает ограничение по времени блокировки по умолчанию, можно переопределить в задаче.

И создать конфигурацию (класс `SchedulerlockConfig`)


Настройка подключения к БД
```yaml
spring:  
	datasource:  
		username: sa  
		url: jdbc:h2:mem:mydb  
		driverClassName: org.h2.Driver  
		password: password  
	h2:  
		console:  
			enabled: 'true'  
			settings:  
				web-allow-others: 'true'  
	jpa:  
		database-platform: org.hibernate.dialect.H2Dialect
```

Нужно создать таблицу для нужд shedlock. В папке resource создадим файл _schema.sql_
```sql
CREATE TABLE IF NOT EXISTS shedlock (  
	name VARCHAR(64),  
	lock_until TIMESTAMP(3) NULL,  
	locked_at TIMESTAMP(3) NULL,  
	locked_by VARCHAR(255),  
	PRIMARY KEY (name)  
);
```

Создадим класс для задачи по расписанию
```java
import lombok.extern.slf4j.Slf4j;  
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;  
import org.springframework.scheduling.annotation.Scheduled;  
import org.springframework.stereotype.Component;  
  
import java.time.LocalDateTime;  
  
@Slf4j  
@Component  
public class RoutineScheduler {  
    @Scheduled(fixedRateString = "15000") //15 сек  
    @SchedulerLock(name = "RoutineScheduler.scheduledTask", lockAtLeastFor = "PT15S", lockAtMostFor = "PT30S")  
    public  void  scheduledTask () {  
        log.info("Планировщик открыт: {} ", LocalDateTime.now());  
    }  
}
```

**_Аннотация Scheduled_** : указывает, что метод — это задача, которая будет выполняться повторно.  
Здесь мы сообщаем, что наша задача будет выполняться каждые 15 секунд. Мы можем изменить единицу времени и использовать другие способы информирования об интервале времени, например crons.

**_Аннотация SchedulerLock_** : указывает на запланированную задачу, которая должна быть заблокирована.  
_name_ : указывает на имя задачи, имя должно быть уникальным, в общем случае, имени класса и имени метода достаточно, чтобы это гарантировать. Имя используется для блокировки задачи, не допускается выполнение задач с одинаковым именем в одно и то же время.

**_lockAtLeastFor_** : указывает минимальное время, в течение которого задача будет заблокирована, здесь мы используем шаблон даты ISO 8601.

**_lockAtMostFor_** : указывает максимальное время, в течение которого задача должна оставаться заблокированной в случае сбоя приложения. Если этот параметр здесь не указан, будет использоваться параметр, указанный в аннотации _EnableSchedulerLock(defaultLockAtMostFor = «PT60S»)_

Запустите свое приложение с помощью IDE или терминала. После этого откройте консоль h2 через браузер: [http://localhost:8080/h2-console/](http://localhost:8080/h2-console/) Пароль, логин и url взять и пропертей
```sql
SELECT * FROM shedlock
```

#### Плюсы:
- Простая реализация.
- Не требует сложной настройки.
- Поддерживает распределённую среду.
#### Минусы:
- Зависимость от сторонней библиотеки.
- Нагрузка на базу данных.
