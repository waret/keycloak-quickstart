package net.waret.demo.bravo.web.rest;

import net.waret.demo.bravo.domain.Contract;
import net.waret.demo.bravo.security.domain.UserDetails;
import net.waret.demo.bravo.web.util.ResponseUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class ContractResource {

    @RequestMapping(method = RequestMethod.GET, value = "/api/contracts")
    public List<Contract> getContracts(UserDetails userDetails) {
        Contract a = new Contract("a - called by user: " + createUserAppendix(userDetails));
        Contract b = new Contract("b - called by user: " + createUserAppendix(userDetails));

        List<Contract> contracts = new ArrayList<>();
        contracts.add(a);
        contracts.add(b);

        return contracts;
    }

    @GetMapping("/api/contract")
    public ResponseEntity<Contract> findContract() {
        return ResponseUtil.wrapOrNotFound(Optional.empty());
    }

    private String createUserAppendix(UserDetails userDetails) {
        return userDetails.getFullName() + " (id= " + userDetails.getId() + ")";
    }

}
