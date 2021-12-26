package outmail.mail;

import outmail.controller.API;
import outmail.model.Config;
import outmail.model.Mail;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.TimerTask;

/**
 * 后台线程获取是否有新的邮件到达
 *
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/12 21:51
 */
public class CheckNewMail extends TimerTask {
    private final Config config;

    public CheckNewMail(Config config) {
        this.config = config;
    }

    /**
     * The action to be performed by this timer task.
     */
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " " + config.getConfigName() + " Start: " + new Date());
//        System.out.println(config);
        ReceiveMail receiveMail = new ReceiveMail(config);
        MailParser mailParser = new MailParser();
        ArrayList<Mail> list = mailParser.parse(receiveMail.fetchInbox());
        System.out.println("Fetch: " + config.getConfigName());
        System.out.println(list);
        HashSet<String> inbox = getSet();
        boolean isNew = false;
        for (Mail m : list) {
            if (!inbox.contains(m.getMsgId())) {
                System.out.println("[INFO] " + config.getConfigName() + " New Mail: " + m.getSubject());
                isNew = true;
                config.addMailToNewMail(m);
            }
        }
        if(isNew)
            API.addMailToMySQL(config);
        System.out.println(Thread.currentThread().getName() + " " + config.getConfigName() + " END: " + new Date());
        list.clear();
    }

    private HashSet<String> getSet() {
        HashSet<String> inbox = new HashSet<>();
        ArrayList<Mail> list = API.queryAllMail(config);
//        System.out.println(config.getConfigName() + " DB: ");
//        System.out.println(list);
        if (list != null) {
            for (Mail m : list) {
                inbox.add(m.getMsgId());
            }
        }
        return inbox;
    }
}
