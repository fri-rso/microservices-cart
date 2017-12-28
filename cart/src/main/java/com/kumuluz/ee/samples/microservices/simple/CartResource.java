package com.kumuluz.ee.samples.microservices.simple;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.samples.microservices.simple.models.Cart;
import org.eclipse.microprofile.metrics.Histogram;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.eclipse.microprofile.metrics.annotation.Timed;


import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("/cart")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class CartResource {

    private static final Logger LOG = LogManager.getLogger(CartResource.class.getName());

    @PersistenceContext
    private EntityManager em;

    /**
     * Vrne seznam vseh artiklov v kosarici
     * */
    @Inject
    @Metric(name = "histogram_dodanih")
    Histogram histogram;

    @GET
    @Metered(name = "getCarts_meter")
    public Response getCarts() {
        LOG.trace("getCarts ENTRY");
        TypedQuery<Cart> query = em.createNamedQuery("Cart.findAll", Cart.class);

        List<Cart> carts = query.getResultList();
        histogram.update(carts.size());
        LOG.info("Stevilo vseh cartov: {}", carts.size());
        return Response.ok(carts).build();
    }

    /**
     * Pridobi posamezen artikel v kosarici glede na njegov id
     */

    @GET
    @Path("/{id}")
    @Timed(name = "getCart_timer")
    public Response getCart(@PathParam("id") Integer id) {
        LOG.trace("getCart ENTRY");
        Cart p = em.find(Cart.class, id);

        if (p == null) {
            return Response.status(Response.Status.NOT_FOUND).build();

        }
        LOG.info("Cart search ID: {}", p.getId());
        return Response.ok(p).build();
    }

    /**
     * Omogoča urejanje kosarice tako, da staremu profilu nastavi nove vrednosti
     */
    @POST
    @Path("/{id}")
    public Response editCart(@PathParam("id") Integer id, Cart cart) {
        LOG.trace("editCart ENTRY");
        Cart p = em.find(Cart.class, id);

        if (p == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        p.setStartDate(cart.getStartDate());
        p.setEndDate(cart.getEndDate());
        p.setHours(cart.getHours());
        p.setCreditCard(cart.getCreditCard());
        p.setProfileRef(cart.getProfileRef());

        em.getTransaction().begin();

        em.persist(p);

        em.getTransaction().commit();
        LOG.info("Uspesno urejen cart ID: {}", p.getId());
        return Response.status(Response.Status.CREATED).entity(p).build();
    }

    /**
     * Doda nov profil (Cart p)
     */
    @POST
    public Response createCart(Cart p) {
        LOG.trace("createCart ENTRY");
        p.setId(null);

        em.getTransaction().begin();

        em.persist(p);

        em.getTransaction().commit();
        LOG.info("Uspesno dodan cart ID: {}", p.getId());
        return Response.status(Response.Status.CREATED).entity(p).build();
    }

    /**
     * Vrne config info
     * */

    @Inject
    private CartProperties properties;

    @GET
    @Path("/config")
    public Response test() {
        LOG.trace("config ENTRY");
        String response =
                "{" +
                        "\"jndi-name\": \"%s\"," +
                        "\"connection-url\": \"%s\"," +
                        "\"username\": \"%s\"," +
                        "\"password\": \"%s\"," +
                        "\"max-pool-size\": %d" +
                        "}";

        response = String.format(
                response,
                properties.getJndiName(),
                properties.getConnectionUrl(),
                properties.getUsername(),
                properties.getPassword(),
                properties.getMaxPoolSize()
        );
        LOG.trace("config uspesen EXIT");
        return Response.ok(response).build();
    }
}
