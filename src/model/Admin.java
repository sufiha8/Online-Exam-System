package model;

public class Admin {
    private int adminId;
    private String name;
    private String email;
    public Admin(int adminId, String name, String email) { this.adminId=adminId; this.name=name; this.email=email; }
    public int getAdminId() { return adminId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
