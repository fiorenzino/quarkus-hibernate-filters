package ovh.flw.quarkusfilters.service.rs.advanced;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import ovh.flw.api.AbstractServiceRs;
import ovh.flw.quarkusfilters.model.filterjointable.Account;
import ovh.flw.quarkusfilters.model.filterjointable.AccountType;
import ovh.flw.quarkusfilters.model.filterjointable.Client;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.util.Map;

@Path("/api/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientServiceRs extends AbstractServiceRs<Client> {
    @Override
    public PanacheQuery<Client> filter(UriInfo uriInfo, String orderBy) {
        if (nn("name")) {
            Client.enableFilter("likeName", "name", get("name") + "%");
        }
        if (nn("maxId")) {
            Client.enableFilter("maxId", "maxId", cast("maxId", Long.class));
        }
        if (nn("minDate") && nn("maxDate")) {
            Client.enableFilter("intervalDates",
                    Map.of(
                            "interval_minDate", cast("minDate", LocalDate.class),
                            "interval_maxDate", cast("maxDate", LocalDate.class)
                    )
            );
        }
        if (nn("maxOrderId")) {
            Client.enableFilter("firstAccounts", "maxOrderId", cast("maxOrderId", Integer.class));
        }
        if (nn("type")) {
            Client.enableFilter("accountType", "type", AccountType.valueOf(get("type")));
        }
        PanacheQuery<Client> search = Client.findByFilters(orderBy);
        return search;
    }


    @Path("/create")
    @GET
    @Transactional
    public Response create() {
        Client client = new Client();
        client.name = "Mario Rossi";
        client.created = LocalDate.now();
        client.addAccount(
                new Account()
                        .type(AccountType.CREDIT)
                        .amount(6000d)
                        .rate(1.25 / 100)
        ).addAccount(
                new Account()
                        .type(AccountType.DEBIT)
                        .amount(20d)
                        .rate(1.05 / 100)
        ).addAccount(
                new Account()
                        .type(AccountType.DEBIT)
                        .amount(2250d)
                        .rate(1.05 / 100)
        );
        client.persist();
        return Response.ok(client).build();
    }
}
