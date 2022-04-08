package real.example;

public interface AccountFactory {

	public abstract BankAccount getBankAccount(int id, double balance, String accountName);
}
