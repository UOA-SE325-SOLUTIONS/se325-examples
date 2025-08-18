package uoa.se325.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uoa.se325.auction.model.Bid;

import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

    /**
     * Gets the latest bid for the item with the given id.
     *
     * @param itemId the id of the item
     * @return an optional with the found bid, or empty.
     */
    Optional<Bid> findFirstByItemIdOrderByTimestampDesc(Long itemId);
}
