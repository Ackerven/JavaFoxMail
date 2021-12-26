package outmail.mail;

import outmail.model.Config;

import javax.mail.*;
import java.util.Properties;

/**
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/12 3:03
 * 接收邮件的封装
 */
public class ReceiveMail {
    private Config config;
    private Store store;
    public ReceiveMail(Config config) {
        this.config = config;
    }


    public Message[] fetchInbox() {
        return fetchInbox(config.getProperties(), config.getAuthenticator(), null);
    }

    public Message[] fetchInbox(Properties props, Authenticator authenticator, String protocol) {
        Message[] messages = null;
        Session session = Session.getDefaultInstance(props, authenticator);
        Folder folder = null;
        try {
            store = protocol == null || protocol.trim().length() == 0 ? session.getStore() : session.getStore(protocol);
            System.out.println(config.getUserName() + " : " + config.getPassWord());
            store.connect(config.getUserName(), config.getPassWord());
            System.out.println(store);
            folder = store.getFolder("INBOX"); 
            folder.open(Folder.READ_ONLY);
            messages = folder.getMessages();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public void closeStore() {
        try {
            this.store.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "ReceiveMail{" +
                "config=" + config +
                '}';
    }
}
