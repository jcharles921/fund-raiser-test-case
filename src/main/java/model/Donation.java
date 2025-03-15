package model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "donations")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "donor_id", nullable = false)
    private User donor;

    @ManyToOne
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @ManyToMany(mappedBy = "donations")
    private Set<User> users = new HashSet<>();

    // Constructor
    public Donation(BigDecimal amount, User donor, Campaign campaign, Transaction transaction) {
        this.amount = amount;
        this.donor = donor;
        this.campaign = campaign;
        this.transaction = transaction;
    }

    // Default constructor
    public Donation() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public User getDonor() { return donor; }
    public void setDonor(User donor) { this.donor = donor; }

    public Campaign getCampaign() { return campaign; }
    public void setCampaign(Campaign campaign) { this.campaign = campaign; }

    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }
}
