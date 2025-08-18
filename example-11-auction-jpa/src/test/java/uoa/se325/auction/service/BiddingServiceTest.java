package uoa.se325.auction.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uoa.se325.auction.model.Address;
import uoa.se325.auction.model.Bid;
import uoa.se325.auction.model.Item;
import uoa.se325.auction.model.User;
import uoa.se325.auction.repository.BidRepository;
import uoa.se325.auction.repository.ItemRepository;
import uoa.se325.auction.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BiddingServiceTest {

    @Autowired
    private BiddingService biddingService;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private long aliceId, bobId, itemId;

    @BeforeEach
    public void setUp() {
        bidRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();

        // Make some users to test
        Address aliceAddress = new Address("Alice", "Street", "12345");
        User alice = new User("alice", "Apples", "Alice");
        alice.setAddress(User.AddressType.SHIPPING, aliceAddress);
        alice.setAddress(User.AddressType.BILLING, aliceAddress);
        alice.setAddress(User.AddressType.HOME, aliceAddress);
        userRepository.save(alice);
        this.aliceId = alice.getId();
        Address bobAddress = new Address("Bob", "Street", "12345");
        User bob = new User("bob", "Bananas", "Bob");
        bob.setAddress(User.AddressType.SHIPPING, bobAddress);
        bob.setAddress(User.AddressType.BILLING, bobAddress);
        bob.setAddress(User.AddressType.HOME, bobAddress);
        userRepository.save(bob);
        this.bobId = bob.getId();

        // Make some items to test
        Item doll = new Item("Dragonite Plushie");
        itemRepository.save(doll);
        this.itemId = doll.getId();
    }

    @Test
    public void testContextLoads() {

    }

    /**
     * Tests a user can make a bid on an item.
     */
    @Test
    public void testMakeBid() {
        biddingService.makeBid(aliceId, itemId, 10);

        // Make sure there's a bid in the database
        Bid bid = bidRepository.findAll().stream().findFirst().orElse(null);
        assertNotNull(bid);
        assertEquals(10, bid.getAmount());
        assertEquals(aliceId, bid.getBidder().getId());
        assertEquals(itemId, bid.getItem().getId());
    }

    /**
     * Tests that a BiddingException is thrown if trying to make a negative bid
     */
    @Test
    public void testMakeBidNegative() {
        BiddingException ex = assertThrows(BiddingException.class,
                () -> biddingService.makeBid(aliceId, itemId, -1));

        assertEquals("Bid amount must be positive", ex.getMessage());
    }

    /**
     * Tests that a NotFoundException is thrown if trying to make a bid on a nonexistent item
     */
    @Test
    public void testMakeBidOnNonexistentItem() {
        assertThrows(NotFoundException.class,
                () -> biddingService.makeBid(aliceId, 10000, 1));
    }

    /**
     * Tests that a NotFoundException is thrown if trying to make a bid for a nonexistent user
     */
    @Test
    public void testMakeBidForNonexistentUser() {
        assertThrows(NotFoundException.class,
                () -> biddingService.makeBid(10000, itemId, 1));
    }

    /**
     * Tests that BiddingException is thrown when we aren't the latest bid for that item
     */
    @Test
    public void testMakeBidTooLate() {
        User user = userRepository.findById(aliceId).get();
        Item item = itemRepository.findById(itemId).get();
        Bid newBid = new Bid(item, user, 1);
        newBid.setTimestamp(LocalDateTime.now().plusHours(1));
        bidRepository.save(newBid);

        // Bid is made too late, "before" the one above, even though the amount
        // is higher.
        assertThrows(BiddingException.class,
                () -> biddingService.makeBid(bobId, itemId, 20));

        // Check only one bid in database
        assertEquals(1, bidRepository.count());
    }

    /**
     * Tests that BiddingException is thrown when we lowball a bid
     */
    @Test
    public void testMakeBidNotEnoughMoney() {
        User user = userRepository.findById(aliceId).get();
        Item item = itemRepository.findById(itemId).get();
        Bid newBid = new Bid(item, user, 100);
//        newBid.setTimestamp(LocalDateTime.now().plusHours(1));
        bidRepository.save(newBid);

        // Bid is made too late, "before" the one above, even though the amount
        // is higher.
        BiddingException ex = assertThrows(BiddingException.class,
                () -> biddingService.makeBid(bobId, itemId, 90));

        assertEquals("Bid amount must be greater than existing bids", ex.getMessage());

        // Check only one bid in database
        assertEquals(1, bidRepository.count());
    }

    /**
     * Tests that the item's version increments when making a bid.
     * <p>
     * Disable this test if not using OCC.
     */
    @Test
    public void testItemVersionIncrement() {

        biddingService.makeBid(aliceId, itemId, 10);

        Optional<Item> item = itemRepository.findByIdAndVersion(itemId, 1L);
        assertTrue(item.isPresent());
    }
}
