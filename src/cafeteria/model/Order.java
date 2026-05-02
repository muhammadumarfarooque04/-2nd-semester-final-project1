package cafeteria.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Order – Model class representing a cafeteria order.
 *
 * Course: SW121 – Object Oriented Programming | Batch: K25SW
 */
public class Order {

    private int             orderId;
    private int             userId;
    private String          customerName;
    private Timestamp       orderDate;
    private double          totalAmount;
    private String          status;   // PENDING | PREPARING | READY | DELIVERED | CANCELLED
    private String          notes;
    private List<OrderItem> items;

    // ── Constructors ─────────────────────────────────────────────────────────

    public Order() {
        items = new ArrayList<>();
    }

    public Order(int orderId, int userId, String customerName,
                 Timestamp orderDate, double totalAmount,
                 String status, String notes) {
        this.orderId      = orderId;
        this.userId       = userId;
        this.customerName = customerName;
        this.orderDate    = orderDate;
        this.totalAmount  = totalAmount;
        this.status       = status;
        this.notes        = notes;
        this.items        = new ArrayList<>();
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public int             getOrderId()                      { return orderId; }
    public void            setOrderId(int id)                { this.orderId = id; }

    public int             getUserId()                       { return userId; }
    public void            setUserId(int id)                 { this.userId = id; }

    public String          getCustomerName()                 { return customerName; }
    public void            setCustomerName(String n)         { this.customerName = n; }

    public Timestamp       getOrderDate()                    { return orderDate; }
    public void            setOrderDate(Timestamp t)         { this.orderDate = t; }

    public double          getTotalAmount()                  { return totalAmount; }
    public void            setTotalAmount(double a)          { this.totalAmount = a; }

    public String          getStatus()                       { return status; }
    public void            setStatus(String s)               { this.status = s; }

    public String          getNotes()                        { return notes; }
    public void            setNotes(String n)                { this.notes = n; }

    public List<OrderItem> getItems()                        { return items; }
    public void            setItems(List<OrderItem> items)   { this.items = items; }

    public void addItem(OrderItem item) { items.add(item); }

    // ── Inner class ──────────────────────────────────────────────────────────

    /**
     * OrderItem – Represents a single line in an order.
     */
    public static class OrderItem {

        private int    orderItemId;
        private int    orderId;
        private int    itemId;
        private String itemName;
        private int    quantity;
        private double unitPrice;
        private double subtotal;

        public OrderItem() {}

        public OrderItem(int itemId, String itemName, int quantity, double unitPrice) {
            this.itemId    = itemId;
            this.itemName  = itemName;
            this.quantity  = quantity;
            this.unitPrice = unitPrice;
            this.subtotal  = quantity * unitPrice;
        }

        public int    getOrderItemId()              { return orderItemId; }
        public void   setOrderItemId(int id)        { this.orderItemId = id; }

        public int    getOrderId()                  { return orderId; }
        public void   setOrderId(int id)            { this.orderId = id; }

        public int    getItemId()                   { return itemId; }
        public void   setItemId(int id)             { this.itemId = id; }

        public String getItemName()                 { return itemName; }
        public void   setItemName(String n)         { this.itemName = n; }

        public int    getQuantity()                 { return quantity; }
        public void   setQuantity(int q)            { this.quantity = q; this.subtotal = q * unitPrice; }

        public double getUnitPrice()                { return unitPrice; }
        public void   setUnitPrice(double p)        { this.unitPrice = p; this.subtotal = quantity * p; }

        public double getSubtotal()                 { return subtotal; }
        public void   setSubtotal(double s)         { this.subtotal = s; }
    }
}
