package com.chaseoes.argonotifier;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.LoggingMode;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Message;
import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.InboxPaginator;
import net.dean.jraw.paginators.ModeratorPaginator;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.SubredditPaginator;

public class RedditAPI {

    private static RedditClient redditClient = null;
    private static Credentials credentials = Credentials.script("username", "someshithere", "moreshithere", "probablymoreshithere");

    private static Submission lastSubmission;
    private static PublicContribution lastModQueueItem;
    private static Message lastModMailItem;

    private static void setup(boolean force) {
        if (force) {
            redditClient = null;
        }

        try {
            redditClient = new RedditClient(UserAgent.of("desktop", "com.chaseoes.argonotifier", "v1.0", "chaseoes"));
            redditClient.authenticate(redditClient.getOAuthHelper().easyAuth(credentials));
            // redditClient.setLoggingMode(LoggingMode.ALWAYS);
            redditClient.setLoggingMode(LoggingMode.NEVER);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Submission getLastSubmission() {
        return lastSubmission;
    }

    public static PublicContribution getLastModQueueItem() {
        return lastModQueueItem;
    }

    public static Message getLastModMailItem() {
        return lastModMailItem;
    }

    public static void refresh() {
        try {
            if (redditClient == null) {
                setup(false);
            }

            if (Main.nagPostsEnabled) {
                SubredditPaginator subredditPaginator = new SubredditPaginator(redditClient);
                subredditPaginator.setSubreddit("globaloffensive");
                subredditPaginator.setSorting(Sorting.NEW);
                subredditPaginator.setLimit(1);
                lastSubmission = subredditPaginator.next().get(0);
            }

            if (Main.nagModQueueEnabled) {
                ModeratorPaginator moderatorPaginator = new ModeratorPaginator(redditClient, "globaloffensive", "modqueue");
                moderatorPaginator.setLimit(1);
                Listing<PublicContribution> listing = moderatorPaginator.next();
                if (!listing.isEmpty()) {
                    lastModQueueItem = listing.get(0);
                }
            }

            if (Main.nagNewModMailEnabled) {
                InboxPaginator inboxPaginator = new InboxPaginator(redditClient, "moderator");
                inboxPaginator.setLimit(1);
                Listing<Message> listing = inboxPaginator.next();
                if (!listing.isEmpty()) {
                    lastModMailItem = listing.get(0);
                }
            }
        } catch (Exception e) {
            setup(true);
            refresh();
        }
    }

}
