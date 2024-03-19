# reactive-spring-webflux

Я прошла курс 
[Build Reactive MicroServices using Spring WebFlux/SpringBoot](https://www.udemy.com/course/build-reactive-restful-apis-using-spring-boot-webflux)
на платформе Udemy.com. 

Думала, что будет нужно по работе. Оказалось, что пока не требуется :slightly_smiling_face: 
но не бросать же было курс на середине.

---

Модуль `reactive-programming-using-reactor` содержит базовые методы работы с Flux & Mono publishers
из библиотеки reactor.

Модуль `movies-info-service` - это обычный Spring Boot сервис на MVC-аннотациях.

Модуль `movies-reviews-service` - Spring Boot сервис, в котором эндпоинты описаны в функциональном стиле.

Модуль `movies-service` - это сервис верхнего уровня, который собирает данные с двух сервисов нижнего
уровня и возвращает объединённый результат:

:film_strip: Movie = MovieInfo + MovieReviews :film_strip:

---

В качестве базы данных использовалась MongoDB, а я с ней работать не очень-то и умею :confused:
Поэтому те части уроков, в которых преподаватель запускал сервисы на выполнение, я просто просматривала,
не повторяя. 

К счастью, функционал проверялся также и с помощью JUnit-тестов, в том числе с использованием
библиотеки WireMock.

Курс могу рекомендовать, в настоящее время он наиболее полный из существующих на Udemy по технологии
Reactive/WebFlux :+1: