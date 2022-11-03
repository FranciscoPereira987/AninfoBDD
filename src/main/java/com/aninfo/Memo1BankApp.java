package com.aninfo;

import com.aninfo.exceptions.InvalidTransactionTypeException;
import com.aninfo.model.Account;
import com.aninfo.model.Transaction;
import com.aninfo.service.AccountService;
import com.aninfo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@SpringBootApplication
@EnableSwagger2
public class Memo1BankApp {

	@Autowired
	private AccountService accountService;

	@Autowired
	private TransactionService transactionService;

	public static void main(String[] args) {
		SpringApplication.run(Memo1BankApp.class, args);
	}

	@PostMapping("/accounts")
	@ResponseStatus(HttpStatus.CREATED)
	public Account createAccount(@RequestBody Account account) {
		return accountService.createAccount(account);
	}

	@GetMapping("/accounts")
	public Collection<Account> getAccounts() {
		return accountService.getAccounts();
	}

	@GetMapping("/accounts/{cbu}")
	public ResponseEntity<Account> getAccount(@PathVariable Long cbu) {
		Optional<Account> accountOptional = accountService.findById(cbu);
		return ResponseEntity.of(accountOptional);
	}

	@PutMapping("/accounts/{cbu}")
	public ResponseEntity<Account> updateAccount(@RequestBody Account account, @PathVariable Long cbu) {
		Optional<Account> accountOptional = accountService.findById(cbu);

		if (!accountOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		account.setCbu(cbu);
		accountService.save(account);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/accounts/{cbu}")
	public void deleteAccount(@PathVariable Long cbu) {
		accountService.deleteById(cbu);
	}

	@PostMapping("/accounts/deposit")
	@ResponseStatus(HttpStatus.CREATED)
	public Transaction newDeposit(@RequestBody Transaction transaction) {
		Optional<Account> account = accountService.findById(transaction.getDoerCbu());

		if(!account.isPresent()){
			throw new InvalidTransactionTypeException("Invalid deposit");
		}

		return transactionService.createDeposit(transaction, account.get());
	}

	@PostMapping("/accounts/{cbu}/withdrawal")
	@ResponseStatus(HttpStatus.CREATED)
	public Transaction newWithdrawal(@RequestBody Transaction transaction){
		Optional<Account> account = accountService.findById(transaction.getDoerCbu());

		if(!account.isPresent()){
			throw new InvalidTransactionTypeException("Invalid deposit");
		}

		return transactionService.createWithdrawal(transaction, account.get());
	}

	@GetMapping("/accounts/{cbu}/transactions")
	public Collection<Transaction> transactions(@PathVariable Long cbu) {
		return this.transactionService.getTransactions();
	}

	@GetMapping("/transactions/{transactionId}")
	public ResponseEntity<Transaction> getTransaction(@PathVariable Long transactionId){
		Optional<Transaction> optionalTransaction = this.transactionService.getTransaction(transactionId);

		return ResponseEntity.of(optionalTransaction);
	}

	@DeleteMapping("/transactions/{transactionId}")
	public void deleteTransaction(@PathVariable Long transactionId){
		throw new InvalidTransactionTypeException("Not implemented");
	}
	@Bean
	public Docket apiDocket() {
		return new Docket(DocumentationType.SWAGGER_2)
			.select()
			.apis(RequestHandlerSelectors.any())
			.paths(PathSelectors.any())
			.build();
	}
}
