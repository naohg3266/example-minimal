package org.example.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.Test;

import io.ebean.DB;

/**
 * When running tests in the IDE install the "Enhancement plugin".
 * <p>
 * http://ebean-orm.github.io/docs/setup/enhancement#ide
 */
public class OrderTest {

  /**
   * This is expected case
   */
  @Test
  public void lazyloadToOne() {

    Customer newCustomer = new Customer("customer1");
    newCustomer.save();
    Order newOrder = new Order("order1");
    newOrder.setCustomer(newCustomer);
    newOrder.save();

    Order order1 = DB.find(Order.class).where().eq("name", "order1").findOne();

    assertEquals("customer1", order1.getCustomer().getName());
  }

  /**
   * This is unexpected case
   */
  @Test
  public void lazyloadToOneOfDeserializedEntity() {

    Customer newCustomer = new Customer("customer2");
    newCustomer.save();
    Order newOrder = new Order("order2");
    newOrder.setCustomer(newCustomer);
    newOrder.save();

    Order order2 = DB.find(Order.class).where().eq("name", "order2").findOne();

    try {
      Serialize(order2, "temp.txt");
      Order deserialize = DeSerialize("temp.txt");
      // NPE occured when lazy load customer.
      assertEquals("customer2", deserialize.getCustomer().getName());
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  private static void Serialize(Order order, String fileName) throws IOException {
    try (FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);) {
      objectOutputStream.writeObject(order);
      objectOutputStream.flush();
    }
  }

  private static Order DeSerialize(String fileName) throws IOException, ClassNotFoundException {
    try (FileInputStream fileInputStream = new FileInputStream(fileName);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);) {
      Order order = (Order) objectInputStream.readObject();

      return order;
    }

  }
}
