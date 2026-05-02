package cafeteria.model;

/**
 * Category – Model class representing a menu category.
 *
 * Course: SW121 – Object Oriented Programming | Batch: K25SW
 */
public class Category {

    private int    categoryId;
    private String categoryName;
    private String description;

    public Category() {}

    public Category(int categoryId, String categoryName, String description) {
        this.categoryId   = categoryId;
        this.categoryName = categoryName;
        this.description  = description;
    }

    public int    getCategoryId()               { return categoryId; }
    public void   setCategoryId(int id)         { this.categoryId = id; }

    public String getCategoryName()             { return categoryName; }
    public void   setCategoryName(String n)     { this.categoryName = n; }

    public String getDescription()              { return description; }
    public void   setDescription(String d)      { this.description = d; }

    @Override
    public String toString() { return categoryName; }
}
