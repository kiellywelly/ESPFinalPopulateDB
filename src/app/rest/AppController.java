package app.rest;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

@Component
public class AppController {
	public final String ROOT = "http://localhost:9999/";
	
	protected Response errorResponse(List<String> errors) {
		Message m = new Message();
		m.setErrors(errors);
		return Response.status(422).entity(m).build();
	}
	
	protected String formatDate(Calendar c) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
	}
	
	public static class Message {
		private List<String> errors;
		public Message() {
		}
		public List<String> getErrors() {
			return errors;
		}
		public void setErrors(List<String> errors) {
			this.errors = errors;
		}
	}
}
