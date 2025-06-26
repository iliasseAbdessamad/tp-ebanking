package com.ebanking;

import com.ebanking.entities.AccountOperation;
import com.ebanking.entities.CurrentAccount;
import com.ebanking.entities.Customer;
import com.ebanking.entities.SavingAccount;
import com.ebanking.enums.AccountStatus;
import com.ebanking.enums.OperationType;
import com.ebanking.repositories.AccountOperationRepository;
import com.ebanking.repositories.BankAccountRepository;
import com.ebanking.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class EbankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(EbankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(CustomerRepository customerRepository,
											   BankAccountRepository bankAccountRepository,
											   AccountOperationRepository accountOperationRepository
	){
		return args -> {
			this.hydrateDatabase(customerRepository, bankAccountRepository, accountOperationRepository);
		};
	}

	//Hydrate database with some values (only for testing in a dev environment)
	private void hydrateDatabase(CustomerRepository customerRepository,
								 BankAccountRepository bankAccountRepository,
								 AccountOperationRepository accountOperationRepository
	){
		//Hydrates customers
		Stream.of("Ahmed", "Asmae", "Rachid").forEach(name -> {
			Customer customer = Customer.builder()
					.name(name)
					.email(name.concat("@gmail.com"))
					.build();

			customerRepository.save(customer);
		});

		//assocites a CurrentAccount and a SavingAccout to each Customer
		customerRepository.findAll().forEach(customer -> {
			CurrentAccount currentAccount = CurrentAccount.builder()
					.id(UUID.randomUUID().toString())
					.createdAt(new Date())
					.balance(10000 * Math.random())
					.overDraft(800)
					.customer(customer)
					.accountStatus(AccountStatus.ACTIVATED)
					.build();


			SavingAccount savingAccount = SavingAccount.builder()
					.id(UUID.randomUUID().toString())
					.createdAt(new Date())
					.balance(10000 * Math.random())
					.interestRate(5.5)
					.customer(customer)
					.accountStatus(AccountStatus.ACTIVATED)
					.build();

			bankAccountRepository.save(currentAccount);
			bankAccountRepository.save(savingAccount);
		});

		//associates 3 operations to each BankAccount
		bankAccountRepository.findAll().forEach(account -> {
			for(int i=0; i < 3; i++){
				AccountOperation accountOperation = AccountOperation.builder()
						.date(new Date())
						.amount(300 * Math.random())
						.description("Description de l'opération " + i)
						.operationType(Math.random() > 0.5 ? OperationType.DEPOSIT : OperationType.WITHDRAW)
						.bankAccount(account)
						.build();

				accountOperationRepository.save(accountOperation);
			}
		});
	}
}
