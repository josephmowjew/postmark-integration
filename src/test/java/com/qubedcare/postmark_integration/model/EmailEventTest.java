package com.qubedcare.postmark_integration.model;

   import org.junit.jupiter.api.Test;
   import java.util.Date;
   import static org.junit.jupiter.api.Assertions.*;

   public class EmailEventTest {

       @Test
       public void testEmailEventCreation() {
           Date now = new Date();
           EmailEvent event = new EmailEvent(EmailEventType.SENT, now, "Email sent to john@example.com");
           assertEquals(EmailEventType.SENT, event.getEventType());
           assertEquals(now, event.getTimestamp());
           assertEquals("Email sent to john@example.com", event.getEmailDetails());
       }
   }