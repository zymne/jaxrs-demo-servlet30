
##Инструкция для теста приложения:

TestPage - считаывает контент файла index.html и отдает в HttpServletResponse, в результате видим страницу на экране

1. Для доступа к тестовой странице в браузере необходимо перейти по адресу:
    localhost:8093/app/index.html
2. http://localhost:8093/app/test - тестовый сервлет TestPage
3. http://localhost:8093/app/api/admin - доступ к REST API (Jersey)
    3.1 Данный ресурс защищен BASIC аутентификацией


##Всякие вопросы
1. Можно ли совместить programmatic mode с настройкой через стандартные файлы конфигов, например, tomcat-users.xml?
    Томкат использует временные папки tomcat.<port> для хранения конфигов и возможно еще других файлов


##Напоминалки
_Default Servlet_:
 - Static content может быть предоставлен при помощи DefaultServlet, например welcome files, которые являются частью Servlet API
 - Может предоставить листинг директорий
    