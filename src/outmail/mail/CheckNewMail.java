package outmail.mail;

import outmail.controller.API;
import outmail.model.Config;
import outmail.model.Mail;

import javax.mail.Message;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/**
 * 后台线程获取是否有新的邮件到达
 *
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/12 21:51
 */
public class CheckNewMail extends Thread {
    private final long sleepTime;

    public CheckNewMail(long sleepTime) {
        this.sleepTime = sleepTime;
        this.setDaemon(true);
    }

    /**
     * The action to be performed by this timer task.
     */
    @Override
    public void run() {
        while(true) {
            System.out.println(Thread.currentThread().getName() + " Start: " + new Date());
            for(Config config : API.CONFIGS) {
                System.out.println("[INFO] " + Thread.currentThread().getName()  + " Fetch " + config.getConfigName());
                ReceiveMail receiveMail = new ReceiveMail(config);
                MailParser mailParser = new MailParser();
                Message[] msgList = receiveMail.fetchInbox();
                ArrayList<Mail> list = mailParser.parse(msgList);
                HashSet<String> inbox = getSet(config);
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
                receiveMail.closeStore();
            }
            System.out.println(Thread.currentThread().getName() + " END: " + new Date());
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private HashSet<String> getSet(Config config) {
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
