import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import controller.TransactionController;
import model.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public class TransactionControllerTest {

    private TransactionController transactionController;

    @Before
    public void setUp() {
        transactionController = new TransactionController();
    }

    @Test
    public void testCreateTransaction() {
        Optional<Transaction> transaction = transactionController.createTransaction(new BigDecimal("100.00"), "Credit Card", LocalDateTime.now());
        assertTrue(transaction.isPresent());
        assertEquals(new BigDecimal("100.00"), transaction.get().getAmount());
    }

    @Test
    public void testDeleteTransaction() {
        Optional<Transaction> transaction = transactionController.createTransaction(new BigDecimal("100.00"), "Credit Card", LocalDateTime.now());
        assertTrue(transaction.isPresent());
        Long transactionId = transaction.get().getId();

        boolean deleted = transactionController.deleteTransaction(transactionId);
        assertTrue(deleted);

        Optional<Transaction> deletedTransaction = transactionController.getTransactionById(transactionId);
        assertFalse(deletedTransaction.isPresent());
    }
}
