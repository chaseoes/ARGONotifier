package com.chaseoes.argonotifier;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Desktop;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URI;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.chaseoes.argonotifier.generalutilities.GeneralUtilities;

public class Main {

    public static TrayIcon icon;
    public static final String version = "1.0";
    public static final int refreshTime = 3;

    public static boolean nagPostsEnabled = true;
    public static boolean nagModQueueEnabled = true;
    public static boolean nagNewModMailEnabled = true;

    public static boolean autoOpenNewItems = false;

    public static void main(String[] args) {
        RedditAPI.refresh();
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.put("swing.boldMetal", Boolean.FALSE);

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    createAndShowGUI();
                    // NotificationManager.show("Welcome!", "/r/GlobalOffensive notifier has successfully started.", "https://www.reddit.com/r/GlobalOffensive", "reddit.png");
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void createAndShowGUI() {
        if (!SystemTray.isSupported()) {
            return;
        }

        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(GeneralUtilities.createImage("/tray.png", "tray icon"));
        final SystemTray tray = SystemTray.getSystemTray();

        MenuItem aboutItem = new MenuItem("About");
        CheckboxMenuItem autoOpen = new CheckboxMenuItem("Auto Open New Items");
        CheckboxMenuItem newPostsToggle = new CheckboxMenuItem("Check New Posts");
        CheckboxMenuItem modQueueToggle = new CheckboxMenuItem("Check Mod Queue");
        CheckboxMenuItem modMailToggle = new CheckboxMenuItem("Check Mod Mail");
        MenuItem exitItem = new MenuItem("Exit");

        newPostsToggle.setState(true);
        modQueueToggle.setState(true);
        modMailToggle.setState(true);

        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(autoOpen);
        popup.addSeparator();
        popup.add(newPostsToggle);
        popup.add(modQueueToggle);
        popup.add(modMailToggle);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);
        trayIcon.setToolTip("Loading...");

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "/r/GlobalOffensive notifier by chaseoes. Version " + version + ".");
            }
        });

        autoOpen.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                autoOpenNewItems = (e.getStateChange() == ItemEvent.SELECTED);
            }
        });

        newPostsToggle.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                nagModQueueEnabled = (e.getStateChange() == ItemEvent.SELECTED);
            }
        });

        modQueueToggle.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                nagPostsEnabled = (e.getStateChange() == ItemEvent.SELECTED);
            }
        });

        modMailToggle.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                nagNewModMailEnabled = (e.getStateChange() == ItemEvent.SELECTED);
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                System.exit(0);
            }
        });

        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://www.reddit.com/r/GlobalOffensive/new/"));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        icon = trayIcon;
        new Nagger(refreshTime);
    }

}
