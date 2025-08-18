package uoa.se325.auction.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uoa.se325.auction.model.Item;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Finds an item with the given id, and eagerly fetches its bids.
     *
     * @param id the id of the item to fetch
     * @return the found item with its bids already eager fetched, or an empty optional if not found
     */
    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.bids WHERE i.id = :id")
    Optional<Item> findByIdEagerFetchBids(@Param("id") long id);

    /**
     * Lock strategies for this case:
     * OPTIMISTIC - Not sufficient for this case - because adding a bid doesn't directly change
     * the item table, since the join column is on the bid side.
     * <p>
     * OPTIMISTIC_FORCE_INCREMENT - when we save() this item it will only succeed if the version hasn't
     * changed. The item's version will ALWAYS increment when we save.
     * throwing OptimisticLockException otherwise. We can add a bid to this item, then save the item
     * to persist the bid, since we are cascading persistence for an item's bids collection. Or
     * we can persist the bid and then just save the item to increment the version.
     * <p>
     * PESSIMISTIC_WRITE - while the transaction is in progress, no other transactions can access
     * this item - they will block until the transaction completes.
     *
     * @param id the id of the item to return
     * @return the item with that id if it exists
     */
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT i FROM Item i WHERE i.id = :id")
    Optional<Item> findByIdForUpdate(@Param("id") Long id);

    Optional<Item> findByIdAndVersion(Long id, Long version);
}
