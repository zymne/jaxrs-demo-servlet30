package ibs.sandbox;

import org.apache.catalina.*;
import org.apache.catalina.authenticator.BasicAuthenticator;
import org.apache.catalina.authenticator.FormAuthenticator;
import org.apache.catalina.realm.JNDIRealm;
import org.apache.catalina.realm.MemoryRealm;
import org.apache.catalina.realm.UserDatabaseRealm;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.*;
import org.glassfish.jersey.server.ResourceConfig;

import javax.naming.NamingException;
import java.io.File;


public class MainApp extends ResourceConfig {

    private Tomcat tomcat;
    private static final String docBase = "src/main/webapp/";
    private static final String userDatabaseConfigPath = docBase + "config/tomcat-users.xml";

    public MainApp() {
        packages("ibs.sandbox.rest");
    }

    public static class UserDatabaseListener implements LifecycleListener {

        Object container = null;

        @Override
        public void lifecycleEvent(LifecycleEvent event) {

            if (Lifecycle.CONFIGURE_STOP_EVENT.equals(event.getType())) {

                container = event.getLifecycle();

                if(container instanceof Server) {
                    Server server = (Server)container;
                    javax.naming.Context ctx = server.getGlobalNamingContext();
                    try {
                        Object userDb = ctx.lookup("jndiUserDatabase");
                        ctx.rebind("jndiUserDatabase", userDb);

                    } catch (NamingException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    /**Делаем отдельный контекст для rest сервисов - то есть отдельное приложение**/
    private void createRestServiceApp() {
        Context restServiceContext = tomcat.addContext("/rest", new File(docBase).getAbsolutePath());
        restServiceContext.getPipeline().addValve(new FormAuthenticator());

        JNDIRealm realm = new JNDIRealm();
        realm.setConnectionURL("ldap://esk-dc3.eispd.dkz.cloud:389");
        //realm.setUserBase("dc=cbr,dc=ru");
        //realm.setUserSearch("(samAccountName={0})");
        realm.setUserPattern("CN={0},OU=Accounts,OU=TechUsersZPE,OU=ZPE,OU=eispd,DC=portal,DC=cbr,DC=ru");
        realm.setUserRoleName("memberOf");
        realm.setRoleBase("DC=cbr,DC=ru");
        //дополнительная настройка для поиска ролей
        realm.setRoleName("cn");
        realm.setRoleSearch("(uniqueMember={0})");

        //loginConf - для данного контекста пропишем механизмы аутентификации
        LoginConfig loginConfig = new LoginConfig();
        loginConfig.setAuthMethod("FORM");
        loginConfig.setLoginPage("/login.html");
        loginConfig.setErrorPage("/error.html");
        loginConfig.setRealmName("ldap");
        restServiceContext.setLoginConfig(loginConfig);
        //TODO: нужен ли? или засетить имя реалма достаточно?
        restServiceContext.setRealm(realm);

        //web-resource-collection
        SecurityCollection secCollection = new SecurityCollection();
        secCollection.addPatternDecoded("/api/admin/*");

        //authConstraint
        SecurityConstraint secConstraint = new SecurityConstraint();
        secConstraint.addCollection(secCollection);
        secConstraint.addAuthRole("siteAdmin");
        secConstraint.addAuthRole("eisl-IODViewers");
        restServiceContext.addConstraint(secConstraint);

        //Сервлет для Jersey (dispatcher)
        Wrapper servlet = restServiceContext.createWrapper();
        servlet.setName("jerseyContainer");
        servlet.setServletClass("org.glassfish.jersey.servlet.ServletContainer");
        servlet.addInitParameter("javax.ws.rs.Application", "ibs.sandbox.MainApp");
        servlet.setLoadOnStartup(1);

        //Default Servlet for serving static content
        Wrapper defaultServlet = restServiceContext.createWrapper();
        defaultServlet.setName("default");
        defaultServlet.setServletClass("org.apache.catalina.servlets.DefaultServlet");
        defaultServlet.addInitParameter("debug", "0");
        defaultServlet.addInitParameter("listings", "false");
        defaultServlet.setLoadOnStartup(1);


        restServiceContext.addChild(servlet);
        restServiceContext.addServletMappingDecoded("/api/*", "jerseyContainer");

        restServiceContext.addChild(defaultServlet);
        restServiceContext.addServletMappingDecoded("*.html", "default");

    }

    private Tomcat setup() {
        tomcat = new Tomcat();
        tomcat.setPort(8093);
        tomcat.enableNaming();

        Server server = tomcat.getServer();

        server.addLifecycleListener(new UserDatabaseListener());
        //tomcat.getServer().getGlobalNamingContext().bind("jndiUserDatabase", new File(userDatabaseConfigPath));

//        ContextResource res = new ContextResource();
//        res.setName("jndiUserDatabase");
//        res.setType(org.apache.catalina.UserDatabase.class.getName());
//        res.setAuth("Container");
//
//
//        server.getGlobalNamingResources().addResource(res);

        //StandardHost host = new StandardHost();

        createRestServiceApp();

        UserDatabaseRealm realm = new UserDatabaseRealm();
        realm.setResourceName("jndiUserDatabase");
        realm.setRealmPath("/config/tomcat-users.xml");

        MemoryRealm memoryRealm = new MemoryRealm();
        memoryRealm.setDomain("memory2");
        memoryRealm.setPathname("/config/tomcat-users.xml");

        //tomcat.getHost().setRealm(memoryRealm);

        Context appContext = tomcat.addContext("/app", new File(docBase).getAbsolutePath());
        appContext.getPipeline().addValve(new BasicAuthenticator());

        //loginConf
        LoginConfig loginConfig = new LoginConfig();
        loginConfig.setAuthMethod("BASIC");
        loginConfig.setRealmName("memory2");
        appContext.setLoginConfig(loginConfig);

        Wrapper defaultServlet = appContext.createWrapper();
        defaultServlet.setName("default");
        defaultServlet.setServletClass("org.apache.catalina.servlets.DefaultServlet");
        defaultServlet.addInitParameter("debug", "0");
        defaultServlet.addInitParameter("listings", "false");
        defaultServlet.setLoadOnStartup(1);

        Wrapper testServlet = appContext.createWrapper();
        testServlet.setName("test-servlet");
        testServlet.setServletClass("ibs.sandbox.servlet.TestPage");

        appContext.addChild(testServlet);
        appContext.addServletMappingDecoded("/test/*", "test-servlet");

        appContext.addChild(defaultServlet);
        appContext.addServletMappingDecoded("*.html", "default");

        return tomcat;
    }

    public static void main(String[] args) {

        Tomcat tomcat = new MainApp().setup();
        try {
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }

}
