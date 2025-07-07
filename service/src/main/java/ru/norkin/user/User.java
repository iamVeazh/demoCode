package ru.norkin.user;

import ru.norkin.server.Server;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "db_user")
public class User {

    @Id
    @Column(name = "user_id")
    private UUID id;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @ManyToOne
    @JoinColumn(name="server_id", nullable=false)
    private Server server;


    public User(UUID id, String login, String password, String email, Server server) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.email = email;
        this.server = new Server();
    }

    public User() {
    }


    public UUID getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
