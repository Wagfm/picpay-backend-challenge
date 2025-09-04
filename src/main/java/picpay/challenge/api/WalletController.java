package picpay.challenge.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v0/wallets")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class WalletController {
    private final IWalletRepository walletRepository;

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody CreateWalletDTO dto) {
        Wallet wallet = new Wallet();
        wallet.setFullName(dto.fullName());
        wallet.setCpfCnpj(dto.cpfCnpj());
        wallet.setEmail(dto.email());
        wallet.setPassword(dto.password());
        wallet.setBalance(BigDecimal.ZERO);
        Wallet savedWallet = walletRepository.save(wallet);
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
        ServletUriComponentsBuilder.fromCurrentRequest().build().getPathSegments().forEach(builder::pathSegment);
        URI location = builder.path("/{id}").buildAndExpand(savedWallet.getId()).toUri();
        return ResponseEntity.created(location).body(savedWallet);
    }


    @PatchMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody TransferDTO dto) {
        Wallet payer = walletRepository.findById(dto.payer()).orElseThrow(RuntimeException::new);
        Wallet payee = walletRepository.findById(dto.payee()).orElseThrow(RuntimeException::new);
        payee.setBalance(payee.getBalance().add(dto.amount()));
        payer.setBalance(payer.getBalance().subtract(dto.amount()));
        walletRepository.saveAll(List.of(payer, payee));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/deposit/{id}")
    public ResponseEntity<Void> deposit(@PathVariable Long id, @RequestBody DepositDTO dto) {
        Wallet wallet = walletRepository.findById(id).orElseThrow(RuntimeException::new);
        wallet.setBalance(wallet.getBalance().add(dto.amount()));
        walletRepository.save(wallet);
        return ResponseEntity.ok().build();
    }
}
