package outmail.utils;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/12 3:11
 * 通过用户名和密码构建鉴权类，用于邮箱smtp和pop认证
 */
public final class AuthenticatorGenerator {
    /**
     * 构建用于SMTP/POP服务器鉴权的类
     * @param userName  用户名
     * @param password  密码
     * @return  返回一个Authenticator对象
     */
    public static javax.mail.Authenticator getAuthenticator(final String userName, final String password) {
        System.out.println("[INFO] Calling AuthenticatorGenerator::getAuthenticator()...");
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
    }
}
