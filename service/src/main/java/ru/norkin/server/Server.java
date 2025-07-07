package ru.norkin.server;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "db_server")
public class Server {

    @Id
    @Column(name = "server_id")
    private UUID id;

    @Column(name = "ip")
    private String ip;

    @Column(name = "portUI")
    private int portUI;

    @Column(name = "pathUI")
    private String pathUI;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    public Server(UUID id, String ip, int portUI, String pathUI, String username, String password) {
        this.id = id;
        this.ip = ip;
        this.portUI = portUI;
        this.pathUI = pathUI;
        this.username = username;
        this.password = password;
    }

    public Server() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPortUI() {
        return portUI;
    }

    public void setPortUI(int portUI) {
        this.portUI = portUI;
    }

    public String getPathUI() {
        return pathUI;
    }

    public void setPathUI(String pathUI) {
        this.pathUI = pathUI;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
