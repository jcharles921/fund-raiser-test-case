package model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private String paymentMethod;
    
    @Column(nullable = false)
    private LocalDateTime transactionDate;
    
    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL)
    private Donation donation;

    public Transaction() {}

    public Transaction(BigDecimal amount, String paymentMethod, LocalDateTime transactionDate) {
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionDate = transactionDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
}
