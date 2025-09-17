package picpay.challenge.api.infra.spring.controller;

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
import picpay.challenge.api.application.usecase.dto.CreateWalletDTO;
import picpay.challenge.api.application.usecase.dto.DepositDTO;
import picpay.challenge.api.application.usecase.dto.TransactionDTO;
import picpay.challenge.api.application.usecase.dto.TransferDTO;
import picpay.challenge.api.application.usecase.dto.WalletDTO;
import picpay.challenge.api.infra.spring.controller.dto.DepositRequestDTO;
import picpay.challenge.api.infra.spring.service.WalletService;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v0/wallets")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<WalletDTO> createWallet(@RequestBody CreateWalletDTO data) {
        WalletDTO wallet = walletService.createWallet(data);
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
        ServletUriComponentsBuilder.fromCurrentRequest().build().getPathSegments().forEach(builder::pathSegment);
        URI location = builder.path("/{id}").buildAndExpand(wallet.id()).toUri();
        return ResponseEntity.created(location).body(wallet);
    }

    @PatchMapping("/deposit/{id}")
    public ResponseEntity<TransactionDTO> deposit(@PathVariable UUID id, @RequestBody DepositRequestDTO data) {
        DepositDTO dto = new DepositDTO(id, data.amount());
        TransactionDTO output = walletService.deposit(dto);
        return ResponseEntity.ok(output);
    }

    @PatchMapping("/transfer")
    public ResponseEntity<TransactionDTO> transfer(@RequestBody TransferDTO data) {
        TransactionDTO dto = walletService.transfer(data);
        return ResponseEntity.ok(dto);
    }
}
