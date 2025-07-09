package se325.examples.auction.onetomany;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Table(name="ITEM_ONETOMANY")
public class Item_onetomany {
	@Id
	@GeneratedValue
	@Column(name="ID")
	private Long id;
	private String name;
	
	@OneToMany(cascade = CascadeType.PERSIST)
	@JoinColumn(name="ITEM_ID", nullable=false)
	private Set<Bid_onetomany> _bids = new HashSet<>();

	protected Item_onetomany() {}
	
	public Item_onetomany(String name) {
		this.name = name;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Iterator<Bid_onetomany> getBidsIterator() {
		return _bids.iterator();
	}
	
	public boolean containsBid(Bid_onetomany bid) {
		return _bids.contains(bid);
	}
	
	public void addBid(Bid_onetomany bid) {
		_bids.add(bid);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Item_onetomany))
            return false;
        if (obj == this)
            return true;

        Item_onetomany rhs = (Item_onetomany) obj;
        return new EqualsBuilder().
            append(id, rhs.getId()).
            append(name, rhs.getName()).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(id).
	            append(name).
	            toHashCode();
	}
}
