package ibs.sandbox;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;
import org.glassfish.jersey.server.ResourceConfig;

import javax.servlet.ServletException;
import javax.ws.rs.ApplicationPath;
import java.io.File;
import java.io.InputStream;


public class MainApp extends ResourceConfig {

    public MainApp() {
        packages("ibs.sandbox.rest");
    }

    public static void main(String[] args) throws ServletException {

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8092);

        String docBase = "src/main/webapp/";

        //tomcat.addWebapp("/", new File(docBase).getAbsolutePath());

        Context rootContext = tomcat.addContext("/", new File(docBase).getAbsolutePath());

        Wrapper servlet = rootContext.createWrapper();
        servlet.setName("ibs.sandbox.MainApp");
        servlet.setServletClass("org.glassfish.jersey.servlet.ServletContainer");
        servlet.addInitParameter("javax.ws.rs.Application", "ibs.sandbox.MainApp");
        servlet.setLoadOnStartup(1);

        Wrapper defaultServlet = rootContext.createWrapper();
        defaultServlet.setName("default");
        defaultServlet.setServletClass("org.apache.catalina.servlets.DefaultServlet");
        defaultServlet.addInitParameter("debug", "0");
        defaultServlet.addInitParameter("listings", "false");
        defaultServlet.setLoadOnStartup(1);

        ClassLoader cl = MainApp.class.getClassLoader();

        WebResourceRoot webResource = new StandardRoot(rootContext);
        webResource.createWebResourceSet(WebResourceRoot.ResourceSetType.POST, "/static", cl.getResource("ibs/sandbox/index.html"), "/");
        rootContext.setResources(webResource);

        rootContext.addChild(defaultServlet);
        rootContext.addServletMappingDecoded("*.html", "default");

        rootContext.addChild(servlet);
        rootContext.addServletMappingDecoded("/rest/*", "ibs.sandbox.MainApp");
        //rootContext.addWelcomeFile("index.html");


        try {

            tomcat.start();
            tomcat.getServer().await();

        } catch (LifecycleException e) {
            e.printStackTrace();
        }


    }

}
