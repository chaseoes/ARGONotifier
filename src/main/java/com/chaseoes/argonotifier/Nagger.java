package com.chaseoes.argonotifier;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Message;
import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.models.Submission;

public class Nagger {

    private static HashSet<String> naggedItems = new HashSet<String>();

    private Timer timer;
    private int seconds;

    public Nagger(int seconds) {
        this.seconds = seconds;
        timer = new Timer();
        timer.schedule(new NagTask(), seconds * 1000);
    }

    class NagTask extends TimerTask {
        public void run() {
            RedditAPI.refresh();

            if (Main.nagPostsEnabled) {
                Submission submission = RedditAPI.getLastSubmission();
                if (submission != null && !naggedItems.contains(submission.getId())) {
                    naggedItems.add(submission.getId());
                    NotificationManager.show(submission.getTitle().replace("&amp", "&"), submission.getSelftext().isEmpty() ? submission.getUrl() : submission.getSelftext(), "https://reddit.com" + RedditAPI.getLastSubmission().getPermalink(), "reddit.png");
                    Main.icon.setToolTip("Last Post: " + submission.getTitle().replace("&amp", "&"));
                }
            }

            if (Main.nagModQueueEnabled) {
                PublicContribution contribution = RedditAPI.getLastModQueueItem();
                if (contribution != null && !naggedItems.contains(contribution.getId())) {
                    naggedItems.add(contribution.getId());

                    String textToShow = "";
                    if (contribution instanceof Submission) {
                        Submission submission = (Submission) contribution;
                        textToShow = submission.getTitle().replace("&amp", "&");
                    } else {
                        Comment comment = (Comment) contribution;
                        textToShow = comment.getBody();
                    }

                    NotificationManager.show("New Mod Queue Item!", textToShow, "https://www.reddit.com/r/mod/about/modqueue", "reddit-red.png");

                }
            }

            if (Main.nagNewModMailEnabled) {
                Message message = RedditAPI.getLastModMailItem();
                if (message != null && !naggedItems.contains(message.getId())) {
                    NotificationManager.show("New Mod Mail!", message.getSubject(), "https://www.reddit.com/message/moderator/", "reddit-orange.png");
                    naggedItems.add(message.getId());
                }
            }

            timer.schedule(new NagTask(), seconds * 1000);
        }
    }

}
