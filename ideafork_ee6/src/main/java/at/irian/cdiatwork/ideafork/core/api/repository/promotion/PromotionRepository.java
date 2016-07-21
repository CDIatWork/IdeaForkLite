package at.irian.cdiatwork.ideafork.core.api.repository.promotion;

import at.irian.cdiatwork.ideafork.core.api.domain.idea.Idea;
import at.irian.cdiatwork.ideafork.core.api.domain.promotion.PromotionRequest;
import at.irian.cdiatwork.ideafork.core.api.domain.role.User;
import at.irian.cdiatwork.ideafork.core.api.monitoring.Monitored;
import at.irian.cdiatwork.ideafork.core.api.repository.change.EntityProcessor;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

@Monitored
@EntityProcessor

@Transactional
@Repository
public abstract class PromotionRepository implements EntityRepository<PromotionRequest, String> {
    @Inject
    private EntityManager entityManager;

    public List<PromotionRequest> loadRecentIdeaPromotions(User user, String searchHint) {
        if ("*".equalsIgnoreCase(searchHint)) {
            return entityManager.createQuery(
                    "select pr from PromotionRequest pr where pr.promotedAt is null and pr.ideaForPromotion.author.nickName <> :currentNick order by pr.createdAt desc",
                    PromotionRequest.class)
                    .setParameter("currentNick", user.getNickName())
                    .setFirstResult(0)
                    .setMaxResults(10)
                    .getResultList();
        }
        return entityManager.createQuery(
                "select pr from PromotionRequest pr where pr.promotedAt is null and pr.ideaForPromotion.author.nickName <> :currentNick and (pr.ideaForPromotion.topic like :searchText or pr.ideaForPromotion.category like :searchText) order by pr.createdAt desc",
                PromotionRequest.class)
                .setParameter("currentNick", user.getNickName())
                .setParameter("searchText", "%" + searchHint + "%")
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();
    }

    public void promoteIdea(PromotionRequest promotionRequest) {
        PromotionRequest storedPromotionRequest = this.entityManager.merge(promotionRequest);
        storedPromotionRequest.setPromotedAt(new Date());
    }

    public List<Idea> loadRecentlyPromotedIdeas(User user) {
        return entityManager.createQuery(
                "select pr.ideaForPromotion from PromotionRequest pr where pr.promotedAt is not null and pr.ideaForPromotion.author.nickName <> :currentNick order by pr.promotedAt desc",
                Idea.class)
                .setParameter("currentNick", user.getNickName())
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();
    }
}
