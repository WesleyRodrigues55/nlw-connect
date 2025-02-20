package br.com.nlw_connect.events.dto;

public record SubscriptionRankingByUser(
        SubscriptionRankingItem item,
        Integer position
) {
}
