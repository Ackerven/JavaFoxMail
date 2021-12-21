package outmail.model;

/**
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/12 3:52
 * 联系人模型
 */
public class Contact {
    /**
     * 联系人ID
     */
    private int id;
    /**
     * 联系人姓名
     */
    private String name;
    /**
     * 联系人邮件
     */
    private String email;
    /**
     * 联系人描述
     */
    private String description;

    public Contact() { }

    /**
     * 构造Contact
     * @param name  联系人名称
     * @param email 联系人联系方式
     * @param description   联系人描述
     */
    public Contact(String name, String email, String description) {
        this.name = name;
        this.email = email;
        this.description = description;
    }

    /**
     * 构造Contact
     * @param id    联系人id
     * @param name  联系人名称
     * @param email 联系人联系方式
     * @param description   联系人描述
     */
    public Contact(int id, String name, String email, String description) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ContactPane{ " +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", description='" + description + '\'' +
                " }";
    }
}
