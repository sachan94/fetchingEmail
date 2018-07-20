package com.subham.email;

import java.util.*;
import java.io.*;

import javax.mail.*;
import javax.mail.event.*;
import javax.activation.*;

import com.sun.mail.imap.IMAPFolder;

/* Monitors given mailbox for new mail */

public class EmailClient {

    public static void main(String argv[]) {
    if (argv.length != 5) {
        System.out.println(
        "Usage: monitor <host> <user> <password> <mbox> <freq>");
        System.exit(1);
    }
    System.out.println("\nTesting monitor\n");

    try {
        Properties props = System.getProperties();

        // Get a Session object
        Session session = Session.getInstance(props, null);
        // session.setDebug(true);

        // Get a Store object
        Store store = session.getStore("imap");

        // Connect
//        store.connect(argv[0], argv[1], argv[2]);
        store.connect("smtp.gmail.com", "techcupola@gmail.com", "cupola@123");
        

        // Open a Folder
        Folder folder = store.getFolder("INBOX");
        if (folder == null || !folder.exists()) {
        System.out.println("Invalid folder");
        System.exit(1);
        }

        folder.open(Folder.READ_WRITE);

        // Add messageCountListener to listen for new messages
        folder.addMessageCountListener(new MessageCountAdapter() {
        public void messagesAdded(MessageCountEvent ev) {
            Message[] msgs = ev.getMessages();
            System.out.println("Got " + msgs.length + " new messages");

            // Just dump out the new messages
            for (int i = 0; i < msgs.length; i++) {
            try {
                System.out.println("-----");
                System.out.println("Message " +
                msgs[i].getMessageNumber() + ":");
                msgs[i].writeTo(System.out);
            } catch (IOException ioex) { 
                ioex.printStackTrace(); 
            } catch (MessagingException mex) {
                mex.printStackTrace();
            }
            }
        }
        });

        // Check mail once in "freq" MILLIseconds
//        int freq = Integer.parseInt(5000);
        boolean supportsIdle = false;
        try {
        if (folder instanceof IMAPFolder) {
            IMAPFolder f = (IMAPFolder)folder;
            f.idle();
            supportsIdle = true;
        }
        } catch (FolderClosedException fex) {
        throw fex;
        } catch (MessagingException mex) {
        supportsIdle = false;
        }
        for (;;) {
        if (supportsIdle && folder instanceof IMAPFolder) {
            IMAPFolder f = (IMAPFolder)folder;
            f.idle();
            System.out.println("IDLE done");
        } else {
            Thread.sleep(5000); // sleep for freq milliseconds

            // This is to force the IMAP server to send us
            // EXISTS notifications. 
            folder.getMessageCount();
        }
        }

    } catch (Exception ex) {
        ex.printStackTrace();
    }
    }
}