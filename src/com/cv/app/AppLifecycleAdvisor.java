/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.MachineProperty;
import com.cv.app.util.Util1;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.config.ApplicationWindowConfigurer;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;

/**
 *
 * @author WSwe
 */
public class AppLifecycleAdvisor extends DefaultApplicationLifecycleAdvisor {

    static Logger log = Logger.getLogger(AppLifecycleAdvisor.class.getName());

    /**
     * This method is called prior to the opening of an application window. Note
     * at this point the window control has not been created. This hook allows
     * programmatic control over the configuration of the window (by setting
     * properties on the configurer) and it provides a hook where code that
     * needs to be executed prior to the window opening can be plugged in (like
     * a startup wizard, for example).
     *
     * @param configurer The application window configurer
     */
    @Override
    public void onPreWindowOpen(ApplicationWindowConfigurer configurer) {

        // If you override this method, it is critical to allow the superclass
        // implementation to run as well.
        super.onPreWindowOpen(configurer);

        // Uncomment to hide the menubar, toolbar, or alter window size...
        // configurer.setShowMenuBar(false);
        // configurer.setShowToolBar(false);
        // configurer.setInitialSize(new Dimension(640, 480));
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        screen.height = screen.height - 50;
        screen.width = screen.width - 50;
        configurer.setInitialSize(screen);
    }

    /**
     * Called just after the command context has been internalized. At this
     * point, all the commands for the window have been created and are
     * available for use. If you need to force the execution of a command prior
     * to the display of an application window (like a login command), this is
     * where you'd do it.
     *
     * @param window The window who's commands have just been created
     */
    @Override
    public void onCommandsCreated(ApplicationWindow window) {
        log.info("onCommandsCreated( windowNumber=" + window.getNumber() + " )");

        //Menu managment
        /*AbstractDataAccess dao = new BestDataAccess();
         String table = "com.best.app.pharmacy.database.entity.Menu";
         String strSQL = "SELECT * FROM " + table;
         List<Menu> listMenu = dao.findAll("Menu");
         List<Menu> listApplication = JoSQLUtil.getResult(strSQL + " WHERE parent = 0",
         listMenu);
        
         for(Menu appl : listApplication){
         boolean status = Util1.hashPrivilege(appl.getMenuClass());
         CommandGroup cmdg = window.getMenuBar();
         cmdg.find(appl.getMenuClass()).setAuthorized(status);
            
         if(status){
         List<Menu> listMenu1 = JoSQLUtil.getResult(strSQL + " where parent = "
         + appl.getMenuId() + " and menuType.typeId = 2", 
         listMenu);
                
         for(Menu menu : listMenu1){
         boolean menuStatus = Util1.hashPrivilege(menu.getMenuClass());
         System.out.println(menu.getMenuClass());
         cmdg.find(menu.getMenuClass()).setAuthorized(menuStatus);
         }
         }
         }*/
    }

    /**
     * Called after the actual window control has been created.
     *
     * @param window The window being processed
     */
    @Override
    public void onWindowCreated(ApplicationWindow window) {
        log.info("onWindowCreated( windowNumber=" + window.getNumber() + " )");
    }

    /**
     * Called immediately after making the window visible.
     *
     * @param window The window being processed
     */
    @Override
    public void onWindowOpened(ApplicationWindow window) {
        //window.getControl().setExtendedState(Frame.MAXIMIZED_BOTH);
        log.info("onWindowOpened( windowNumber=" + window.getNumber() + " )");

        org.springframework.richclient.application.Application.instance()
                .getActiveWindow().getStatusBar()
                .setMessage(Global.loginUser.getUserName() + " (" + Global.sessionName + ")");
    }

    /**
     * Called when the window is being closed. This hook allows control over
     * whether the window is allowed to close. By returning false from this
     * method, the window will not be closed.
     *
     * @return boolean indicator if window should be closed. <code>true</code>
     * to allow the close, <code>false</code> to prevent the close.
     */
    @Override
    public boolean onPreWindowClose(ApplicationWindow window) {
        log.info("onPreWindowClose( windowNumber=" + window.getNumber() + " )");
        return true;
    }

    /**
     * Called when the application has fully started. This is after the initial
     * application window has been made visible.
     */
    @Override
    public void onPostStartup() {
        this.getOpeningWindow().getControl().setExtendedState(JFrame.MAXIMIZED_BOTH);
        log.info("onPostStartup()");
    }

    @Override
    public void onPreStartup() {
        LoginDialog login = new LoginDialog(new JFrame(), true);
        //WesleyLoginDialog login = new WesleyLoginDialog(new JFrame(), true);
        //Calculate dialog position to centre.
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        int x = (screen.width - login.getWidth()) / 2;
        int y = (screen.height - login.getHeight()) / 2;

        login.setLocation(x, y);
        login.show();
        log.info("After Login Dialog");

        if (login.isStatus()) {
            AbstractDataAccess dao = Global.dao;

            Global.machineName = Util1.getComputerName();
            System.out.println("Machine Name : " + Global.machineName);

            try {
                dao.open();
                Global.machineId = dao.getMax("machine_id", "machine_info", "machine_name = '"
                        + Global.machineName + "'").toString();
                dao.close();
            } catch (Exception ex) {
                /*String machineName = Util1.getComputerName();
                String ipAddress = Util1.getIPAddress();
                MachineInfo machine = new MachineInfo();

                machine.setIpAddress(ipAddress);
                machine.setMachineName(machineName);

                try {
                    dao.save(machine);
                    Global.machineId = dao.getMax("machine_id", "machine_info", "machine_name = '"
                            + Global.machineName + "'").toString();
                    dao.close();

                } catch (Exception exSave) {
                    log.error("onPreStartup : " + ex.getStackTrace()[0].getLineNumber() + " - " + exSave.toString());
                }*/
                JOptionPane.showMessageDialog(new JFrame(), "Your machie is not registered.",
                        "Invalid Machine Name", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }

            //Machine property
            try {
                List<MachineProperty> listMP = dao.findAllHSQL("select o from MachineProperty o where o.key.machineId = "
                        + Global.machineId);
                for (MachineProperty mp : listMP) {
                    Global.systemProperties.put(mp.getKey().getPropDesp(), mp.getPropValue());
                }
                initFont();
            } catch (Exception ex) {
                log.error("Machine property : " + ex.getMessage());
            } finally {
                dao.close();
            }
        } else {
            System.exit(1);
        }
    }

    private void initFont() {
        String strFontSize = Util1.getPropValue("system.font.size");
        int fontSize = 12;
        if (!strFontSize.isEmpty() && !strFontSize.equals("-")) {
            try {
                fontSize = Integer.parseInt(strFontSize);
            } catch (NumberFormatException ex) {
                log.error("Font Size Error : " + ex.getMessage());
            }
        }
        Global.lableFont = new java.awt.Font("Zawgyi-One", 1, fontSize);
        Global.textFont = new java.awt.Font("Zawgyi-One", 0, fontSize);
        Global.menuFont = new java.awt.Font("Zawgyi-One", 0, fontSize + 3);
        Global.rowHeight = fontSize + 15;
    }
}
