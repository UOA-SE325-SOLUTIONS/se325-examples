package uoa.se325.auction.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uoa.se325.auction.model.Bid;
import uoa.se325.auction.model.Item;
import uoa.se325.auction.model.User;
import uoa.se325.auction.repository.BidRepository;
import uoa.se325.auction.repository.ItemRepository;
import uoa.se325.auction.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BiddingService {

    private final ItemRepository itemRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;

    @Autowired
    public BiddingService(ItemRepository itemRepository, BidRepository bidRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
    }

    /**
     * Makes a bid for the given user on the given item, for the given amount. The bid must
     * be "after" all other bids for that item, AND must be for an amount greater than the current
     * greatest bid.
     *
     * @param userId the id of the user
     * @param itemId the id of the item
     * @param amount the amount to bid
     * @throws NotFoundException if the user or item aren't found in the database
     * @throws BiddingException  if the bid fails due to an insufficient amount or being "too late".
     */
    @Transactional
    public void makeBid(long userId, long itemId, long amount) throws NotFoundException, BiddingException {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findByIdForUpdate(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        Optional<Bid> latestExistingBid = bidRepository.findFirstByItemIdOrderByTimestampDesc(itemId);

        // If the amount is <= 0 we can't bid.
        if (amount <= 0) throw new BiddingException("Bid amount must be positive");

        Bid newBid = new Bid(item, user, amount);
        newBid.setItem(item);
        newBid.setBidder(user);

        // Check that the new bid is eligible, if there is an existing bid on the item
        if (latestExistingBid.isPresent()) {
            Bid existingBid = latestExistingBid.get();
            if (newBid.getAmount() <= existingBid.getAmount())
                throw new BiddingException("Bid amount must be greater than existing bids");

            if (newBid.getTimestamp().isBefore(existingBid.getTimestamp()) ||
                    newBid.getTimestamp().isEqual(existingBid.getTimestamp()))
                throw new BiddingException("Bid must be placed after latest existing bid");
        }

        // Add the bid to the item and then save the item - Can use this with the
        // OPTIMISTIC_FORCE_INCREMENT strategy for concurrency control. Even in this case,
        // just OPTIMISTIC isn't sufficient because the item table isn't changing - the
        // item's bids are represented by a join column on the bids table.
//        item.getBids().add(newBid);
//        itemRepository.save(item);

        // Save the bid by itself - can use this with the OPTIMISTIC_FORCE_INCREMENT
        // or the PESSIMISTIC_WRITE strategies for concurrency control (see
        // ItemRepository for more details)
        bidRepository.save(newBid);

        // Saving the item will update its version number, or throw OptimisticLockException
        // if the item is out-of-date, causing the transaction to rollback.
        // Can use this with OPTIMISTIC_FORCE_INCREMENT, in combination with the above line.
        itemRepository.save(item);
    }
}
