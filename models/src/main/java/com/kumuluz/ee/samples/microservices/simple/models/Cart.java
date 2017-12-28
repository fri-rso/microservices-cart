package com.kumuluz.ee.samples.microservices.simple.models;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "carts")
@NamedQuery(name = "Cart.findAll", query = "SELECT p FROM Cart p")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date startDate;
    private Date endDate;
    private Integer hours;
    private Integer CreditCard;
    private Integer profileRef;

    /*Getters - Setters*/

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Integer getProfileRef() {
        return profileRef;
    }

    public void setProfileRef(Integer profileRef) {
        this.profileRef = profileRef;
    }

    public Integer getCreditCard() {
        return CreditCard;
    }

    public void setCreditCard(Integer creditCard) {
        CreditCard = creditCard;
    }
}