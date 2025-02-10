package chapter01.item05;

public class FlexibleUserService {
    private final EncryptPassword encryptPassword;

    public FlexibleUserService(EncryptPassword encryptPassword) {
        this.encryptPassword = encryptPassword;
    }

    public void register(String username, String password) {
        String hashedPassword = encryptPassword.encrypt(password);
        System.out.println(username + ": " + hashedPassword);
    }
}
