package chapter01.item05;

class EncryptPassword {
    public String encrypt(String password) {
        return "hash_" + password;
    }
}

public class UserService {
    private EncryptPassword encryptPassword;

    public UserService() {
        this.encryptPassword = new EncryptPassword();
    }

    public void register(String username, String password) {
        String hashedPassword = encryptPassword.encrypt(password);
        System.out.println(username + ": " + hashedPassword);
    }
}
