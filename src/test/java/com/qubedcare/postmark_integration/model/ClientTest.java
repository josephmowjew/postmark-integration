package com.qubedcare.postmark_integration.model;

   import org.junit.jupiter.api.Test;
   import static org.junit.jupiter.api.Assertions.*;

   public class ClientTest {

       @Test
       public void testClientCreation() {
           Client client = new Client("1", "John Doe", "john.doe@example.com");
           assertEquals("1", client.getId());
           assertEquals("John Doe", client.getName());
           assertEquals("john.doe@example.com", client.getEmailAddress());
       }

       @Test
       public void testClientEquality() {
           Client client1 = new Client("1", "John Doe", "john.doe@example.com");
           Client client2 = new Client("1", "John Doe", "john.doe@example.com");
           Client client3 = new Client("2", "Jane Doe", "jane.doe@example.com");

           assertEquals(client1, client2);
           assertNotEquals(client1, client3);
       }
   }