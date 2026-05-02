package cafeteria.model;

/**
 * MenuItem – Model class representing a cafeteria menu item.
 *
 * Course: SW121 – Object Oriented Programming | Batch: K25SW
 */
public class MenuItem {

    private int     itemId;
    private String  itemName;
    private int     categoryId;
    private String  categoryName;
    private double  price;
    private String  description;
    private boolean isAvailable;

    // ── Constructors ─────────────────────────────────────────────────────────

    public MenuItem() {}

    public MenuItem(int itemId, String itemName, int categoryId,
                    String categoryName, double price,
                    String description, boolean isAvailable) {
        this.itemId       = itemId;
        this.itemName     = itemName;
        this.categoryId   = categoryId;
        this.categoryName = categoryName;
        this.price        = price;
        this.description  = description;
        this.isAvailable  = isAvailable;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public int     getItemId()                  { return itemId; }
    public void    setItemId(int id)            { this.itemId = id; }

    public String  getItemName()                { return itemName; }
    public void    setItemName(String n)        { this.itemName = n; }

    public int     getCategoryId()              { return categoryId; }
    public void    setCategoryId(int id)        { this.categoryId = id; }

    public String  getCategoryName()            { return categoryName; }
    public void    setCategoryName(String n)    { this.categoryName = n; }

    public double  getPrice()                   { return price; }
    public void    setPrice(double p)           { this.price = p; }

    public String  getDescription()             { return description; }
    public void    setDescription(String d)     { this.description = d; }

    public boolean isAvailable()                { return isAvailable; }
    public void    setAvailable(boolean a)      { this.isAvailable = a; }

    @Override
    public String toString() { return itemName; }
}
