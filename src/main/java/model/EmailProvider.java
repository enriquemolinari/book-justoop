package model;

public interface EmailProvider {

	void send(String to, String subject, String body);

	static EmailProvider defaultProvider() {
		return (to, subject, body) -> {
		};
	}
}
