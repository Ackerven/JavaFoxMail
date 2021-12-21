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
    private final ReceiveMail receiveMail;

    public CheckNewMail(Config config) {
        this.config = config;
        this.receiveMail = new ReceiveMail(config);
    }

    /**
     * The action to be performed by this timer task.
     */
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " Start: " + new Date());
        ArrayList<Mail> list = MailParser.parse(receiveMail.fetchInbox());
        HashSet<String> inbox = getSet();
        for (Mail m : list) {
            if (!inbox.contains(m.getMsgId())) {
                System.out.println("[INFO] " + config.getConfigName() + " New Mail: " + m.getSubject());
                config.addMailToNewMail(m);
                API.addMailToMySQL(config, m);
            }
        }
        System.out.println(Thread.currentThread().getName() + " END: " + new Date());
    }

    private HashSet<String> getSet() {
        HashSet<String> inbox = new HashSet<>();
        ArrayList<Mail> list = API.queryAllMail(config);
        if (list != null) {
            for (Mail m : list) {
                inbox.add(m.getMsgId());
            }
        }
        return inbox;
    }
}
