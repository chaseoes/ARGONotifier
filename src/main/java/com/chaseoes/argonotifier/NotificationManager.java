package com.chaseoes.argonotifier;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.net.URI;

import com.chaseoes.argonotifier.generalutilities.GeneralUtilities;
import com.notification.Notification;
import com.notification.NotificationFactory;
import com.notification.NotificationFactory.Location;
import com.notification.NotificationListener;
import com.notification.manager.QueueManager;
import com.notification.manager.QueueManager.ScrollDirection;
import com.notification.types.WindowNotification;
import com.platform.Platform;
import com.theme.TextTheme;
import com.theme.ThemePackage;
import com.theme.WindowTheme;
import com.utils.IconUtils;
import com.utils.Time;

public class NotificationManager {

    public static NotificationFactory factory = null;
    public static QueueManager manager;

    private static void setup() {
        Platform.instance().setAdjustForPlatform(true);
        factory = new NotificationFactory(cleanDark());
        manager = new QueueManager(Location.SOUTHEAST);
        manager.setScrollDirection(ScrollDirection.NORTH);
    }

    public static void show(String title, String description, final String onClickURL, String icon) {
        if (factory == null) {
            setup();
        }

        WindowNotification note;
        note = factory.buildIconNotification(title, description, IconUtils.createIcon("/" + icon, 40, 40));

        note.addNotificationListener(new NotificationListener() {
            public void actionCompleted(Notification notification, String action) { // shown, clicked, hidden
                if (action.equalsIgnoreCase("clicked") || (action.equalsIgnoreCase("shown") && Main.autoOpenNewItems)) {
                    try {
                        Desktop.getDesktop().browse(new URI(onClickURL));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        note.setCloseOnClick(true);
        manager.addNotification(note, Time.seconds(30));
        GeneralUtilities.play("/ding.wav");
    }

    private static ThemePackage cleanDark() {
        ThemePackage pack = new ThemePackage();

        WindowTheme window = new WindowTheme();
        window.background = new Color(32, 32, 32);
        window.foreground = new Color(127, 127, 127);
        window.opacity = 1f;
        window.width = 350;
        window.height = 60;

        TextTheme text = new TextTheme();
        text.title = new Font("Arial", Font.BOLD, 15);
        text.subtitle = new Font("Arial", Font.PLAIN, 12);
        text.titleColor = new Color(80, 153, 183);
        text.subtitleColor = new Color(127, 127, 127);

        pack.setTheme(WindowTheme.class, window);
        pack.setTheme(TextTheme.class, text);

        return pack;
    }

}
