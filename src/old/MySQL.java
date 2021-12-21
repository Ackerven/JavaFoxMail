package old;

import outmail.model.Config;
import outmail.model.Contact;
import outmail.model.Mail;

import java.sql.*;
import java.util.ArrayList;

/**
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/12 3:53
 * 封装与数据库有关的操作
 */
public final class MySQL {
    private final String url;
    private final String username;
    private final String password;

    public MySQL(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * 获取一个数据库连接（T）
     * @return 放回一个 Connection 对象
     * 注意：获取和close是要同对出现
     */
    private Connection connectMySQL() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            System.out.println("[ERROR] Class.forName() 出错！");
            System.out.println("[ERROR]: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            System.out.println("connectMySQL() 出错！");
            System.out.println("[ERROR]: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 获取表中自增字段的值（T）
     * @param table 表名
     * @return
     */
    public int getId(String table) {
        System.out.println("[INFO] Calling MySQL::getId()...");
        int id = -1;
        String sql = "select AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'foxmail' AND TABLE_NAME = ? limit 1";
        try (Connection connection = connectMySQL();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, table);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                id = resultSet.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::getId() throws Exception");
            e.printStackTrace();
            return -1;
        }
        return id;
    }

    /* BEGIN Config */

    /**
     * 增加配置（T）
     * @param config   增加配置记录到数据库
     * @return  添加成功返回true，失败返回false
     */
    public boolean addConfig(Config config) {
        System.out.println("[INFO] Calling MySQL::addConfig()...");
        String sql = "INSERT INTO config(config_name) VALUES(?)";
        try (
                Connection connection = connectMySQL();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, config.getConfigName());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::addConfig() throws Exception!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除配置（T）
     * @param id    需要删除配置的id
     * @return  删除成功返回true，失败返回false
     */
    public boolean delConfig(int id) {
        System.out.println("[INFO] Calling MySQL::delConfig()...");
        String sql = "DELETE FROM config WHERE id = ?";
        String sqls = "DELETE FROM contact WHERE cid = ?";
        try (
                Connection connection = connectMySQL();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                PreparedStatement preparedStatements = connection.prepareStatement(sqls);
        ) {
            preparedStatements.setInt(1, id);
            preparedStatement.setInt(1, id);
            preparedStatements.executeUpdate();
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::addConfig() throws Exception!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 查询一个配置文件信息（T）
     * @param id    配置记录ID
     * @return  找到返回Config对象，失败返回null
     */
    public Config queryOneConfig(int id) {
        System.out.println("[INFO] Calling MySQL::queryOneConfig()...");
        Config config = null;
        String sql = "SELECT * FROM config WHERE id = ?";
        try(
                Connection connection = connectMySQL();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                config = new Config(resultSet.getInt("id"), resultSet.getString("config_name"));
            }
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::queryOneConfig() throws Exception!");
            e.printStackTrace();
            return null;
        }
        return config;
    }

    /**
     * 查询全部配置信息（T）
     * @return  找到返回Config对象列表，失败返回null
     */
    public ArrayList<Config> queryAllConfig() {
        System.out.println("[INFO] Calling MySQL::queryAllConfig()...");
        ArrayList<Config> list = new ArrayList<>();
        String sql = "SELECT * FROM config ORDER BY id";
        try (
                Connection connection = connectMySQL();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
        ) {
            while(resultSet.next()) {
                list.add(new Config(resultSet.getInt("id"),resultSet.getString("config_name")));
            }
        } catch(Exception e) {
            System.out.println("[ERROR] MySQL::queryAllConfig() throws Exception!");
            e.printStackTrace();
            return null;
        }
        return list;
    }

    /* END Config */

    /* BEGIN ContactPane */

    /**
     * 添加联系人到数据库（T）
     * @param config    联系人的表中cid字段通过外键关联config表中的id，所以config的id就是contact的cid
     * @param contact   联系人
     * @return  如果不抛出异常，证明数据库插入是没有问题的，返回true，否则返回false
     * 联系人表的 id 字段是自增的，所以写sql语句可以不用填id字段
     */
    public boolean addContact(Config config, Contact contact) {
        System.out.println("[INFO] Calling MySQL::addContact()...");
        String sql = "INSERT INTO contact(cid, name, email, description) VALUES (?, ?, ?, ?)";
        contact.setId(getId("contact"));
        try (Connection connection = connectMySQL();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ) {
            preparedStatement.setInt(1, config.getId());
            preparedStatement.setString(2, contact.getName());
            preparedStatement.setString(3, contact.getEmail());
            preparedStatement.setString(4, contact.getDescription());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::addContact() throws Exception! ");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 从数据库中删除联系人（T）
     * @param id  联系人的ID
     * @return  如果不抛出异常，证明数据库修改是没有问题的，返回true，否则返回false
     */
    public boolean delContact(int id) {
        System.out.println("[INFO] Calling MySQL::delContact()...");
        String sql = "DELETE FROM contact WHERE id = ?";
        try (
                Connection connection = connectMySQL();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch(Exception e) {
            System.out.println("[ERROR] MySQL::delContact() throws Exception!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据关键字key在字段field中查找联系人（T）
     * @param config 联系人的表中cid字段通过外键关联config表中的id，所以config的id就是contact的cid，查找联系人时加上筛选cid为config的id的人
     * @param key   查找的关键词
     * @param field 查找到字段，如果为null，则不区分字段，即搜索全部字段
     * @param fuzzy 值为true时是开启模糊匹配，为false为精准匹配
     * @return  返回找到的联系人，如果出错或找不到，返回null
     */
    public ArrayList<Contact> queryContact(Config config, String key, String field, boolean fuzzy) {
        System.out.println("[INFO] Calling MySQL::queryAllContact()...");
        ArrayList<Contact> list = new ArrayList<>();
        String sqlName = "SELECT * FROM contact WHERE name like ? AND cid = ?";
        String sqlEmail = "SELECT * FROM contact WHERE email like ? AND cid = ?";
        String sqlDescription = "SELECT * FROM contact WHERE description like ? and cid = ?";
        if(fuzzy) {
            key = "%"+key+"%";
        }
        try (
                Connection connection = connectMySQL();
                PreparedStatement name = connection.prepareStatement(sqlName);
                PreparedStatement email = connection.prepareStatement(sqlEmail);
                PreparedStatement des = connection.prepareStatement(sqlDescription);
                ) {
            name.setString(1, key);
            name.setInt(2, config.getId());
            email.setString(1, key);
            email.setInt(2, config.getId());
            des.setString(1, key);
            des.setInt(2, config.getId());
            if(field == null) {
                ResultSet resultSet = name.executeQuery();
                handler(resultSet, list);
                resultSet = email.executeQuery();
                handler(resultSet, list);
                resultSet = des.executeQuery();
                handler(resultSet, list);
            } else {
                ResultSet resultSet = null;
                switch (field) {
                    case "name":
                        resultSet = name.executeQuery();
                        break;
                    case "email":
                        resultSet = email.executeQuery();
                        break;
                    case "description":
                        resultSet = des.executeQuery();
                        break;
                    default:
                        return null;
                }
                handler(resultSet, list);
            }
        } catch(Exception e) {
            System.out.println("[ERROR] MySQL::queryContact() throws Exception");
            e.printStackTrace();
            return null;
        }
        return list;
    }

    /**
     * 处理返回的数据集
     * @param resultSet
     * @param list
     * @throws Exception
     */
    private void handler(ResultSet resultSet, ArrayList<Contact> list) throws Exception{
        while (resultSet.next()) {
            boolean flag = false;
            for(Contact c: list) {
                if(c.getId() == resultSet.getInt("id")) {
                    flag = true;
                    break;
                }
            }
            if(flag) {
                continue;
            }
            Blob blob = resultSet.getBlob("file");
            blob.getBinaryStream();
            list.add(new Contact(resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("email"),
                    resultSet.getString("description")));
        }
    }

    /**
     * 查询所有的联系人（T）
     * @param config 联系人的表中cid字段通过外键关联config表中的id，所以config的id就是contact的cid，查找联系人时加上筛选cid为config的id的人
     * @return  返回一个Contact的列表，如果出错，返回null
     */
    public ArrayList<Contact> queryAllContact(Config config) {
        System.out.println("[INFO] Calling MySQL::queryAllContact()...");
        ArrayList<Contact> list = new ArrayList<>();
        String sql = "SELECT * FROM contact WHERE cid = ? ORDER BY id";
        try (
                Connection connection = connectMySQL();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ) {
            preparedStatement.setInt(1, config.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                list.add(new Contact(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("description")));
            }
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::queryAllContact() throws Exception!");
            e.printStackTrace();
            return null;
        }
        return list;
    }

    /**
     * 更新联系人（T）
     * @param contact 新的联系人，更新数据库的时候，查询条件为 where id = contact.getId()
     * @return  如果不抛出异常，证明数据库修改是没有问题的，返回true，否则返回false
     */
    public boolean updateContact(Contact contact) {
        System.out.println("[INFO] Calling MySQL::updateContact()...");
        String sql = "UPDATE contact SET name = ?, email = ?, description = ? WHERE id = ?";
        try (
                Connection connection = connectMySQL();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ) {
            preparedStatement.setString(1, contact.getName());
            preparedStatement.setString(2, contact.getEmail());
            preparedStatement.setString(3, contact.getDescription());
            preparedStatement.setInt(4, contact.getId());
            preparedStatement.executeUpdate();
        } catch(Exception e) {
            System.out.println("[ERROR] MySQL::queryAllContact() throws Exception!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* END BEGIN */

    /* BEGIN Mail */

    /**
     * 添加一封邮件到
     * @param config
     * @param mail
     * @return
     */
    public boolean addMessage(Config config, Mail mail) {
        //TODO
        //这个涉及到收邮件时的DataSource对象，所以我来写
        return false;
    }

    /**
     * 更新邮件的状态
     * @param mail  哪一封邮件
     * @return  如果没有异常抛出，返回true，否则返回false
     */
    public boolean updateStatus(Mail mail) {
        //TODO
        //更新邮件的状态
        return false;
    }

    /**
     * 更新邮件的收件人，抄送人，密送人列表
     * @param mail  属于哪一封邮件
     * @param table 修改哪个表   收件人(mail_receiver) 抄送(mail_cc) 密送(mail_bcc)
     * @return 如果没有异常抛出，返回true，否则返回false
     */
    public boolean updateTable(Mail mail, String table) {
        //TODO
        //更新邮件的接收者列表
        //这个更新是指先把原来这个邮件id关联的的记录（即cid = mail.getId())删掉，再把新的写进去
        return false;
    }

    /**
     * 更新草稿箱的邮件
     * @param mail  属于哪一封邮件
     * @return  如果没有异常抛出，返回true，否则返回false
     */
    public boolean updateMail(Mail mail) {
        //TODO
        //写更新主表的逻辑，附表调用上面的函数updateTable
        return false;
    }

    /**
     * 删除一封邮件
     * @param id  邮件的id
     * @return  如果没有异常抛出，返回true，否则返回false
     */
    public boolean delMail(int id) {
        //TODO
        //有外键关联,所以先删除子表(CC,BCC,receiver,attachment)的数据,再删主表的数据
        return false;
    }

    /**
     * 删除某个配置关联的所有邮件
     * @param config
     * @return
     */
    public boolean delAllMail(Config config) {
        //TODO
        //删除Config关联的id
        //先查出哪些邮件ID是与config.getId()关联的，（即cid = config.getId())
        //然后再调用上面的delMail，传这些id进去
        return false;
    }

    /**
     * 查询某个配置关联的所有邮件
     * @param config
     * @return
     */
    public ArrayList<Mail> queryAllMail(Config config) {
        //TODO
        //查询主表,子表,构建Mail对象列表
        //Mail只提供无参构造方法,字段调用Setter来设置
        //这样的话先查询主表,处理主表的resultSet,在查询副表,处理副表的resultSet
        return null;
    }
    /* END Mail */

}
