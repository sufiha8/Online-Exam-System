
package service;

import dao.AdminDAO;
import model.Admin;

public class AuthenticationService {
    private final AdminDAO adminDAO = new AdminDAO();

    public boolean registerAdmin(
            String name, String email, String password) throws Exception {

        if (name == null || name.isBlank()
                || email == null || email.isBlank()
                || password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                    "Enter all fields and use a password of at least 8 characters.");
        }

        return adminDAO.register(name, email, password);
    }

    public Admin loginAdmin(String email, String password) throws Exception {
        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Enter admin email and password.");
        }

        return adminDAO.login(email, password);
    }
}
