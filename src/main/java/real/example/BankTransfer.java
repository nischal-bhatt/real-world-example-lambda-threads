package real.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class BankTransfer {
	public static void main(String[] args) {
		AccountFactory accountFactory = BankAccount::new;

		BankAccount studentBankAccount = accountFactory.getBankAccount(1, 50000, "StudentA");
		BankAccount universityBankAccount = accountFactory.getBankAccount(2, 100000, "University");

		BiPredicate<Double, Double> p1 = (balance, amount) -> balance > amount;
		BiConsumer<String, Double> printer = (x, y) -> System.out.println(x + y);
		BiConsumer<BankAccount, BankAccount> printer2 = (r, r2) -> System.out
				.println("ending balance of student account: " + studentBankAccount.getBalance() + "University account "
						+ universityBankAccount.getBalance());

		ExecutorService service = Executors.newFixedThreadPool(10);

		Thread t1 = new Thread(() -> {
			System.out.println(Thread.currentThread().getName() + " says :: Executing transfer");
			try {
				double amount = 1000;
				if (!p1.test(studentBankAccount.getBalance(), amount)) {
					printer.accept(Thread.currentThread().getName() + "says :: balance insufficient", amount);
					return;
				}

				while (!studentBankAccount.transfer(universityBankAccount, amount)) {
					TimeUnit.MILLISECONDS.sleep(100);
					continue;
				}
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			printer.accept(Thread.currentThread().getName() + " says transfer is successsful : Balance in account ",
					universityBankAccount.getBalance());
		});

		for (int i = 0; i < 30; i++) {
			service.submit(t1);
			System.out.println("printing iteration : " + i);
		}
		service.shutdown();

		try {
			while (!service.awaitTermination(24L, TimeUnit.HOURS)) {
				System.out.println("not yet. still waiting for termination");
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		printer2.accept(studentBankAccount, universityBankAccount);
	}

}
