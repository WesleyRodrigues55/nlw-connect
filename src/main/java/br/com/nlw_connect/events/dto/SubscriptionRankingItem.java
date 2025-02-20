package br.com.nlw_connect.events.dto;

public record SubscriptionRankingItem(
        Long subscribers,
        Integer userId,
        String name
) {
}
