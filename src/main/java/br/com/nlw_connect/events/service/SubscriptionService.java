package br.com.nlw_connect.events.service;

import br.com.nlw_connect.events.dto.SubscriptionRankingByUser;
import br.com.nlw_connect.events.dto.SubscriptionRankingItem;
import br.com.nlw_connect.events.dto.SubscriptionResponse;
import br.com.nlw_connect.events.exception.EventNotFoundException;
import br.com.nlw_connect.events.exception.SubscriptionConflictException;
import br.com.nlw_connect.events.exception.UserIndicationNotFoundException;
import br.com.nlw_connect.events.model.Event;
import br.com.nlw_connect.events.model.Subscription;
import br.com.nlw_connect.events.model.User;
import br.com.nlw_connect.events.repository.EventRepo;
import br.com.nlw_connect.events.repository.SubscriptionRepo;
import br.com.nlw_connect.events.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepo subscriptionRepo;

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private UserRepo userRepo;

    public SubscriptionResponse createNewSubscription(
            String eventName,
            User user,
            Integer userId
    ) {
        Event evt = eventRepo.findByPrettyName(eventName);
        if (evt == null) {
            throw new EventNotFoundException("Event " + eventName + " not found");
        }

        User userRec = userRepo.findByEmail(user.getEmail());
        if (userRec == null) {
            userRec = userRepo.save(user);
        }

        // is here the problem with creation subscription without indication
        User indication = null;
        if (userId != null) {
            indication = userRepo.findById(userId).orElse(null);
            if (indication == null) {
                throw new UserIndicationNotFoundException("User indication " + userId + " not exists");
            }
        }

        Subscription subs = new Subscription();
        subs.setEvent(evt);
        subs.setSubscriber(userRec);
        subs.setIndication(indication);

        Subscription tmpSub = subscriptionRepo.findByEventAndSubscriber(evt, userRec);
        if (tmpSub != null) {
            throw new SubscriptionConflictException(
                    "There is already registration for user "
                            + userRec.getName() + " in event " + evt.getTitle());
        }

        Subscription res = subscriptionRepo.save(subs);
        return new SubscriptionResponse(
                res.getSubscriptionNumber(),
                "http://codecraft.com/subscription/"+res.getEvent().getPrettyName()+"/"+res.getSubscriber().getId()
        );
    }

    public List<SubscriptionRankingItem> getCompleteRanking(String prettyName) {
        Event event = eventRepo.findByPrettyName(prettyName);
        if (event == null) {
            throw new EventNotFoundException("Event ranking " + prettyName + " not found");
        }

        return subscriptionRepo.generateRanking(event.getEventId());
    }

    public SubscriptionRankingByUser getRankingByUser(String prettyName, Integer userId) {
        List<SubscriptionRankingItem> ranking = getCompleteRanking(prettyName);

        SubscriptionRankingItem item = ranking.stream()
                .filter(i->i.userId().equals(userId))
                .findFirst().orElse(null);

        if (item == null) {
            throw new UserIndicationNotFoundException("There are no sign-ups with user referral " + userId);
        }

        Integer position = IntStream.range(0, ranking.size())
                .filter(pos -> ranking.get(pos).userId().equals(userId))
                .findFirst().getAsInt();

        return new SubscriptionRankingByUser(item, position+1);
    }
}
