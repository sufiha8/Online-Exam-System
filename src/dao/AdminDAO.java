
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.Admin;
import util.DatabaseConnection;
import util.PasswordUtil;

public class AdminDAO {

    public boolean register(String name, String email, String password)
            throws Exception {

        String cleanName = name == null ? "" : name.trim();
        String cleanEmail = email == null
                ? "" : email.trim().toLowerCase();

        if (cleanName.isBlank() || password == null
                || password.length() < 8) {
            throw new IllegalArgumentException(
                    "Enter a name and a password of at least 8 characters.");
        }

        if (!cleanEmail.matches("^[^\\s@]+@onlineexam\\.com$")) {
            throw new IllegalArgumentException(
                    "Admin email must end with @onlineexam.com.");
        }

        String sql =
                "INSERT INTO admins(name,email,password,is_active) VALUES(?,?,?,0)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, cleanName);
            p.setString(2, cleanEmail);
            p.setString(3, PasswordUtil.hash(password));

            return p.executeUpdate() == 1;
        }
    }

    public Admin login(String email, String password) throws Exception {
        if (email == null || password == null) {
            return null;
        }

        String sql =
                "SELECT admin_id,name,email,password "
              + "FROM admins WHERE email=? AND is_active=1";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, email.trim().toLowerCase());

            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) {
                    return null;
                }

                String storedPassword = r.getString("password");

                if (!PasswordUtil.verify(password, storedPassword)) {
                    return null;
                }

                // Upgrade an old plain-text password after successful login.
                if (!storedPassword.startsWith("PBKDF2:")) {
                    try (PreparedStatement update = c.prepareStatement(
                            "UPDATE admins SET password=? WHERE admin_id=?")) {
                        update.setString(1, PasswordUtil.hash(password));
                        update.setInt(2, r.getInt("admin_id"));
                        update.executeUpdate();
                    }
                }

                return new Admin(
                        r.getInt("admin_id"),
                        r.getString("name"),
                        r.getString("email"));
            }
        }
    }
}
