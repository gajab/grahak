package org.community.grahak.channel;

public class JMSConfig {

	private String targetDestination;
	private String replyDestination;
	private String destinationType;

	public String getTargetDestination() {
		return targetDestination;
	}

	public void setTargetDestination(String targetDestination) {
		this.targetDestination = targetDestination;
	}

	public String getReplyDestination() {
		return replyDestination;
	}

	public void setReplyDestination(String replyDestination) {
		this.replyDestination = replyDestination;
	}

	public String getDestinationType() {
		return destinationType;
	}

	public void setDestinationType(String destinationType) {
		this.destinationType = destinationType;
	}

}
