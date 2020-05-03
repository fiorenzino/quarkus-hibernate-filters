package ovh.flw.quarkusfilters.service.rs;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import ovh.flw.quarkusfilters.model.secondarytable.Gift;
import ovh.flw.quarkusfilters.model.secondarytable.GiftDetail;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/api/gifts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GiftServiceRs {

    @Context
    UriInfo ui;

    @SuppressWarnings("unchecked")
    private <T> T cast(String key, Class<T> clazz) {
        String value = ui.getQueryParameters().getFirst(key);
        if (Long.class.equals(clazz)) {
            return (T) Long.valueOf(value);
        }
        if (Integer.class.equals(clazz)) {
            return (T) Integer.valueOf(value);
        }
        if (Boolean.class.equals(clazz)) {
            return (T) Boolean.valueOf(value);
        }
        if (Double.class.equals(clazz)) {
            return (T) Double.valueOf(value);
        }
        if (LocalDate.class.equals(clazz)) {
            return (T) LocalDate.parse(value);
        }
        return (T) value;
    }

    private boolean nn(String key) {
        return ui.getQueryParameters().containsKey(key)
                && ui.getQueryParameters().getFirst(key) != null
                && !ui.getQueryParameters().getFirst(key).trim().isEmpty();
    }

    private String get(String key) {
        return ui.getQueryParameters().getFirst(key);
    }

    private List<Gift> paginate(PanacheQuery<Gift> search, Integer startRow, Integer pageSize) {
        long listSize = search.count();
        if (listSize == 0) {
            return new ArrayList<>();
        } else {
            int currentPage = 0;
            if (pageSize != 0) {
                currentPage = startRow / pageSize;
            } else {
                pageSize = Long.valueOf(listSize).intValue();
            }
            return search.page(Page.of(currentPage, pageSize)).list();
        }
    }

    @GET
    @Transactional
    public Response getList(
            @DefaultValue("0") @QueryParam("startRow") Integer startRow,
            @DefaultValue("10") @QueryParam("pageSize") Integer pageSize,
            @QueryParam("orderBy") String orderBy) {
        try {
            if (nn("name")) {
                Gift.enableFilter("likeName", "name", get("name") + "%");
            }
            if (nn("maxId")) {
                Gift.enableFilter("maxId", "maxId", cast("maxId", Long.class));
            }
            if (nn("minDate") && nn("maxDate")) {
                Gift.enableFilter("intervalDates",
                        Map.of(
                                "interval_minDate", cast("minDate", LocalDate.class),
                                "interval_maxDate", cast("maxDate", LocalDate.class)
                        )
                );
            }
            if (nn("id") && nn("gift_color")) {
                Gift.enableFilter("onDetail",
                        Map.of(
                                "id", cast("id", Long.class),
                                "gift_color", get("gift_color")
                        )
                );
            }
            PanacheQuery<Gift> search = Gift.findByFilters(orderBy);
            List<Gift> list = paginate(search, startRow, pageSize);
            return Response.ok(list).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }


    @GET
    @Path("/create")
    @Transactional
    public Response create() {
        Gift gift = new Gift();
        gift.name = "maglia 1";
        gift.localDate = LocalDate.now();
        gift.persist();
        GiftDetail giftDetail = new GiftDetail();
        giftDetail.gift_id = gift.id;
        giftDetail.gift_color = "RED";
        giftDetail.gift_model = "nike";
        giftDetail.gift_size = "M";
        giftDetail.persist();

        Gift gift2 = new Gift();
        gift2.name = "maglia 2";
        gift2.localDate = LocalDate.now();
        gift2.persist();
        GiftDetail giftDetail2 = new GiftDetail();
        giftDetail2.gift_id = gift2.id;
        giftDetail2.gift_color = "BLU";
        giftDetail2.gift_model = "lacoste";
        giftDetail2.gift_size = "L";
        giftDetail2.persist();

        Gift gift3 = new Gift();
        gift3.name = "maglia 3";
        gift3.localDate = LocalDate.now();
        gift3.persist();
        GiftDetail giftDetail3 = new GiftDetail();
        giftDetail3.gift_id = gift3.id;
        giftDetail3.gift_color = "BLACK";
        giftDetail3.gift_model = "adidas";
        giftDetail3.gift_size = "XL";
        giftDetail3.persist();

        return Response.ok().build();
    }

}
