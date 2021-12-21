package test;

import outmail.controller.API;
import outmail.mail.MailParser;
import outmail.mail.ReceiveMail;
import outmail.model.Mail;
import outmail.mysql.MySQL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/17 0:28
 */
public class Test {
    private static final String URL = "jdbc:mysql://110.42.156.179:3306/foxmail?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "foxmail";
    private static final String PASSWORD = "foxmail";
    private static final MySQL mySQL = new MySQL(URL, USERNAME, PASSWORD);
    public static void main(String[] args) {
        System.out.println(Test.class.getClassLoader().getResource("home/add.png").getPath());
    }

    public static void a() {
        long startTime = System.currentTimeMillis();
        API.init();
        ReceiveMail receiveMail = new ReceiveMail(API.CONFIGS.get(0));
        ArrayList<Mail> list = MailParser.parse(receiveMail.fetchInbox());
        ArrayList<Mail> newMail = new ArrayList<>();
        HashSet<String> inbox = new HashSet<>();
        for(Mail m: API.queryInbox(API.CONFIGS.get(0))) {
            inbox.add(m.getMsgId());
        }
        for(Mail m: list) {
            if(!inbox.contains(m.getMsgId())) {
                newMail.add(m);
            }
//                mySQL.addMessage(API.CONFIGS.get(0), m);
        }
        for(Mail m: newMail) {
            System.out.println(m.getSubject());
        }

        long endTime = System.currentTimeMillis();
        System.out.println(((endTime - startTime) / 1000.0) + "s");
    }

    public static void abc(String[] args) {
        String pattern = "^[a-zA-Z0-9._%+-]+@(?!.*\\.\\..*)[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$";


        /** Regex format for multiple EmailValidator */
        String MULTIPLE_EMAIL_REGEX_FORMAT = "(?:(?:%1$s)(?:(?:\\s*,\\s*)|(?:\\s*;\\s*)|\\s*$))*";

    /** Regex for single EmailValidator */
        String SINGLE_EMAIL_REGEX = "(?:(?:[A-Za-z0-9\\-_@!#$%&'*+/=?^`{|}~]|(?:\\\\[\\x00-\\xFF]?)|(?:\"[\\x00-\\xFF]*\"))+(?:\\.(?:(?:[A-Za-z0-9\\-_@!#$%&'*+/=?^`{|}~])|(?:\\\\[\\x00-\\xFF]?)|(?:\"[\\x00-\\xFF]*\"))+)*)@(?:(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+(?:(?:[A-Za-z0-9]*[A-Za-z][A-Za-z0-9]*)(?:[A-Za-z0-9-]*[A-Za-z0-9])?))";
        Pattern SINGLE_EMAIL_REGEX_PATTERN = Pattern.compile(SINGLE_EMAIL_REGEX);

    /** Regex for multiple EmailValidator */
        String MULTIPLE_EMAIL_REGEX = String.format(MULTIPLE_EMAIL_REGEX_FORMAT, SINGLE_EMAIL_REGEX);
        Pattern MULTIPLE_EMAIL_REGEX_PATTERN = Pattern.compile(MULTIPLE_EMAIL_REGEX);

        Pattern r = Pattern.compile(pattern);
        String str = "=?utf-8?B?5pyL5Y+L=?= <ackerven@qq.com>";
        str = "<ackerven@qq.com> < 64126092@qq.com>";
        str = "ackerven@qq.com";
        Matcher m = SINGLE_EMAIL_REGEX_PATTERN.matcher(str);
        int count = 0;
        while (m.find()) {
            count++;
            System.out.println("Match number " + count);
            System.out.println("start(): " + m.start());
            System.out.println("end(): " + m.end());
            System.out.println(str.substring(m.start(),m.end()));
        }

    }
}
