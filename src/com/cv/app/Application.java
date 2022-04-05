/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app;

import org.apache.log4j.Logger;
import org.springframework.richclient.application.ApplicationLauncher;

/**
 *
 * @author WSwe
 */
public class Application {

    static Logger log = Logger.getLogger(Application.class.getName());

    /**
     * Main routine for the BestApplication application.
     *
     * @param args
     */
    public static void main(String[] args) {
        log.info("SimpleApp starting up");

        // In order to launch the platform, we have to construct an
        // application context that defines the beans (services) and
        // wiring. This is pretty much straight Spring.
        //
        // Part of this configuration will indicate the initial page to be
        // displayed.

        String rootContextDirectoryClassPath = "/ctx";

        // The startup context defines elements that should be available
        // quickly such as a splash screen image.

        String startupContextPath = rootContextDirectoryClassPath + "/richclient-startup-context.xml";

        String richclientApplicationContextPath = rootContextDirectoryClassPath
                + "/richclient-application-context.xml";

        // The ApplicationLauncher is responsible for loading the contexts,
        // presenting the splash screen, initializing the Application
        // singleton instance, creating the application window to display
        // the initial page.
        try {
            new ApplicationLauncher(startupContextPath, 
                    new String[] { richclientApplicationContextPath });
        } catch (RuntimeException e) {
            log.error("RuntimeException during startup", e);
        }
    }
}
