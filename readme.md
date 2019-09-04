
##Инструкция для теста приложения:

TestPage - считаывает контент файла index.html и отдает в HttpServletResponse, в результате видим страницу на экране

1. Для доступа к тестовой странице в браузере необходимо перейти по адресу:
    localhost:8093/app/index.html
2. http://localhost:8093/app/test - тестовый сервлет TestPage
3. http://localhost:8093/app/api/admin - доступ к REST API (Jersey)
    3.1 Данный ресурс защищен BASIC аутентификацией


##Реализация form based login
1. Необходимо указать login page и error page - login.html, error.html
2. Определить кто будет отдавать login.html - отдельный зарегистрированный Servlet, Jersey Rest Handler, Tomcat как статический ресурс
    2.1 В последнем случае нашему веб приложению (контексту) необходим дефолтный Servlet

##Процесс настройки DefaultServlet для обслуживания статического контента:
1. За процесс сопоставления url маппингов из web.xml и uri ресурса ответчает объект Mapper
2. Маппер по очереди использует: exactWrappers, wildcardWrappers, extensionWrappers
3. exactWrappers имеет __наивысший__ приоритет и матчит первым, если совпадет uri, например матч произойдет:
    - сервлет замаплен на /api/*, а uri статического ресурса /api/index.html 
4. Отсюда вывод: для того чтобы сработал DefaultServlet uri ресурса не должен совпадать ни с одним маппингом, кроме DefaultServlet

##Настройка JNDIRealm и описание маппинга этих настроек на соответствующий фильтр InitialDirContext

1. Дополнительные настройки для поиск ролей приводят к запрос с использованием такого фильтра:

Устанавливаем параметры реалма:
//приводит к установке имени возвращаемых атрибутов для группы - controls.setReturningAttributes(new String[] {roleName});
realm.setRoleName("cn");

realm.setRoleSearch("(uniqueMember={0})");
Получаем в результате такой фильтр поиска:
(uniqueMember=CN=zpesvcfilenetefd,OU=Accounts,OU=TechUsersZPE,OU=ZPE,OU=eispd,DC=portal,DC=cbr,DC=ru)

##Всякие вопросы
1. Можно ли совместить programmatic mode с настройкой через стандартные файлы конфигов, например, tomcat-users.xml?
    Томкат использует временные папки tomcat.<port> для хранения конфигов и возможно еще других файлов
2. Ститические ресурсы можно кэшировать? - см. StandardRoot->getResource()->isCachingAllowed() метод

##Напоминалки
_Default Servlet_:
 - Static content может быть предоставлен при помощи DefaultServlet, например welcome files, которые являются частью Servlet API
 - Может предоставить листинг директорий
    