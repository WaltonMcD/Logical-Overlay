package cs455.overlay.protocols;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.ArrayList;

import javax.print.attribute.standard.MediaSize.Other;
import javax.security.auth.callback.TextInputCallback;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.ConnDirectiveFormat;
import cs455.overlay.wireformats.DoneMessageFormat;
import cs455.overlay.wireformats.PayloadMessageFormat;
import cs455.overlay.wireformats.RegResponseFormat;
import cs455.overlay.wireformats.RegisterMessageFormat;
import cs455.overlay.wireformats.TaskCompleteFormat;
import cs455.overlay.wireformats.TaskInitiateFormat;
import cs455.overlay.wireformats.TrafficSumRequestFormat;

public class Message {
	private Integer messageType;
	private String  ipAddress;
	private Integer port;
	private Integer statusCode;
	private String  additionalInfo;
	private Integer identifier;
	private Integer frontNodePort;
	private String  frontNodeIp;
	private Integer backNodePort;
	private String  backNodeIp;
	private long payload;
	private Integer startNodeId;
	private Integer messagesToSend;
	private Integer numMessagesSent;
	private Integer numMessagesReceived;
	private long sumOfSentMessages;
	private long sumOfReceivedMessages;
	private int hops;
	private int messageNumber;

	// Default constructor for when you're receiving a message and don't know the
	// message type yet
	public Message() {
	}

	// Registration Request : Type = 0 / Deregistration Request : Type = 1
	public Message(Integer messageType, String ipAddress, Integer port) {
		this.messageType = messageType;
		this.ipAddress = ipAddress;
		this.port = port;
	}

	// Registration Response : Type = 2
	public Message(Integer messageType, Integer statusCode, Integer identifier, String additionalInfo) {
		this.messageType = messageType;
		this.statusCode = statusCode;
		this.identifier = identifier;
		this.additionalInfo = additionalInfo;
	}

	// Connection Directive : Type = 3
	public Message(Integer messageType, Integer frontNodePort, String frontNodeIp, Integer backNodePort, String backNodeIp) {
		this.messageType = messageType;
		this.frontNodePort = frontNodePort;
		this.frontNodeIp = frontNodeIp;
		this.backNodeIp = backNodeIp;
		this.backNodePort = backNodePort;
	}

	// Task Initiate : Type = 4
	public Message(Integer messageType, Integer messagesToSend) {
		this.messageType = messageType;
		this.messagesToSend = messagesToSend;
	}

	// Data Traffic : Type = 5
	public Message(int messageType, int hops, int numberOfMessage, long payload, int fromPort, String fromHost, int toPort, String toHost) {
		this.messageType = messageType;
		this.frontNodeIp = fromHost;
		this.frontNodePort = fromPort;
		this.backNodeIp = toHost;
		this.backNodePort = toPort;
		this.payload = payload;
		this.messageNumber = numberOfMessage;
		this.hops = hops;
	}

	// Task Complete : Type = 6
	public Message(Integer messageType, Integer identifier, String ipAddress, Integer port) {
		this.messageType = messageType;
		this.identifier = identifier;
		this.ipAddress = ipAddress;
		this.port = port;
	}

	// Pull Traffic Summary : Type = 7
	public Message(Integer messageType, String hostname) {
		this.messageType = messageType;
		this.ipAddress = hostname;
	}

	// Traffic Summary : Type = 8
	public Message(Integer messageType, String ipAddress, Integer port, Integer numMessagesSent,
			long sumOfSentMessages, Integer numMessagesReceived, long sumOfReceivedMessages) {
		this.messageType = messageType;
		this.ipAddress = ipAddress;
		this.port = port;
		this.numMessagesSent = numMessagesSent;
		this.numMessagesReceived = numMessagesReceived;
		this.sumOfSentMessages = sumOfSentMessages;
		this.sumOfReceivedMessages = sumOfReceivedMessages;
	}

	// Connection Directive helper, needed identifier for array storage : Type = 9
	public Message(String frontNodeIp, Integer messageType, Integer identifier, Integer frontNodePort, String backNodeIp, int backNodePort) {
		this.messageType = messageType;
		this.identifier = identifier;
		this.frontNodePort = frontNodePort;
		this.frontNodeIp = frontNodeIp;
		this.backNodeIp = backNodeIp;
		this.backNodePort = backNodePort;
	}

	public String packMessage(DataOutputStream outputStream) {
		try {
			
			switch (this.messageType) {

			case 0:
				RegisterMessageFormat registrationRequest = new RegisterMessageFormat(this.ipAddress, port);
				byte[] marshalledRegMsg = registrationRequest.getBytes();
				outputStream.writeInt(registrationRequest.type);
				outputStream.writeInt(marshalledRegMsg.length);
				outputStream.write(marshalledRegMsg);
				break;

			case 1:
				DoneMessageFormat dereg = new DoneMessageFormat(this.ipAddress, this.port);
				byte[] marshalledDereg = dereg.getBytes();
				outputStream.writeInt(dereg.type);
				outputStream.writeInt(marshalledDereg.length);
				outputStream.write(marshalledDereg);
				break;

			case 2:
				RegResponseFormat regResponse = new RegResponseFormat(this.statusCode, this.identifier, this.additionalInfo);
				byte[] marshalledRegRes = regResponse.getBytes();
				outputStream.writeInt(regResponse.type);
				outputStream.writeInt(marshalledRegRes.length);
				outputStream.write(marshalledRegRes);
				break;

			case 3:
				ConnDirectiveFormat connDir = new ConnDirectiveFormat(this.frontNodeIp, this.frontNodePort, this.backNodeIp, this.backNodePort);
				byte[] marshalledConnDirMsg = connDir.getBytes();
				outputStream.writeInt(connDir.type);
				outputStream.writeInt(marshalledConnDirMsg.length);
				outputStream.write(marshalledConnDirMsg);
				break;

			case 4:
				TaskInitiateFormat taskInitiate = new TaskInitiateFormat(this.messagesToSend);
				byte[] marshalledTask = taskInitiate.getBytes();
				outputStream.writeInt(taskInitiate.type);
				outputStream.writeInt(marshalledTask.length);
				outputStream.write(marshalledTask);
				break;

			case 5:
				PayloadMessageFormat newPayload = new PayloadMessageFormat(this.hops, this.messageNumber, this.payload, this.frontNodePort, this.frontNodeIp, this.backNodePort, this.backNodeIp);
				byte[] marshalledPayload = newPayload.getBytes();
				outputStream.writeInt(newPayload.type);
				outputStream.writeInt(marshalledPayload.length);
				outputStream.write(marshalledPayload);
				break;

			case 6:
				TaskCompleteFormat taskComplete = new TaskCompleteFormat(this.identifier, this.ipAddress, this.port);
				byte[] marshTask = taskComplete.getBytes();
				outputStream.writeInt(taskComplete.type);
				outputStream.writeInt(marshTask.length);
				outputStream.write(marshTask);
				break;

			case 7:
				TrafficSumRequestFormat trafficSum = new TrafficSumRequestFormat(this.ipAddress);
				byte[] marshalledTraffic = trafficSum.getBytes();
				outputStream.writeInt(trafficSum.type);
				outputStream.writeInt(marshalledTraffic.length);
				outputStream.write(marshalledTraffic);
				break;

			case 8:
				outputStream.writeInt(this.messageType);
				outputStream.writeUTF(this.ipAddress);
				outputStream.writeInt(this.port);
				outputStream.writeInt(this.numMessagesSent);
				outputStream.writeInt(this.numMessagesReceived);
				outputStream.writeLong(this.sumOfSentMessages);
				outputStream.writeLong(this.sumOfReceivedMessages);
				break;

			case 9:
				outputStream.writeInt(this.messageType);
				outputStream.writeInt(this.identifier);
				outputStream.writeInt(this.frontNodePort);
				outputStream.writeUTF(this.frontNodeIp);
				outputStream.writeInt(this.backNodePort);
				outputStream.writeUTF(this.backNodeIp);
				break;

			}

			outputStream.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void unpackMessage(DataInputStream inputStream) {
		
		try {
			int messageSize;
			this.messageType = inputStream.readInt();

			switch (this.messageType) {
			case 0:
				messageSize = inputStream.readInt();
				byte[] regRequest = new byte[messageSize];
				inputStream.readFully(regRequest);

				RegisterMessageFormat registrationRequest = new RegisterMessageFormat(regRequest);
				this.ipAddress = registrationRequest.hostName;
				this.port = registrationRequest.portNumber;
				registrationRequest.printContents();
				break;

			case 1:
				messageSize = inputStream.readInt();
				byte[] dereg = new byte[messageSize];
				inputStream.readFully(dereg);

				DoneMessageFormat deregistration = new DoneMessageFormat(dereg);
				this.ipAddress = deregistration.hostname;
				this.port = deregistration.port;
				break;

			case 2:
				messageSize = inputStream.readInt();
				byte[] regResponse = new byte[messageSize];
				inputStream.readFully(regResponse);

				RegResponseFormat registerRes = new RegResponseFormat(regResponse);
				this.statusCode = registerRes.statusCode;
				this.identifier = registerRes.identifier;
				this.additionalInfo = registerRes.additionalInfo;
				registerRes.printContents();
				break;

			case 3:
				messageSize = inputStream.readInt();
				byte[] connDir = new byte[messageSize];
				inputStream.readFully(connDir);
				
				ConnDirectiveFormat connDirective = new ConnDirectiveFormat(connDir);
				this.frontNodePort = connDirective.portNumber;
				this.frontNodeIp = connDirective.hostName;
				this.backNodeIp = connDirective.toHost;
				this.backNodePort = connDirective.toPort;
				connDirective.printContents();
				break;

			case 4:
				messageSize = inputStream.readInt();
				byte[] taskInitiate = new byte[messageSize];
				inputStream.readFully(taskInitiate);

				TaskInitiateFormat task = new TaskInitiateFormat(taskInitiate);
				this.messagesToSend = task.messagesToSend;
				task.printContents();
				break;

			case 5:
				messageSize = inputStream.readInt();
				byte[] payloadMsg = new byte[messageSize];
				inputStream.readFully(payloadMsg, 0, messageSize);

				PayloadMessageFormat payloadMsgFormat = new PayloadMessageFormat(payloadMsg);
				this.frontNodeIp = payloadMsgFormat.fromHostname;
				this.frontNodePort = payloadMsgFormat.fromPort;
				this.backNodeIp = payloadMsgFormat.toHostname;
				this.backNodePort = payloadMsgFormat.toPort;
				this.payload = payloadMsgFormat.payload;
				this.messageNumber = payloadMsgFormat.numberOfMessage;
				this.hops = payloadMsgFormat.hops;
				payloadMsgFormat.printContents();
				break;

			case 6:
				messageSize = inputStream.readInt();
				byte[] taskComplete = new byte[messageSize];
				inputStream.readFully(taskComplete);

				TaskCompleteFormat taskComp = new TaskCompleteFormat(taskComplete);
				this.identifier = taskComp.identifier;
				this.ipAddress = taskComp.ip;
				this.port = taskComp.port;
				taskComp.printContents();
				break;

			case 7:
				messageSize = inputStream.readInt();
				byte[] trafficReq = new byte[messageSize];
				inputStream.readFully(trafficReq);

				TrafficSumRequestFormat traffic = new TrafficSumRequestFormat(trafficReq);
				this.ipAddress = traffic.hostname;
				traffic.printContents();
				break;
				
			case 8:
				this.ipAddress = inputStream.readUTF();
				this.port = inputStream.readInt();
				this.numMessagesSent = inputStream.readInt();
				this.numMessagesReceived = inputStream.readInt();
				this.sumOfSentMessages = inputStream.readInt();
				this.sumOfReceivedMessages = inputStream.readInt();
				break;

			case 9:
				this.identifier = inputStream.readInt();
				this.frontNodePort = inputStream.readInt();
				this.frontNodeIp = inputStream.readUTF();
				this.backNodePort = inputStream.readInt();
				this.backNodeIp = inputStream.readUTF();
				break;

			}
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}

	public  String getType() {
		switch (this.messageType) {
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
		case 9:
			return "Connection_Directive_Helper";
		}
		return null;
	}

	public  Integer getMessageType() {
		return messageType;
	}

	public  void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}

	public  String getIpAddress() {
		return ipAddress;
	}

	public  void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public  Integer getPort() {
		return port;
	}

	public  void setPort(Integer port) {
		this.port = port;
	}

	public  Integer getStatusCode() {
		return statusCode;
	}

	public  void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public  String getAdditionalInfo() {
		return additionalInfo;
	}

	public  void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public  Integer getIdentifier() {
		return identifier;
	}

	public  void setIdentifier(Integer identifier) {
		this.identifier = identifier;
	}

	public  Integer getFrontNodePort() {
		return frontNodePort;
	}

	public  void setFrontNodePort(Integer frontNodePort) {
		this.frontNodePort = frontNodePort;
	}

	public  String getFrontNodeIp() {
		return frontNodeIp;
	}

	public  void setFrontNodeIp(String frontNodeIp) {
		this.frontNodeIp = frontNodeIp;
	}

	public  Integer getBackNodePort() {
		return backNodePort;
	}

	public  void setBackNodePort(Integer backNodePort) {
		this.backNodePort = backNodePort;
	}

	public  String getBackNodeIp() {
		return backNodeIp;
	}

	public  void setBackNodeIp(String backNodeIp) {
		this.backNodeIp = backNodeIp;
	}

	public  long getPayload() {
		return payload;
	}

	public  void setPayload(Integer payload) {
		this.payload = payload;
	}

	public  Integer getStartNodeId() {
		return startNodeId;
	}

	public  void setStartNodeId(Integer startNodeId) {
		this.startNodeId = startNodeId;
	}

	public  Integer getMessagesToSend() {
		return messagesToSend;
	}

	public  void setMessagesToSend(Integer messagesToSend) {
		this.messagesToSend = messagesToSend;
	}

	public  Integer getNumMessagesSent() {
		return numMessagesSent;
	}

	public  void setNumMessagesSent(Integer numMessagesSent) {
		this.numMessagesSent = numMessagesSent;
	}

	public  Integer getNumMessagesReceived() {
		return numMessagesReceived;
	}

	public  void setNumMessagesReceived(Integer numMessagesReceived) {
		this.numMessagesReceived = numMessagesReceived;
	}

	public  long getSumOfSentMessages() {
		return sumOfSentMessages;
	}

	public  void setSumOfSentMessages(Integer sumOfSentMessages) {
		this.sumOfSentMessages = sumOfSentMessages;
	}

	public  long getSumOfReceivedMessages() {
		return sumOfReceivedMessages;
	}

	public  void setSumOfReceivedMessages(Integer sumOfReceivedMessages) {
		this.sumOfReceivedMessages = sumOfReceivedMessages;
	}
	
	

}
