package ovh.flw.quarkusfilters.service.rs.advanced;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import ovh.flw.api.AbstractServiceRs;
import ovh.flw.quarkusfilters.model.onetomany.MobilePhone;
import ovh.flw.quarkusfilters.model.onetomany.Person;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/api/persons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonsServiceRs extends AbstractServiceRs<Person> {
    @Override
    public PanacheQuery<Person> filter(UriInfo uriInfo, String orderBy) {
        if (nn("lastName")) {
            Person.enableFilter("lastName", "lastName", "%" + get("lastName") + "%");
        }
        if (nn("tax_code")) {
            Person.enableFilter("tax_code", "tax_code", get("tax_code"));
        }
        if (nn("brand")) {
            Person.enableFilter("phones_by_brand", "brand", get("brand"));
        }
        if (nn("phone_model")) {
            Person.enableFilter("phones_by_phone_model", "phone_model", get("phone_model"));
        }
        if (nn("mobile_operator")) {
            Person.enableFilter("phones_by_mobile_operator", "mobile_operator", get("mobile_operator"));
        }
        PanacheQuery<Person> search = Person.findByFilters(orderBy);
        return search;
    }


    @Path("/create")
    @GET
    @Transactional
    public Response create() {
        Person person = new Person();
        person.firstName = "fiorenzo";
        person.lastName = "pizza";
        person.tax_code = "PZZFNZ75B11H769T";
        person
                .add(new MobilePhone()
                        .mobile_operator("TIM")
                        .phone_model("nokia")
                        .owner(person)
                        .brand("P20"))
                .add(new MobilePhone()
                        .mobile_operator("VODAFONE")
                        .phone_model("nokia")
                        .owner(person)
                        .brand("P21"))
                .add(new MobilePhone()
                        .mobile_operator("TRE")
                        .phone_model("nokia")
                        .owner(person)
                        .brand("P22"));
        person.persist();
        Person person2 = new Person();
        person2.firstName = "silvano";
        person2.lastName = "pizza";
        person2.tax_code = "PZZSVN73E11H769T";
        person2
                .add(new MobilePhone()
                        .mobile_operator("TIM")
                        .phone_model("nokia")
                        .owner(person2)
                        .brand("P20"))
                .add(new MobilePhone()
                        .mobile_operator("VODAFONE")
                        .phone_model("nokia")
                        .owner(person2)
                        .brand("P21"))
                .add(new MobilePhone()
                        .mobile_operator("TRE")
                        .phone_model("nokia")
                        .owner(person2)
                        .brand("P22"));
        person2.persist();
        return Response.ok().build();
    }
}
