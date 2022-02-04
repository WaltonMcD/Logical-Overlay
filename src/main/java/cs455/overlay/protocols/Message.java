package cs455.overlay.protocols;

public class Message {
    public Integer messageType;
    public String ipAddress;
    public Integer port;
    public Integer statusCode;
    public String additionalInfo;
    public Integer identifier;
    public Integer frontNodePort;
    public String frontNodeIp;
    public Integer backNodePort;
    public Integer backNodeIp;

    //Registration Request / Deregistration Request
    public Message(Integer messageType, String ipAddress, Integer port){
        this.messageType = messageType;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    //Registration Response
    public Message(Integer messageType, Integer statusCode, Integer identifier, String additionalInfo){
        this.messageType = messageType;
        this.statusCode = statusCode;
        this.identifier = identifier;
        this.additionalInfo = additionalInfo;
    }

    //Connection Directive
    public Message(Integer messageType, Integer frontNodePort, String frontNodeIp, Integer backNodePort, Integer backNodeIp){
        this.messageType = messageType;
        this.frontNodePort = frontNodePort;
        this.frontNodeIp = frontNodeIp;
        this.backNodeIp = backNodeIp;
        this.backNodePort = backNodePort;
    }

}
