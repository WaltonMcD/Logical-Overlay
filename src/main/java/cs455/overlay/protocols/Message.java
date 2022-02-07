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
    public String backNodeIp;
    public Integer payload;
    public Integer startNodeId;
    public Integer messagesToSend;
    public Integer numMessagesSent;
    public Integer numMessagesReceived;
    public Integer sumOfSentMessages;
    public Integer sumOfReceivedMessages;

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
    public Message(Integer messageType, Integer frontNodePort, String frontNodeIp, Integer backNodePort, String backNodeIp){
        this.messageType = messageType;
        this.frontNodePort = frontNodePort;
        this.frontNodeIp = frontNodeIp;
        this.backNodeIp = backNodeIp;
        this.backNodePort = backNodePort;
    }

    //Task Initiate : Type = 4
    public Message(Integer messageType, Integer messagesToSend){
        this.messageType = messageType;
        this.messagesToSend = messagesToSend;
    }
    
    //Data Traffic : Type = 5
    public Message(Integer messageType, Integer startNodeId, Integer payload){
        this.messageType = messageType;
        this.startNodeId = startNodeId;
        this.payload = payload;
    }

    //Task Complete : Type = 6
    public Message(Integer messageType, Integer identifier, String ipAddress, Integer port){
        this.messageType = messageType;
        this.identifier = identifier;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    //Pull Traffic Summary : Type = 7
    public Message(Integer messageType){
        this.messageType = messageType;

    }

    //Traffic Summary : Type = 8
    public Message(Integer messageType, String ipAddress, Integer port, Integer numMessagesSent, 
        Integer numMessagesReceived, Integer sumOfSentMessages, Integer sumOfReceivedMessages){
        this.messageType = messageType;
        this.ipAddress = ipAddress;
        this.port = port;
        this.numMessagesSent = numMessagesSent;
        this.numMessagesReceived = numMessagesReceived;
        this.sumOfSentMessages = sumOfSentMessages;
        this.sumOfReceivedMessages = sumOfReceivedMessages;
    }

    //Connection Directive helper, needed identifier for array storage : Type = 9
    public Message(Integer messageType, Integer identifier, Integer frontNodePort, String frontNodeIp, Integer backNodePort, String backNodeIp){
        this.messageType = messageType;
        this.identifier = identifier;
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
            case 4:
                return "Task_Initiate";
            case 5:
                return "Data_Traffic";
            case 6:
                return "Task_Complete";
            case 7: 
                return "Pull_Traffic_Summary";
            case 8:
                return "Traffic_Summary";
        }
        return null; 
    }

}
