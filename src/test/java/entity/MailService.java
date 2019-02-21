package entity;

public class MailService {
    int port;
    String protocol;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @PostConstruct
    public void post(){
        System.out.println("Post construct call");
    }
}
