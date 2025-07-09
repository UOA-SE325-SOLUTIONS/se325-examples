package se325.examples.auction.onetoone;

import javax.persistence.*;

@Entity
public class Shipment_onetoone {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
	/*@JoinTable(
			name = "ITEM_SHIPMENT", 
			joinColumns = 
				@JoinColumn(name = "SHIPMENT_ID"), 
			inverseJoinColumns = 
				@JoinColumn(name = "ITEM_ID", 
							nullable = false, 
							unique = true))*/
    @JoinTable(
            name = "ITEM_SHIPMENT",
            joinColumns =
            @JoinColumn(name = "SHIPMENT_ID"),
            inverseJoinColumns =
            @JoinColumn(name = "ITEM_ID",
                    unique = true))
    private Item_onetoone item;

    protected Shipment_onetoone() {
    }

    public Shipment_onetoone(Item_onetoone item) {
        this.item = item;
    }
}
