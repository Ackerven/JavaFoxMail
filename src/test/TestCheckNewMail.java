package test;

import outmail.controller.API;
import outmail.mail.CheckNewMail;
import outmail.model.Config;
import outmail.model.Mail;

import java.util.ArrayList;
import java.util.Timer;

/**
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/17 13:19
 */
public class TestCheckNewMail {
    public static void main(String[] args) {
        signConfig();
        Config config = API.CONFIGS.get(0);
        boolean b = true;
        while(b) {
            if(config.hasNewMail()) {
                b = false;
                for(Mail m: config.getNewMail()) {
                    System.out.println(m.getSubject());
                }
            }
        }
        System.out.println("Have");
        while(true) {

        }
    }

    public static void signConfig() {
        API.init();
        Timer timer = new Timer(true);
        timer.schedule(new CheckNewMail(API.CONFIGS.get(0)), 0L, 10000L);
    }

    public static void multiConfig() {
        API.init();
        ArrayList<Timer> list = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            list.add(new Timer());
            list.get(i).schedule(new CheckNewMail(API.CONFIGS.get(0)), 0L, 30000L);
        }
    }
}
