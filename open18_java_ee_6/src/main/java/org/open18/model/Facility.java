package org.open18.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Facility generated by hbm2java
 */
@Entity
@Table(name = "FACILITY")
public class Facility implements java.io.Serializable {

	private Long id;
	private String name;
	private String description;
	private String type;
	private String address;
	private String city;
	private String state;
	private String zip;
	private String county;
	private String country;
	private String phone;
	private String uri;
	private Integer priceRange;
	private Set<Course> courses = new HashSet<Course>(0);
	private Golfer owner;

	public Facility() {
	}

	public Facility(String name, String type) {
		this.name = name;
		this.type = type;
	}
	public Facility(String name, String description, String type,
			String address, String city, String state, String zip,
			String county, String country, String phone, String uri,
			Integer priceRange, Set<Course> courses) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.county = county;
		this.country = country;
		this.phone = phone;
		this.uri = uri;
		this.priceRange = priceRange;
		this.courses = courses;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "NAME", nullable = false, length = 50)
	@NotNull
	@Max(50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "TYPE", nullable = false, length = 15)
	@NotNull
	@Max(15)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "ADDRESS", length = 50)
	@Max(50)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "CITY", length = 30)
	@Max(30)
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "STATE", length = 2)
	@Max(2)
	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Column(name = "ZIP", length = 5)
	@Max(5)
	public String getZip() {
		return this.zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	@Column(name = "COUNTY", length = 30)
	@Max(30)
	public String getCounty() {
		return this.county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	@Column(name = "COUNTRY", length = 30)
	@Max(30)
	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(name = "PHONE", length = 10)
	@Max(10)
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "URI")
	public String getUri() {
		return this.uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@Column(name = "PRICE_RANGE")
	public Integer getPriceRange() {
		return this.priceRange;
	}

	public void setPriceRange(Integer priceRange) {
		this.priceRange = priceRange;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "facility")
	public Set<Course> getCourses() {
		return this.courses;
	}

	public void setCourses(Set<Course> courses) {
		this.courses = courses;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER_ID", nullable = true)
	public Golfer getOwner() {
		return owner;
	}

	public void setOwner(Golfer owner) {
		this.owner = owner;
	}

}
