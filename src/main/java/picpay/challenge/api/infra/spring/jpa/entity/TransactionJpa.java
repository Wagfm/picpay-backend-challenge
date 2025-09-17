package picpay.challenge.api.infra.spring.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import picpay.challenge.api.domain.enums.TransactionStatus;
import picpay.challenge.api.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
public class TransactionJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID transactionId;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID operationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_wallet_id", updatable = false)
    private WalletJpa sourceWallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_wallet_id", updatable = false)
    private WalletJpa destinationWallet;

    @Column(nullable = false, updatable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false, updatable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private ZonedDateTime timestamp;

    private TransactionJpa(Long id, UUID transactionId, UUID operationId, WalletJpa sourceWallet, WalletJpa destinationWallet, BigDecimal amount, TransactionStatus status, TransactionType transactionType, ZonedDateTime timestamp) {
        this.id = id;
        this.transactionId = transactionId;
        this.operationId = operationId;
        this.sourceWallet = sourceWallet;
        this.destinationWallet = destinationWallet;
        this.amount = amount;
        this.status = status;
        this.transactionType = transactionType;
        this.timestamp = timestamp;
    }
}
