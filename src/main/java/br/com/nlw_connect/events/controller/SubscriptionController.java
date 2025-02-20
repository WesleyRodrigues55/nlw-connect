package br.com.nlw_connect.events.controller;

import br.com.nlw_connect.events.dto.ErrorMessage;
import br.com.nlw_connect.events.dto.SubscriptionResponse;
import br.com.nlw_connect.events.exception.EventNotFoundException;
import br.com.nlw_connect.events.exception.SubscriptionConflictException;
import br.com.nlw_connect.events.exception.UserIndicationNotFoundException;
import br.com.nlw_connect.events.model.Event;
import br.com.nlw_connect.events.model.Subscription;
import br.com.nlw_connect.events.model.User;
import br.com.nlw_connect.events.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping({
            "/subscription/{prettyName}",
            "/subscription/{prettyName}/{userId}"
    })
    public ResponseEntity<?> createSubscription(
            @PathVariable String prettyName,
            @RequestBody User subscriber,
            @PathVariable(required = false) Integer userId
    ) {
        try {
            SubscriptionResponse res = subscriptionService.createNewSubscription(prettyName, subscriber, userId);
            if (res != null) {
                return ResponseEntity.ok(res);
            }
        } catch (EventNotFoundException ex) {
            return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
        } catch (SubscriptionConflictException ex) {
            return ResponseEntity.status(409).body(new ErrorMessage(ex.getMessage()));
        } catch (UserIndicationNotFoundException ex) {
            return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/subscription/{prettyName}/ranking")
    public ResponseEntity<?> generateRankingByEvent(@PathVariable String prettyName) {
        try {
            return ResponseEntity.ok(subscriptionService.getCompleteRanking(prettyName).subList(0, 3));
        } catch (EventNotFoundException ex) {
            return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
        }
    }

    @GetMapping("/subscription/{prettyName}/ranking/{userId}")
    public ResponseEntity<?> generateRankingByEventAndUserId(
            @PathVariable String prettyName,
            @PathVariable Integer userId
    ) {

        try {
            return ResponseEntity.ok(subscriptionService.getRankingByUser(prettyName, userId));
        } catch (Exception ex) {
            return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
        }
    }
}
