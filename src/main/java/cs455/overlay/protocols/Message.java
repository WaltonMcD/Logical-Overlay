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

    //Registration Request : Type = 0 / Deregistration Request : Type = 1
    public Message(Integer messageType, String ipAddress, Integer port){
        this.messageType = messageType;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    //Registration Response : Type = 2
    public Message(Integer messageType, Integer statusCode, Integer identifier, String additionalInfo){
        this.messageType = messageType;
        this.statusCode = statusCode;
        this.identifier = identifier;
        this.additionalInfo = additionalInfo;
    }

    //Connection Directive : Type = 3
    public Message(Integer messageType, Integer frontNodePort, String frontNodeIp, Integer backNodePort, Integer backNodeIp){
        this.messageType = messageType;
        this.frontNodePort = frontNodePort;
        this.frontNodeIp = frontNodeIp;
        this.backNodeIp = backNodeIp;
        this.backNodePort = backNodePort;
    }

    public String getType(){
        switch(this.messageType){
            case 0:
                return "Registration_Request";
            case 1:
                return "Deregistration_Request";
            case 2:
                return "Registration_Response";
            case 3:
                return "Connection_Directive";
        }
        return null; 
    }

}
