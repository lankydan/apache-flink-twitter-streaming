package dev.lankydan.flink.kafka.consumer;

import java.util.List;

public class Includes {

    private List<User> users;

    public Includes() {
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Includes{" +
            "users=" + users +
            '}';
    }
}
