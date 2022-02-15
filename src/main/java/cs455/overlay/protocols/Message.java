package cs455.overlay.protocols;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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
	private Integer payload;
	private Integer startNodeId;
	private Integer messagesToSend;
	private Integer numMessagesSent;
	private Integer numMessagesReceived;
	private Integer sumOfSentMessages;
	private Integer sumOfReceivedMessages;

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
	public Message(Integer messageType, Integer frontNodePort, String frontNodeIp, Integer backNodePort,
			String backNodeIp) {
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
	public Message(Integer messageType, Integer startNodeId, Integer payload) {
		this.messageType = messageType;
		this.startNodeId = startNodeId;
		this.payload = payload;
	}

	// Task Complete : Type = 6
	public Message(Integer messageType, Integer identifier, String ipAddress, Integer port) {
		this.messageType = messageType;
		this.identifier = identifier;
		this.ipAddress = ipAddress;
		this.port = port;
	}

	// Pull Traffic Summary : Type = 7
	public Message(Integer messageType) {
		this.messageType = messageType;

	}

	// Traffic Summary : Type = 8
	public Message(Integer messageType, String ipAddress, Integer port, Integer numMessagesSent,
			Integer sumOfSentMessages, Integer numMessagesReceived, Integer sumOfReceivedMessages) {
		this.messageType = messageType;
		this.ipAddress = ipAddress;
		this.port = port;
		this.numMessagesSent = numMessagesSent;
		this.numMessagesReceived = numMessagesReceived;
		this.sumOfSentMessages = sumOfSentMessages;
		this.sumOfReceivedMessages = sumOfReceivedMessages;
	}

	// Connection Directive helper, needed identifier for array storage : Type = 9
	public Message(Integer messageType, Integer identifier, Integer frontNodePort, String frontNodeIp,
			Integer backNodePort, String backNodeIp) {
		this.messageType = messageType;
		this.identifier = identifier;
		this.frontNodePort = frontNodePort;
		this.frontNodeIp = frontNodeIp;
		this.backNodePort = backNodePort;
		this.backNodeIp = backNodeIp;
	}

	public synchronized String packMessage(DataOutputStream outputStream) {
		try {
			outputStream.writeInt(this.messageType);

			switch (this.messageType) {

			case 0:
				outputStream.writeUTF(this.ipAddress);
				outputStream.writeInt(this.port);
				break;

			case 1:
				outputStream.writeUTF(this.ipAddress);
				outputStream.writeInt(this.port);
				break;

			case 2:
				outputStream.writeInt(this.statusCode);
				outputStream.writeInt(this.identifier);
				outputStream.writeUTF(this.additionalInfo);
				break;

			case 3:
				outputStream.writeInt(this.frontNodePort);
				outputStream.writeUTF(this.frontNodeIp);
				outputStream.writeInt(this.backNodePort);
				outputStream.writeUTF(this.backNodeIp);
				break;

			case 4:
				outputStream.writeInt(this.messageType);
				outputStream.writeInt(this.messagesToSend);
				break;

			case 5:
				outputStream.writeInt(this.startNodeId);
				outputStream.writeInt(this.payload);
				break;

			case 6:
				outputStream.writeInt(this.identifier);
				outputStream.writeUTF(this.ipAddress);
				outputStream.writeInt(this.port);
				break;

			case 7:
				outputStream.writeInt(this.messageType);
				break;

			case 8:
				outputStream.writeUTF(this.ipAddress);
				outputStream.writeInt(this.port);
				outputStream.writeInt(this.numMessagesSent);
				outputStream.writeInt(this.numMessagesReceived);
				outputStream.writeInt(this.sumOfSentMessages);
				outputStream.writeInt(this.sumOfReceivedMessages);
				break;

			case 9:
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

	public synchronized ArrayList<Object> unpackMessage(DataInputStream inputStream) {
		ArrayList<Object> messageContents = new ArrayList<Object>();
		
		try {
			this.messageType = inputStream.readInt();
			messageContents.add(messageType);

			switch (this.messageType) {
			case 0:
				this.ipAddress = inputStream.readUTF();
				this.port = inputStream.readInt();

				messageContents.add(ipAddress);
				messageContents.add(port);
				break;

			case 1:
				this.ipAddress = inputStream.readUTF();
				this.port = inputStream.readInt();

				messageContents.add(ipAddress);
				messageContents.add(port);
				break;

			case 2:
				this.statusCode = inputStream.readInt();
				this.identifier = inputStream.readInt();
				this.additionalInfo = inputStream.readUTF();

				messageContents.add(statusCode);
				messageContents.add(identifier);
				messageContents.add(additionalInfo);
				break;

			case 3:
				this.frontNodePort = inputStream.readInt();
				this.frontNodeIp = inputStream.readUTF();
				this.backNodePort = inputStream.readInt();
				this.backNodeIp = inputStream.readUTF();

				messageContents.add(frontNodePort);
				messageContents.add(frontNodeIp);
				messageContents.add(backNodePort);
				messageContents.add(backNodeIp);
				break;

			case 4:
				this.messageType = inputStream.readInt();
				this.messagesToSend = inputStream.readInt();

				messageContents.add(messageType);
				messageContents.add(messagesToSend);
				break;

			case 5:
				this.startNodeId = inputStream.readInt();
				this.payload = inputStream.readInt();

				messageContents.add(startNodeId);
				messageContents.add(payload);
				break;

			case 6:
				this.identifier = inputStream.readInt();
				this.ipAddress = inputStream.readUTF();
				this.port = inputStream.readInt();

				messageContents.add(identifier);
				messageContents.add(ipAddress);
				messageContents.add(port);
				break;

			case 7:
				this.messageType = inputStream.readInt();
				break;
				
			case 8:
				this.ipAddress = inputStream.readUTF();
				this.port = inputStream.readInt();
				this.numMessagesSent = inputStream.readInt();
				this.numMessagesReceived = inputStream.readInt();
				this.sumOfSentMessages = inputStream.readInt();
				this.sumOfReceivedMessages = inputStream.readInt();

				messageContents.add(ipAddress);
				messageContents.add(port);
				messageContents.add(numMessagesSent);
				messageContents.add(numMessagesReceived);
				messageContents.add(sumOfSentMessages);
				messageContents.add(sumOfReceivedMessages);
				break;

			case 9:
				this.identifier = inputStream.readInt();
				this.frontNodePort = inputStream.readInt();
				this.frontNodeIp = inputStream.readUTF();
				this.backNodePort = inputStream.readInt();
				this.backNodeIp = inputStream.readUTF();

				messageContents.add(identifier);
				messageContents.add(frontNodePort);
				messageContents.add(frontNodeIp);
				messageContents.add(backNodePort);
				messageContents.add(backNodeIp);
				break;

			}
		} catch (IOException e) {
			//e.printStackTrace();
		}
		return messageContents;
	}

	public synchronized String getType() {
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

	public synchronized Integer getMessageType() {
		return messageType;
	}

	public synchronized void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}

	public synchronized String getIpAddress() {
		return ipAddress;
	}

	public synchronized void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public synchronized Integer getPort() {
		return port;
	}

	public synchronized void setPort(Integer port) {
		this.port = port;
	}

	public synchronized Integer getStatusCode() {
		return statusCode;
	}

	public synchronized void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public synchronized String getAdditionalInfo() {
		return additionalInfo;
	}

	public synchronized void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public synchronized Integer getIdentifier() {
		return identifier;
	}

	public synchronized void setIdentifier(Integer identifier) {
		this.identifier = identifier;
	}

	public synchronized Integer getFrontNodePort() {
		return frontNodePort;
	}

	public synchronized void setFrontNodePort(Integer frontNodePort) {
		this.frontNodePort = frontNodePort;
	}

	public synchronized String getFrontNodeIp() {
		return frontNodeIp;
	}

	public synchronized void setFrontNodeIp(String frontNodeIp) {
		this.frontNodeIp = frontNodeIp;
	}

	public synchronized Integer getBackNodePort() {
		return backNodePort;
	}

	public synchronized void setBackNodePort(Integer backNodePort) {
		this.backNodePort = backNodePort;
	}

	public synchronized String getBackNodeIp() {
		return backNodeIp;
	}

	public synchronized void setBackNodeIp(String backNodeIp) {
		this.backNodeIp = backNodeIp;
	}

	public synchronized Integer getPayload() {
		return payload;
	}

	public synchronized void setPayload(Integer payload) {
		this.payload = payload;
	}

	public synchronized Integer getStartNodeId() {
		return startNodeId;
	}

	public synchronized void setStartNodeId(Integer startNodeId) {
		this.startNodeId = startNodeId;
	}

	public synchronized Integer getMessagesToSend() {
		return messagesToSend;
	}

	public synchronized void setMessagesToSend(Integer messagesToSend) {
		this.messagesToSend = messagesToSend;
	}

	public synchronized Integer getNumMessagesSent() {
		return numMessagesSent;
	}

	public synchronized void setNumMessagesSent(Integer numMessagesSent) {
		this.numMessagesSent = numMessagesSent;
	}

	public synchronized Integer getNumMessagesReceived() {
		return numMessagesReceived;
	}

	public synchronized void setNumMessagesReceived(Integer numMessagesReceived) {
		this.numMessagesReceived = numMessagesReceived;
	}

	public synchronized Integer getSumOfSentMessages() {
		return sumOfSentMessages;
	}

	public synchronized void setSumOfSentMessages(Integer sumOfSentMessages) {
		this.sumOfSentMessages = sumOfSentMessages;
	}

	public synchronized Integer getSumOfReceivedMessages() {
		return sumOfReceivedMessages;
	}

	public synchronized void setSumOfReceivedMessages(Integer sumOfReceivedMessages) {
		this.sumOfReceivedMessages = sumOfReceivedMessages;
	}
	
	

}
